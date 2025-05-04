package com.j_mikolajczyk.backend.models;
import java.util.ArrayList;
import java.util.List;

public class Day {
    private String name;
    private List<ExerciseSet> exerciseSets;


    public Day() {
        this.name = null;
        this.exerciseSets = new ArrayList<ExerciseSet>();
    }

    public Day(String name, List<ExerciseSet> exerciseSets) {
        this.name = name;
        this.exerciseSets = exerciseSets;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ExerciseSet> getExercises() {
        return this.exerciseSets;
    }

    public void setExercises(List<ExerciseSet> exerciseSets) {
        this.exerciseSets = exerciseSets;
    }
    
}
