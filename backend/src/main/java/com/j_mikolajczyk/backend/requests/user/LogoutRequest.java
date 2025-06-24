package com.j_mikolajczyk.backend.requests.user;

public class LogoutRequest {

    private final String email;

    public LogoutRequest() {
        this.email = null;
    }

    public LogoutRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

}
