package org.patientbot.patienttelegrambot.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DoctorDTO {
    @JsonProperty("doctorPosition")
    String doctorPosition;
    @JsonProperty("workroom")
    private int workroom;
    @JsonProperty("doctorShift")
    private String doctorShift;
    @JsonProperty("shiftStatus")
    private String shiftStatus;
    @JsonProperty("id")
    private int id;
    @JsonProperty("totalPages")
    private int totalPages;
    @JsonProperty("user")
    private UserDTO user;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class UserDTO {
        @JsonProperty("name")
        private String name;
        @JsonProperty("secondName")
        private String secondName;
    }


    @Override
    public String toString() {
        return "\nid: [%s]\nname: [%s]\nsecond name: [%s]\nposition: [%s]\nshift: [%s]\nshift status: [%s]\nworkroom: [%s]"
                .formatted(this.id,
                        this.user.name,
                        this.user.secondName,
                        this.doctorPosition.toString(),
                        this.doctorShift.toString(),
                        this.shiftStatus.toString(),
                        this.workroom);
    }
}
