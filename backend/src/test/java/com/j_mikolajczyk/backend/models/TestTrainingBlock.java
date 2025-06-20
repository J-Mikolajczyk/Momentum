package com.j_mikolajczyk.backend.models;

import static org.junit.jupiter.api.Assertions.*;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class TestTrainingBlock {

    @Test
    public void testCreateEmpty() {
        TrainingBlock trainingBlock = new TrainingBlock();
        assertEquals(null, trainingBlock.getName());
        assertEquals(null, trainingBlock.getWeeks());
        assertEquals(null, trainingBlock.getCreatedByUserID());
        assertEquals(null, trainingBlock.getSelectedDays());
        assertEquals(-1, trainingBlock.getMostRecentDayOpen());
        assertEquals(-1, trainingBlock.getMostRecentWeekOpen());
        assertEquals(false, trainingBlock.getLogged());
    }

    @Test 
    public void testCreateArgs() {
        List<Week> weeks = new ArrayList<>();
        ObjectId userId = new ObjectId();
        List<String> selectedDays = new ArrayList<>();
        TrainingBlock trainingBlock = new TrainingBlock("Name", weeks, userId, selectedDays, false);
        assertEquals("Name", trainingBlock.getName());
        assertEquals(weeks, trainingBlock.getWeeks());
        assertEquals(userId, trainingBlock.getCreatedByUserID());
        assertEquals(selectedDays, trainingBlock.getSelectedDays());
        assertEquals(0, trainingBlock.getMostRecentDayOpen());
        assertEquals(0, trainingBlock.getMostRecentWeekOpen());
        assertEquals(false, trainingBlock.getLogged());
    }

    @Test
    public void testSetters() {
        TrainingBlock trainingBlock = new TrainingBlock();
        List<Week> weeks = new ArrayList<>();
        ObjectId userId = new ObjectId();
        List<String> selectedDays = new ArrayList<>();
        trainingBlock.setName("Name");
        trainingBlock.setWeeks(weeks);
        trainingBlock.setCreatedByUserID(userId);
        trainingBlock.setSelectedDays(selectedDays);
        trainingBlock.setMostRecentDayOpen(0);
        trainingBlock.setMostRecentWeekOpen(0);
        trainingBlock.setLogged(true);

        assertEquals("Name", trainingBlock.getName());
        assertEquals(weeks, trainingBlock.getWeeks());
        assertEquals(userId, trainingBlock.getCreatedByUserID());
        assertEquals(selectedDays, trainingBlock.getSelectedDays());
        assertEquals(0, trainingBlock.getMostRecentDayOpen());
        assertEquals(0, trainingBlock.getMostRecentWeekOpen());
        assertEquals(true, trainingBlock.getLogged());
    }
}
