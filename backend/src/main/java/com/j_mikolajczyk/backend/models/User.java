package com.j_mikolajczyk.backend.models;

import java.util.List;

public class User {
    private String name;
    private String email;
    private List<Block> trainingBlocks;

    public User() {
    }

    public User(String name, String email, List<Block> trainingBlocks) {
        this.name = name;
        this.email = email;
        this.trainingBlocks = trainingBlocks;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Block> getTrainingBlocks() {
        return this.trainingBlocks;
    }

    public void setTrainingBlocks(List<Block> trainingBlocks) {
        this.trainingBlocks = trainingBlocks;
    }
    
}
