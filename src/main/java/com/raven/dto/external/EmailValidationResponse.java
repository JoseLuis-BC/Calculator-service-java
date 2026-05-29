package com.raven.dto.external;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmailValidationResponse {

    @JsonProperty("email_deliverability")
    private EmailDeliverability deliverability;

    @JsonProperty("email_quality")
    private EmailQuality quality;

}
