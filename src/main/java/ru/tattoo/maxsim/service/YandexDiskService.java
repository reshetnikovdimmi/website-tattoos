package ru.tattoo.maxsim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.tattoo.maxsim.exceptions.YandexDiskException;
import ru.tattoo.maxsim.model.DTO.DiskItem;
import ru.tattoo.maxsim.model.DTO.DiskResourcesResponse;
import ru.tattoo.maxsim.model.DTO.DiskUploadResponse;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class YandexDiskService {

    private static final String API_BASE = "https://cloud-api.yandex.net/v1/disk";

    private final RestTemplate restTemplate;

    public boolean testConnection(String token) {
        try {
            HttpEntity<?> entity = buildEntity(token);
            ResponseEntity<String> response = restTemplate.exchange(
                API_BASE, HttpMethod.GET, entity, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException.Unauthorized e) {
            log.warn("Yandex.Disk: неверный токен");
            return false;
        } catch (Exception e) {
            log.warn("Yandex.Disk: ошибка проверки — {}", e.getMessage());
            return false;
        }
    }

    public List<DiskItem> listFiles(String token, String path) {
        if (path == null || path.isBlank()) {
            path = "/";
        }

        String url = API_BASE + "/resources?path={path}&limit=200&sort=-created";

        try {
            HttpEntity<?> entity = buildEntity(token);
            ResponseEntity<DiskResourcesResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, DiskResourcesResponse.class, path);

            DiskResourcesResponse body = response.getBody();
            if (body == null || body.getEmbedded() == null
                || body.getEmbedded().getItems() == null) {
                return List.of();
            }
            return body.getEmbedded().getItems();

        } catch (HttpClientErrorException.NotFound e) {
            log.info("Yandex.Disk: папка '{}' ещё не создана", path);
            return List.of();
        } catch (HttpClientErrorException e) {
            log.error("Yandex.Disk API: {} — {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new YandexDiskException("Ошибка Яндекс.Диска: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Yandex.Disk: непредвиденная ошибка", e);
            throw new YandexDiskException("Ошибка Яндекс.Диска: " + e.getMessage(), e);
        }
    }

    public void delete(String token, String diskPath) {        String url = API_BASE + "/resources?path={path}&permanently=false";

        try {
            HttpEntity<?> entity = buildEntity(token);
            restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class, diskPath);
            log.info("Yandex.Disk: удалён {}", diskPath);
        } catch (HttpClientErrorException e) {
            log.error("Yandex.Disk: не удалось удалить {}", diskPath, e);
            throw new YandexDiskException("Не удалось удалить: " + e.getMessage(), e);
        }
    }

    public String getDownloadUrl(String token, String diskPath) {
        String url = API_BASE + "/resources/download?path={path}";

        try {
            HttpEntity<?> entity = buildEntity(token);
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class, diskPath);
            Map<?, ?> body = response.getBody();
            if (body == null || body.get("href") == null) {
                throw new YandexDiskException("Yandex.Disk не вернул URL скачивания");
            }
            return (String) body.get("href");
        } catch (HttpClientErrorException e) {
            log.error("Yandex.Disk: не удалось получить URL скачивания", e);
            throw new YandexDiskException("Ошибка получения URL скачивания: " + e.getMessage(), e);
        }
    }

    /**
     * Создаёт папку на Диске. Если она уже есть — Яндекс отвечает 409, это не ошибка.
     */
    public void createFolder(String token, String diskPath) {
        String url = API_BASE + "/resources?path={path}";

        try {
            HttpEntity<?> entity = buildEntity(token);
            restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class, diskPath);
            log.info("Yandex.Disk: создана папка {}", diskPath);
        } catch (HttpClientErrorException.Conflict e) {
            log.debug("Yandex.Disk: папка '{}' уже существует", diskPath);
        } catch (HttpClientErrorException e) {
            log.error("Yandex.Disk: не удалось создать папку {} — {}",
                diskPath, e.getResponseBodyAsString());
            throw new YandexDiskException(
                "Не удалось создать папку " + diskPath + ": " + e.getMessage(), e);
        }
    }

    /**
     * Загрузка файла в два шага, как требует API Диска:
     * 1) получаем href для загрузки, 2) PUT-им содержимое файла на этот href.
     * OAuth-токен нужен только на первом шаге.
     */
    public void uploadFile(String token, Path localFile, String diskPath) {
        if (!Files.isRegularFile(localFile)) {
            throw new YandexDiskException("Файл для загрузки не найден: " + localFile);
        }

        String url = API_BASE + "/resources/upload?path={path}&overwrite=true";

        try {
            HttpEntity<?> entity = buildEntity(token);
            ResponseEntity<DiskUploadResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, DiskUploadResponse.class, diskPath);

            DiskUploadResponse body = response.getBody();
            if (body == null || body.getHref() == null || body.getHref().isBlank()) {
                throw new YandexDiskException("Yandex.Disk не вернул URL загрузки для " + diskPath);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(Files.size(localFile));
            HttpEntity<FileSystemResource> upload =
                new HttpEntity<>(new FileSystemResource(localFile), headers);

            ResponseEntity<String> put = restTemplate.exchange(
                body.getHref(), HttpMethod.PUT, upload, String.class);

            if (!put.getStatusCode().is2xxSuccessful()) {
                throw new YandexDiskException(
                    "Загрузка " + diskPath + " вернула " + put.getStatusCode());
            }
            log.info("Yandex.Disk: загружен {} ({} байт)", diskPath, Files.size(localFile));

        } catch (HttpClientErrorException e) {
            log.error("Yandex.Disk: не удалось загрузить {} — {}",
                diskPath, e.getResponseBodyAsString());
            throw new YandexDiskException(
                "Не удалось загрузить " + diskPath + ": " + e.getMessage(), e);
        } catch (IOException e) {
            throw new YandexDiskException("Не удалось прочитать файл " + localFile, e);
        }
    }

    private HttpEntity<?> buildEntity(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "OAuth " + token);
        return new HttpEntity<>(headers);
    }
}
