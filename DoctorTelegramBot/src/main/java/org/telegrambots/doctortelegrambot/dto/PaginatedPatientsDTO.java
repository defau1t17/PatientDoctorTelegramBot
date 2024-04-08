package org.telegrambots.doctortelegrambot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaginatedPatientsDTO {
    @JsonProperty("content")
    private List<PatientDTO> content;
    @JsonProperty("totalPages")
    private int totalPages;

    @Override
    public String toString() {
        String result = "";
        for (PatientDTO patientDTO : content) {
            result = result
                    .concat(patientDTO
                            .toString())
                    .concat("\n-----------------------------------------");
        }
        return result;
    }

}
