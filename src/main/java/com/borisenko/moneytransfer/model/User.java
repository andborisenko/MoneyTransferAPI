package com.borisenko.moneytransfer.model;

public class User implements PersistentEntity<Long> {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    public User() {
    }

    public User(Long userId, String firstName, String lastName, String email) {
        this.id = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public User(String firstName, String lastName, String email) {
        this.id = 0L;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public Long getId() {
        return id;
    }
}
