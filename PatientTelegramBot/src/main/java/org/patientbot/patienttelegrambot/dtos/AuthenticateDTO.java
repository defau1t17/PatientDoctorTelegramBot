package org.patientbot.patienttelegrambot.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticateDTO {
    @JsonProperty("chatID")
    private long chatID;
    @JsonProperty("token")
    private String token;
}
