package org.telegramchat.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.telegramchat.chat.entity.ChatStates;
import org.telegramchat.chat.repository.ChatStateRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.telegramchat.chat.entity.ChatState;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class StateService extends ServiceDAO<ChatState, Integer> {

    private final ChatStateRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Page<ChatState> findAll(int pageNumber, int pageSize) {
        return repository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<ChatState> findByChatID(Long chatID) {
        return repository.findChatStateByChatID(chatID);
    }

    @Override
    @Transactional
    public ChatState create(Object object) {
        return repository.save((ChatState) object);
    }

    @Override
    @Transactional
    public ChatState update(Object object) {
        return repository.save((ChatState) object);
    }

    @Override
    @Transactional
    public void delete(Object object) {
        repository.delete((ChatState) object);
    }

    @Override
    @Transactional
    public void deleteByChatID(Long chatID) {
        repository.deleteChatStateByChatID(chatID);
    }

    @Override
    public boolean validateBeforeSave(ChatState object) {
        return findByChatID(object.getChatID()).isEmpty();
    }

    @Transactional
    public ChatState moveState(ChatState chatState, String moveDirection) {
        String commandReference = chatState.getChatStates().getCommandReference();
        System.out.println(moveDirection);
        switch (moveDirection) {
            case "next" -> {
                chatState.setChatStates(chatState.getChatStates().moveToNext(commandReference));
            }
            case "prev" -> {
                chatState.setChatStates(chatState.getChatStates().moveToPrevious(commandReference));
            }
            case "default" -> {
                chatState.setChatStates(ChatStates.DEFAULT);
            }
            default -> {

            }
        }
        return update(chatState);
    }

}
