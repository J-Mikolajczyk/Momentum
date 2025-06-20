package com.j_mikolajczyk.backend.models;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "training-blocks")
public class TrainingBlock {

    @Id
    private ObjectId id;
    private String name;
    private List<Week> weeks;
    private ObjectId createdByUserID;
    private List<String> selectedDays;
    private int mostRecentDayOpen;
    private int mostRecentWeekOpen;
    private boolean logged;


    public TrainingBlock() {
    }

    public TrainingBlock(String name, List<Week> weeks, ObjectId createdByUserID, List<String> selectedDays, boolean logged) {
        this.name = name;
        this.weeks = weeks;
        this.createdByUserID = createdByUserID;
        this.selectedDays = selectedDays;
        this.mostRecentDayOpen = 0;
        this.mostRecentWeekOpen = 0;
        this.logged = logged;
    }

    public TrainingBlock(String name, ObjectId createdByUserID, List<String> selectedDays, boolean logged) {
        this.name = name;
        this.weeks = new ArrayList<Week>();
        for (int i = 0; i < 4; i++) {
            this.weeks.add(new Week(selectedDays));
        }
        this.createdByUserID = createdByUserID;
        this.selectedDays = selectedDays;
        this.mostRecentDayOpen = 0;
        this.mostRecentWeekOpen = 0;
        this.logged = logged;
    }

    public ObjectId getId() {
        return this.id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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

    public ObjectId getCreatedByUserID() {
        return this.createdByUserID;
    }

    public void setCreatedByUserID(ObjectId createdByUserID) {
        this.createdByUserID = createdByUserID;
    }

    public List<String> getSelectedDays() {
        return this.selectedDays;
    }

    public void setSelectedDays(List<String> selectedDays) {
        this.selectedDays = selectedDays;
    }

    public int getMostRecentDayOpen() {
        return this.mostRecentDayOpen;
    }

    public void setMostRecentDayOpen(int mostRecentDayOpen) {
        this.mostRecentDayOpen = mostRecentDayOpen;
    }

    public int getMostRecentWeekOpen() {
        return this.mostRecentWeekOpen;
    }

    public void setMostRecentWeekOpen(int mostRecentWeekOpen) {
        this.mostRecentWeekOpen = mostRecentWeekOpen;
    }

    public boolean getLogged() {
        return this.logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }
    
}
