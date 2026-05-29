package com.raven.dto.external;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmailQuality {

    @JsonProperty("is_disposable")
    private Boolean disposable;

}
