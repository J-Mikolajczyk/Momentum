package com.j_mikolajczyk.backend.dto;

import java.util.Map;

import org.bson.types.ObjectId;

import com.j_mikolajczyk.backend.models.User;

public class UserDTO {

    private ObjectId id;
    private String email;
    private String name;
    private Map<ObjectId, String> trainingBlockNameMap;

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.trainingBlockNameMap = user.getTrainingBlockNameMap();
    }

    public ObjectId getId() {
        return this.id;
    }
    
    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

    public Map<ObjectId, String> getTrainingBlockNameMap() {
        return this.trainingBlockNameMap;
    }


}
