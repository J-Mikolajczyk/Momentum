package com.j_mikolajczyk.backend.models;

import java.util.ArrayList;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {

    @Id
    private ObjectId id;
    private String email;
    private String password;
    private String name;
    private List<String> trainingBlockNames;

    public User() {
    }

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.trainingBlockNames = new ArrayList<String>();
    }

    public void updateBlockPosition(String name) {
        trainingBlockNames.remove(name);
        trainingBlockNames.add(0, name); 
    }

    public ObjectId getId() {
        return this.id;
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

    public List<String> getTrainingBlockNames() {
        return this.trainingBlockNames;
    }

    public void setTrainingBlockNames(List<String> trainingBlockNames) {
        this.trainingBlockNames = trainingBlockNames;
    }

    
    public void addBlock(String name) throws Exception {
        if(trainingBlockNames.contains(name)) {
            throw new Exception("409");
        }
        this.trainingBlockNames.add(0, name);
    }

    public void deleteBlock(String name) throws Exception {
        this.trainingBlockNames.remove(name);
    }
    
}
