package com.j_mikolajczyk.backend.requests;

public class UserRequest {

    private final String email;

    public UserRequest() {
        this.email = null;
    }

    public UserRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

}
