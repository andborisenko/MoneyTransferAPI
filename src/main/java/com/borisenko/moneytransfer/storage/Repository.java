package com.borisenko.moneytransfer.storage;

import com.borisenko.moneytransfer.model.PersistentEntity;

import java.util.Optional;

public interface Repository<T,E extends PersistentEntity<T>> {

    void create(E entity);

    E update(E entity);

    Optional<E> getById(T id);

    Iterable<E> getAllEntities();

    void delete(T id);

    void deleteAll();
}

