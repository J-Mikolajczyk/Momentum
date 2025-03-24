package main.java.com.j_mikolajczyk.momentum.models;
import java.util.List;

public class Week {
    private List<Day> days;

    public Week() {
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
