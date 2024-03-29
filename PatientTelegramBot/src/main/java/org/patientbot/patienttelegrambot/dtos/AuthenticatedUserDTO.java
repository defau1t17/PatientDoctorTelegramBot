package org.patientbot.patienttelegrambot.dtos;

import lombok.Data;

@Data
public class AuthenticatedUserDTO {

    private String name;

    private String secondName;
}
