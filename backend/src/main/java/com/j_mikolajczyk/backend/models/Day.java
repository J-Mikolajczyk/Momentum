package com.j_mikolajczyk.backend.models;
import java.util.ArrayList;
import java.util.List;

public class Day {
    private String name;
    private List<Exercise> exercises;
    private boolean logged;


    public Day() {
        this.name = null;
        this.exercises = new ArrayList<Exercise>();
        this.logged = false;
    }

    public Day(String name, List<Exercise> exercises) {
        this.name = name;
        this.exercises = exercises;
        this.logged = false;
    }

    public Day(String name) {
        this.name = name;
        this.exercises = null;
        this.logged = false;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Exercise> getExercises() {
        return this.exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    public boolean getLogged() {
        return this.logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }
    
}
