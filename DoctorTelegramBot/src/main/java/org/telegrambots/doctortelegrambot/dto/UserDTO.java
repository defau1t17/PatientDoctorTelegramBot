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
public class UserDTO {
    @JsonProperty("id")
    private int id;
    @JsonProperty("chatID")
    private long chatID;
    @JsonProperty("name")
    private String name;
    @JsonProperty("secondName")
    private String secondName;
}
