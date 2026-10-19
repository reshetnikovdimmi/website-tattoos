package ru.tattoo.maxsim.model.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiskItem {

    private String name;
    private String path;
    private String type;      // "file" | "dir"

    @JsonProperty("mime_type")
    private String mimeType;

    private Long size;
    private String created;
    private String modified;
}
