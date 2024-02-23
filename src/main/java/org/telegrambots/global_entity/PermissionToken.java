package org.telegrambots.global_entity;

import jakarta.persistence.*;
import lombok.Data;
import org.telegrambots.global_service.TokenRepository;

import java.util.UUID;

@Data
@Entity
@Table(name = "tokens")
public final class PermissionToken {
    @Id
    @Column(name = "token_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "token")
    private UUID permissionToken;

    protected PermissionToken() {
        this.id = 0;
        this.permissionToken = generateToken();
    }

    public static PermissionToken tokenFabric(TokenRepository tokenRepository) {
        System.out.println("default token : " + new PermissionToken());
        return tokenRepository.save(new PermissionToken());
    }

    private UUID generateToken() {
        return UUID.randomUUID();
    }

}
