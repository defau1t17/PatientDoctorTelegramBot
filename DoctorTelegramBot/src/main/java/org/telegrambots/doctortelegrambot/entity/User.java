package org.telegrambots.doctortelegrambot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@MappedSuperclass
public abstract class User {
    @OneToOne
    @JoinColumn(name = "token_id", referencedColumnName = "token_id")
    private Permission personalToken;
    @Column(name = "name")
    private String name;
    @Column(name = "second_name")
    private String secondName;

}
