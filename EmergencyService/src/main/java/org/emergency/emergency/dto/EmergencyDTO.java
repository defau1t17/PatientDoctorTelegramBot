package org.emergency.emergency.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmergencyDTO implements Serializable {

    private int chamberNumber;

    private long chatID;
}
