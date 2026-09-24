package ru.tattoo.maxsim.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {

    /** Директория для хранения загруженных файлов. */
    private String uploadDir = "uploads/images";

    /** URL-префикс, по которому раздаются файлы. */
    private String urlPrefix = "/images";

    /** Время кэширования статики, сек. */
    private Integer cacheSeconds = 3600;

    /** Максимальный размер файла, байт. */
    private long maxFileSize = 10_485_760;

    /** Разрешённые MIME-типы. */
    private List<String> allowedTypes = List.of(
        "image/jpeg", "image/png", "image/gif", "image/webp");

    public String getUploadDir() { return uploadDir; }
    public void setUploadDir(String uploadDir) { this.uploadDir = uploadDir; }

    public String getUrlPrefix() { return urlPrefix; }
    public void setUrlPrefix(String urlPrefix) { this.urlPrefix = urlPrefix; }

    public Integer getCacheSeconds() { return cacheSeconds; }
    public void setCacheSeconds(Integer cacheSeconds) { this.cacheSeconds = cacheSeconds; }

    public long getMaxFileSize() { return maxFileSize; }
    public void setMaxFileSize(long maxFileSize) { this.maxFileSize = maxFileSize; }

    public List<String> getAllowedTypes() { return allowedTypes; }
    public void setAllowedTypes(List<String> allowedTypes) { this.allowedTypes = allowedTypes; }
}
