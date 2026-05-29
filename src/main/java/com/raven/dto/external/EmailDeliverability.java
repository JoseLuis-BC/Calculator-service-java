package com.raven.dto.external;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmailDeliverability {

    private String status;

    @JsonProperty("is_format_valid")
    private Boolean formatValid;

    @JsonProperty("is_mx_valid")
    private Boolean mxValid;

}
