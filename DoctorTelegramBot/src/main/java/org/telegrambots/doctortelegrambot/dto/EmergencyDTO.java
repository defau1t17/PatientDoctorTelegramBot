package org.telegrambots.doctortelegrambot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmergencyDTO implements Serializable {
    @JsonProperty("chamberNumber")
    private int chamberNumber;
    @JsonProperty("chatID")
    private long chatID;
}
