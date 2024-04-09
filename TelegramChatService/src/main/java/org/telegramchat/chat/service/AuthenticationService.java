package org.telegramchat.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.telegramchat.chat.entity.TelegramBotAuthentication;
import org.telegramchat.chat.repository.TelegramBotAuthenticationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService extends ServiceDAO<TelegramBotAuthentication, Integer> {

    private final TelegramBotAuthenticationRepository repository;

    @Transactional(readOnly = true)
    @Override
    public Page<TelegramBotAuthentication> findAll(int pageNumber, int pageSize) {
        return repository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TelegramBotAuthentication> findByChatID(Long chatID) {
        return repository.findTelegramBotAuthenticationByChatID(chatID);
    }

    @Override
    @Transactional
    public TelegramBotAuthentication create(Object object) {
        return repository.save((TelegramBotAuthentication) object);
    }

    @Transactional(readOnly = true)
    public Optional<TelegramBotAuthentication> findAuthenticationByToken(String token) {
        return repository.findTelegramBotAuthenticationByToken(token);
    }

    @Override
    @Transactional
    public TelegramBotAuthentication update(Object object) {
        return repository.save((TelegramBotAuthentication) object);
    }

    @Override
    @Transactional
    public void delete(Object object) {
        repository.delete((TelegramBotAuthentication) object);
    }

    @Override
    @Transactional
    public void deleteByChatID(Long chatID) {
        repository.deleteTelegramBotAuthenticationByChatID(chatID);
    }

    @Override
    public boolean validateBeforeSave(TelegramBotAuthentication object) {
        return findByChatID(object.getChatID()).isEmpty();
    }

    @Transactional
    public TelegramBotAuthentication authenticate(long chatID, TelegramBotAuthentication telegramBotAuthentication) {
        return update(telegramBotAuthentication.updateChatId(chatID));
    }
}
