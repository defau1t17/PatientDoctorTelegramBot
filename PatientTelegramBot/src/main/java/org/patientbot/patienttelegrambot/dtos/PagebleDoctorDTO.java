package org.patientbot.patienttelegrambot.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class PagebleDoctorDTO {
    @JsonProperty("content")
    private List<DoctorDTO> content;
    @JsonProperty("totalPages")
    private int totalPages;

    @Override
    public String toString() {
        String result = "";
        for (DoctorDTO doctorDTO : content) {
            result = result
                    .concat(doctorDTO.toString()
                            .concat("\n--------------------------------------------"));
        }
        return result;
    }
}
