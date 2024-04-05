package org.telegramchat.chat.service;

import org.springframework.data.domain.Page;

import java.util.Optional;

public abstract class ServiceDAO<V, T> {

    public abstract Page<V> findAll(int pageNumber, int pageSize);

    public abstract Optional<V> findByChatID(Long chatID);

    public abstract V create(Object object);

    public abstract V update(Object object);

    public abstract void delete(Object object);

    public abstract void deleteByChatID(Long chatID);

    public abstract boolean validateBeforeSave(V object);

}
