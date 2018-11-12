package com.borisenko.moneytransfer.storage;

import com.borisenko.moneytransfer.model.PersistentEntity;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class AbstractRepository<T, E extends PersistentEntity<T>> implements Repository<T, E> {
    private final Map<T, E> repository = new ConcurrentHashMap<>();

    @Override
    public void create(E entity) {
        repository.put(entity.getId(), entity);
    }

    @Override
    public E update(E entity) {
        return repository.replace(entity.getId(), entity);
    }

    @Override
    public Optional<E> getById(T id) {
        return Optional.ofNullable(repository.get(id));
    }

    @Override
    public void delete(T id) {
        repository.remove(id);
    }

    @Override
    public void deleteAll() {
        repository.clear();
    }

    @Override
    public Collection<E> getAllEntities() {
        return repository.values();
    }
}
