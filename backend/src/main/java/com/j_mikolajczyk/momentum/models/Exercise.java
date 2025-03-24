package main.java.com.j_mikolajczyk.momentum.models;
import java.util.List;
import java.util.Set;

public class Exercise {
    private String name;
    private String muscleWorked;
    private List<Set> sets;


    public Exercise() {
    }

    public Exercise(String name, String muscleWorked, List<Set> sets) {
        this.name = name;
        this.muscleWorked = muscleWorked;
        this.sets = sets;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMuscleWorked() {
        return this.muscleWorked;
    }

    public void setMuscleWorked(String muscleWorked) {
        this.muscleWorked = muscleWorked;
    }

    public List<Set> getSets() {
        return this.sets;
    }

    public void setSets(List<Set> sets) {
        this.sets = sets;
    }
    
}
