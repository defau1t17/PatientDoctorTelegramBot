package org.telegrambots.doctortelegrambot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
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

    @Override
    public String toString() {
        return "\nid: [%s]\nname: [%s]\nsecond name: [%s]\nposition: [%s]\nshift: [%s]\nshift status: [%s]\nworkroom: [%s]"
                .formatted(this.id,
                        this.user.getName(),
                        this.user.getSecondName(),
                        this.doctorPosition.toString(),
                        this.doctorShift.toString(),
                        this.shiftStatus.toString(),
                        this.workroom);
    }
}
