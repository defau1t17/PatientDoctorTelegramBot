package org.telegrambots.doctortelegrambot.repository;

import java.util.List;
import java.util.Optional;

public abstract class EntityDAO<T, B> {

    public abstract List<T> findAll();

    public abstract Optional<T> findByID(B b);

    public abstract T create(Object object);

    public abstract T update(Object object);

    public abstract void delete(Object object);

    public abstract void deleteByID(B b);

}
