package com.j_mikolajczyk.backend.models;
import java.util.ArrayList;
import java.util.List;

public class Week {
    private List<Day> days;

    public Week() {
        this.days = new ArrayList<Day>();
    }

    public Week(int dayAmount) {
        this.days = new ArrayList<Day>();

        for (int i = 0; i < dayAmount; i++) {
            days.add(new Day());
        }
    }

    public Week(List<Day> days) {
        this.days = days;
    }

    public List<Day> getDays() {
        return this.days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }
    
}
