package com.j_mikolajczyk.backend.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.j_mikolajczyk.backend.models.Day;
import com.j_mikolajczyk.backend.models.Exercise;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class TestDay {

    @Test
    public void testCreateEmpty() {
        Day day = new Day();
        assertEquals(null, day.getName());
    }

    @Test
    public void testCreateWithName() {
        Day day = new Day("Name");
        assertEquals("Name", day.getName());
    }

    @Test
    public void testCreateWithNameExercises() {
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(new Exercise("Push-ups"));
        Day day = new Day("Workout", exercises);

        assertEquals("Workout", day.getName());
        assertEquals(exercises, day.getExercises());
    }

}
