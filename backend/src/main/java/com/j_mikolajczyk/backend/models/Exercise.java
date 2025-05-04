package com.j_mikolajczyk.backend.models;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "exercises")
public class Exercise {
    private String name;
    private String type;
    private String muscleWorked;


    public Exercise() {
        this.name = null;
        this.type = null;
        this.muscleWorked = null;
    }

    public Exercise(String name, String type, String muscleWorked) {
        this.name = name;
        this.type = type;
        this.muscleWorked = muscleWorked;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMuscleWorked() {
        return this.muscleWorked;
    }

    public void setMuscleWorked(String muscleWorked) {
        this.muscleWorked = muscleWorked;
    }
    
}
