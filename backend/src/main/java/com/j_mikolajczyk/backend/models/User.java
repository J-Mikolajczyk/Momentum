package com.j_mikolajczyk.backend.models;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document(collection = "users")
public class User {

    @Id
    private ObjectId id;
    private String email;
    private String password;
    private String name;

    @DocumentReference
    private List<Block> trainingBlocks;

    public User() {
    }

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.trainingBlocks = new ArrayList<Block>();
    }
    
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<Block> getTrainingBlocks() {
        return this.trainingBlocks;
    }

    public void setTrainingBlocks(List<Block> trainingBlocks) {
        this.trainingBlocks = trainingBlocks;
    }
    
}
