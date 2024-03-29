package org.telegramchat.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegramchat.chat.repository.TelegramBotAuthenticationRepository;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
public final class TelegramBotAuthentication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "token")
    private String token;
    @Column(name = "chat_id")
    private long chatID;

    public TelegramBotAuthentication() {
        this.chatID = -1;
        this.id = 0;
        this.token = UUID.randomUUID().toString();
    }

    public static TelegramBotAuthentication authenticationFabric(TelegramBotAuthenticationRepository repository) {
        return repository.save(new TelegramBotAuthentication());
    }

    public TelegramBotAuthentication updateChatId(long chatID) {
        this.chatID = chatID;
        return this;
    }
}
