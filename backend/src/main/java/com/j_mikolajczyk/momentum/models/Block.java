package main.java.com.j_mikolajczyk.momentum.models;
import java.util.List;

public class Block {
    private String name;
    private List<Week> weeks;


    public Block() {
    }

    public Block(String name, List<Week> weeks) {
        this.name = name;
        this.weeks = weeks;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Week> getWeeks() {
        return this.weeks;
    }

    public void setWeeks(List<Week> weeks) {
        this.weeks = weeks;
    }
    
}
