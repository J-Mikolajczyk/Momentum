package com.j_mikolajczyk.backend.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestExercise {
    
    @Test
    public void testCreateEmpty() {
        Exercise exercise = new Exercise();
        assertEquals(null, exercise.getName());
        assertEquals(0, exercise.getSets().size());
    }

    @Test
    public void testCreateWithName() {
        Exercise exercise = new Exercise("Push-ups");
        assertEquals("Push-ups", exercise.getName());
        assertEquals(0, exercise.getSets().size());
    }

    @Test
    public void testCreateWithNameAndSets() {
        List<Set> sets = new ArrayList<>();
        sets.add(new Set(100.0, 10));
        Exercise exercise = new Exercise("Bench Press", sets);
        assertEquals("Bench Press", exercise.getName());
        assertEquals(sets, exercise.getSets());
    }

    @Test
    public void testSetters() {
        Exercise exercise = new Exercise();
        List<Set> sets = new ArrayList<>();
        sets.add(new Set(50.0, 12));
        exercise.setName("Squats");
        exercise.setSets(sets);
        assertEquals("Squats", exercise.getName());
        assertEquals(sets, exercise.getSets());
    }

}
