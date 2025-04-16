package com.j_mikolajczyk.backend.requests;

public class RefreshRequest {

    private final String email;

    public RefreshRequest() {
        this.email = null;
    }

    public RefreshRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

    public String toString() {
        return "User " + email + " successfully refreshed.";
    }

}
