package com.j_mikolajczyk.backend.models;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "exercises")
public class Exercise {
    private String name;
    private String type;
    private String muscleWorked;
    private List<ExerciseSet> sets;


    public Exercise() {
    }

    public Exercise(String name, String type, String muscleWorked, List<ExerciseSet> sets) {
        this.name = name;
        this.type = type;
        this.muscleWorked = muscleWorked;
        this.sets = sets;
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

    public List<ExerciseSet> getSets() {
        return this.sets;
    }

    public void setSets(List<ExerciseSet> sets) {
        this.sets = sets;
    }
    
}
