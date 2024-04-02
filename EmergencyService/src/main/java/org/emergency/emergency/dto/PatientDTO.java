package org.emergency.emergency.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientDTO {
    @JsonProperty("id")
    private int id;
    @JsonProperty("chamberNumber")
    private int chamberNumber;
    @JsonProperty("patientState")
    private String patientState;
}
