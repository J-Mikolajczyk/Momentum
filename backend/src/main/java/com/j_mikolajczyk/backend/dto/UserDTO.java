package com.j_mikolajczyk.backend.dto;

import java.util.List;

import org.bson.types.ObjectId;

import com.j_mikolajczyk.backend.models.User;

public class UserDTO {

    private ObjectId id;
    private String email;
    private String name;
    private List<ObjectId> trainingBlockIds;

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.trainingBlockIds = user.getTrainingBlockIds();
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

    public List<ObjectId> getTrainingBlockIds() {
        return this.trainingBlockIds;
    }


}
