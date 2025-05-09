package com.j_mikolajczyk.backend.models;
import java.util.ArrayList;
import java.util.List;

public class Week {
    private List<Day> days;
    private List<String> selectedDays;

    public Week() {
        this.days = new ArrayList<Day>();
        this.selectedDays = new ArrayList<String>();
    }

    public Week(List<String> selectedDays) {
        this.days = new ArrayList<Day>();

        for (int i = 0; i < selectedDays.size(); i++) {
            days.add(new Day(selectedDays.get(i)));
        }
    }

    public List<Day> getDays() {
        return this.days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }
    
}
