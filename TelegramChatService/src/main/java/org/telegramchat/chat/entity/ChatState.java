package org.telegramchat.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(value = EnumType.STRING)
    private ChatStates chatStates;
    @Column(name = "chat_id")
    private long chatID;

    public ChatState(long chatID) {
        this.id = 0;
        this.chatID = chatID;
        this.chatStates = ChatStates.DEFAULT;
    }

    public ChatState updateChatState(ChatStates chatStates) {
        this.chatStates = chatStates;
        return this;
    }
}
