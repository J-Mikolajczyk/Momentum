package com.j_mikolajczyk.backend.requests;

import com.j_mikolajczyk.backend.models.User;

public class RegisterRequest {

    private final String email;
    private final String password;
    private final String name;

    private User user;

    public RegisterRequest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;

        this.user = new User(email, password, name);
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getName() {
        return this.name;
    }

    public User getUser() {
        return this.user;
    }

}
