package com.j_mikolajczyk.backend.models;

import java.util.ArrayList;
import java.util.List;

public class Exercise {
    private String name;
    private List<Set> sets;


    public Exercise() {
        this.name = null;
        this.sets = new ArrayList<Set>();
    }

    public Exercise(String name) {
        this.name = name;
        this.sets = new ArrayList<Set>();
    }

    public Exercise(String name, List<Set> sets) {
        this.name = name;
        this.sets = sets;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Set> getSets() {
        return this.sets;
    }

    public void setSets(List<Set> sets) {
        this.sets = sets;
    }
    
}
