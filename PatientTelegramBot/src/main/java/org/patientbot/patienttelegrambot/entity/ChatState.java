package org.patientbot.patienttelegrambot.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ChatState {
    private int id;
    private long chat_id;
    private ChatStates chatStates;

    public ChatState(long chat_id) {
        this.id = 0;
        this.chat_id = chat_id;
        this.chatStates = ChatStates.DEFAULT;
    }

    public ChatState updateChatState(ChatStates chatStates) {
        this.chatStates = chatStates;
        return this;
    }

}
