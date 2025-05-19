package com.j_mikolajczyk.backend.models;

public class Set {
    private double weight;
    private int reps;
    private boolean logged;

    public Set() {
        this.weight = 0.0;
        this.reps = 0;
        this.logged = false;
    }

    public Set(double weight, int reps) {
        this.weight = weight;
        this.reps = reps;
        this.logged = false;
    }

    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getReps() {
        return this.reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public boolean getLogged() {
        return this.logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }
    
}
