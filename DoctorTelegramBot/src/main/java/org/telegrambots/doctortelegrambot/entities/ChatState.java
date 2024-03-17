package org.telegrambots.doctortelegrambot.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "states")
@NoArgsConstructor
@Data
public class ChatState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @JoinColumn(name = "chat_id", referencedColumnName = "chat_id", table = "tokens")
    private long chat_id;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "chat_state")
    private ChatStates chatStates;

    public ChatState(long chat_id) {
        this.id = 0;
        this.chat_id = chat_id;
        this.chatStates = ChatStates.DEFAULT;
    }
}
