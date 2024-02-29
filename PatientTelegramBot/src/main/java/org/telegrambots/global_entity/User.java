package org.telegrambots.global_entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@MappedSuperclass
public abstract class User {
    @Column(name = "chat_id")
    private int chatID;
    @OneToOne
    @JoinColumn(name = "token_id", referencedColumnName = "token_id")
    private PermissionToken personalToken;
    @Column(name = "name")
    private String name;
    @Column(name = "second_name")
    private String secondName;

}
