package com.j_mikolajczyk.backend.requests;

public class LoginRequest {

    private final String email;
    private final String password;

    public LoginRequest() {
        this.email = null;
        this.password = null;
    }

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String toString() {
        return "User " + email + " successfully logged in.";
    }

}
