package com.borisenko.moneytransfer.service;

import com.borisenko.moneytransfer.model.User;
import com.borisenko.moneytransfer.storage.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserServiceImpl implements UserService {
    private static final AtomicLong userIdGenerator = new AtomicLong(100);
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
    private Repository<Long, User> repository;

    public UserServiceImpl(Repository<Long, User> repository) {
        this.repository = repository;
    }

    @Override
    public long createUser(String firstName, String lastName, String email) {
        long userId = userIdGenerator.incrementAndGet();
        repository.create(new User(userId, firstName, lastName, email));
        return userId;
    }

    @Override
    public User updateUser(long id, String firstName, String lastName, String email) {
        return repository.update(new User(id, firstName, lastName, email));
    }

    @Override
    public Optional<User> getUserById(long id) {
        return repository.getById(id);
    }

    @Override
    public List<User> getAllUsers() {
        Iterable<User> users = repository.getAllEntities();
        return StreamSupport.stream(users.spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(long userId) {
        repository.delete(userId);
    }
}
