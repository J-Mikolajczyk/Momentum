package com.j_mikolajczyk.backend.models;

public class Set {
    private double weight;
    private int reps;


    public Set() {
    }

    public Set(double weight, int reps) {
        this.weight = weight;
        this.reps = reps;
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
    
}
