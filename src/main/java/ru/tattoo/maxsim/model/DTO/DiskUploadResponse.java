package ru.tattoo.maxsim.model.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Ответ Яндекс.Диска на запрос URL для загрузки файла:
 * GET /v1/disk/resources/upload возвращает href, на который нужно сделать PUT.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiskUploadResponse {

    private String href;
    private String method;
    private Boolean templated;

    @JsonProperty("operation_id")
    private String operationId;
}
