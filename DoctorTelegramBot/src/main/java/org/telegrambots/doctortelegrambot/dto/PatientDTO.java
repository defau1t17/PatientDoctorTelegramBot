package org.telegrambots.doctortelegrambot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegrambots.doctortelegrambot.entities.PatientState;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientDTO {
    @JsonProperty
    private long id;
    @JsonProperty("disease")
    private String disease;
    @JsonProperty("patientState")
    private PatientState patientState;
    @JsonProperty("chamberNumber")
    private int chamberNumber;
    @JsonProperty("description")
    private String description;
    @JsonProperty("user")
    private UserDTO user;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserDTO {
        @JsonProperty("name")
        private String name;
        @JsonProperty("secondName")
        private String secondName;
    }

    @Override
    public String toString() {
        return "\nid : [%s]\nPatient : [%s %s]\nDisease : [%s]\nState : [%s]\nChamber : [%s]\nDescription : [%s]"
                .formatted(
                        this.id,
                        this.user.name,
                        this.user.secondName,
                        this.disease,
                        this.patientState,
                        this.chamberNumber,
                        this.description);
    }
}
