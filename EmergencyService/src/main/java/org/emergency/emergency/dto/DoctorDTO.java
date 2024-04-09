package org.emergency.emergency.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {

    @JsonProperty("id")
    private long id;
    @JsonProperty("shiftStatus")
    private String shiftStatus;
    @JsonProperty("doctorPosition")
    private String doctorPosition;
    @JsonProperty("user")
    private UserDTO user;
}
