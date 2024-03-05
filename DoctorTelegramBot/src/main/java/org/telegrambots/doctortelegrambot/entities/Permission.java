package org.telegrambots.doctortelegrambot.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.telegrambots.doctortelegrambot.repositories.PermissionRepository;

import java.util.UUID;

@Data
@Entity
@Table(name = "tokens")
public final class Permission {
    @Id
    @Column(name = "token_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "chat_id")
    private int chatID;
    @Column(name = "token")
    private UUID permissionToken;

    protected Permission() {
        this.id = 0;
        this.chatID = -1;
        this.permissionToken = generateToken();
    }

    public static Permission tokenFabric(PermissionRepository permissionRepository) {
        return permissionRepository.save(new Permission());
    }

    private UUID generateToken() {
        return UUID.randomUUID();
    }

}
