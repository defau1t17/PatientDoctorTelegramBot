package org.hospital.hospitalservice.services;

import org.springframework.data.domain.Page;

import java.util.Optional;

public abstract class ServiceDAO<T, V> {

    public abstract Page<T> findAll(int pageNumber, int pageSize);

    public abstract T create(T entity);

    public abstract T update(T entity);

    public abstract void delete(T entity);

    public abstract void deleteByChatID(Long chatID);

    public abstract void deleteByID(V id);

    public abstract Optional<T> findByChatID(Long chatID);

    public abstract Optional<T> findByID(V id);

    public abstract boolean validateBeforeSave(T entity);
}
