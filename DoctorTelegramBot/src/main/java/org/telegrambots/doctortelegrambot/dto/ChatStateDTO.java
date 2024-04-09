package org.telegrambots.doctortelegrambot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatStateDTO {
    @JsonProperty("chatStates")
    private String chatStates;
    @JsonProperty("chatID")
    private long chatID;
}
