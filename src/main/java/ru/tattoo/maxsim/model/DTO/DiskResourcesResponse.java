package ru.tattoo.maxsim.model.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiskResourcesResponse {

    @JsonProperty("_embedded")
    private Embedded embedded;

    private Integer limit;
    private Integer offset;
    private Integer total;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Embedded {
        private List<DiskItem> items;
    }
}
