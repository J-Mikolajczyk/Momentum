package com.j_mikolajczyk.backend.dto;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.j_mikolajczyk.backend.models.User;

public class UserDTO {

    private String id;
    private String email;
    private String name;
    private List<String> trainingBlockNames;

    public UserDTO(User user) {
        this.id = user.getId().toString();
        this.email = user.getEmail();
        this.name = user.getName();
        this.trainingBlockNames = user.getTrainingBlockNames();
    }

    public String getId() {
        return this.id;
    }
    
    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getTrainingBlockNames() {
        return this.trainingBlockNames;
    }


}
