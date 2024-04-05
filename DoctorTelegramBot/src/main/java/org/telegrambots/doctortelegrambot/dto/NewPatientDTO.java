package org.telegrambots.doctortelegrambot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegrambots.doctortelegrambot.entities.PatientState;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewPatientDTO {
    @JsonProperty("name")
    private String name;
    @JsonProperty("secondName")
    private String secondName;
    @JsonProperty("disease")
    private String disease;
    @JsonProperty("patientState")
    private PatientState patientState;
    @JsonProperty("chamberNumber")
    private int chamberNumber;
    @JsonProperty("description")
    private String description;
    @JsonProperty("token")
    private String token;


    @Override
    public String toString() {
        return "\nName: [%s]\nSecond name: [%s]\nDisease: [%s]\nState: [%s]\nChamber: [%s]\nDescription: [%s]\nToken: [%s]"
                .formatted(
                        this.name,
                        this.secondName,
                        this.disease,
                        this.patientState,
                        this.chamberNumber,
                        this.description,
                        this.token
                );
    }

}
