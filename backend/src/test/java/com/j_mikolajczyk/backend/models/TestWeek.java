package com.j_mikolajczyk.backend.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestWeek {

    @Test
    public void testCreateEmpty() {
        Week week = new Week();
        assertEquals(0, week.getDays().size());
    }

    @Test
    public void testCreateArgs() {
        List<String> selectedDays = new ArrayList<>();
        selectedDays.add("Monday");
        selectedDays.add("Wednesday");
        Week week = new Week(selectedDays);
        assertEquals(2, week.getDays().size());
        assertEquals("Monday", week.getDays().get(0).getName());
        assertEquals("Wednesday", week.getDays().get(1).getName());
    }

    @Test
    public void testSetDays() {
        Week week = new Week();
        List<Day> days = new ArrayList<>();
        days.add(new Day("Tuesday"));
        week.setDays(days);
        assertEquals(1, week.getDays().size());
        assertEquals("Tuesday", week.getDays().get(0).getName());
    }
    
}
