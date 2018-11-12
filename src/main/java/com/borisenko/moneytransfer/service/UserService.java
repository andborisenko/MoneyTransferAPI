package com.borisenko.moneytransfer.service;

import com.borisenko.moneytransfer.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    long createUser(String firstName, String lastName, String email);

    User updateUser(long id, String firstName, String lastName, String email);

    Optional<User> getUserById(long id);

    List<User> getAllUsers();

    void deleteUser(long userId);
}
