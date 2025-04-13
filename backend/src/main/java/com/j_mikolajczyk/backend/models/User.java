package com.j_mikolajczyk.backend.models;

import java.util.HashMap;
import java.util.Map;

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
    private Map<ObjectId, String> trainingBlockNameMap;

    public User() {
    }

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.trainingBlockNameMap = new HashMap<ObjectId, String>();
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

    public Map<ObjectId, String> getTrainingBlockNameMap() {
        return this.trainingBlockNameMap;
    }

    public void setTrainingBlockNameMap(HashMap<ObjectId, String> trainingBlockNameMap) {
        this.trainingBlockNameMap = trainingBlockNameMap;
    }

    
    public void addBlock(ObjectId id, String name) {
        this.trainingBlockNameMap.put(id, name);
    }
    
}
