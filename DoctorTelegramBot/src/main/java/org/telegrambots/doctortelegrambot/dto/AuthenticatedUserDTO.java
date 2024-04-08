package org.telegrambots.doctortelegrambot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticatedUserDTO {

    private String name;

    private String secondName;
}
