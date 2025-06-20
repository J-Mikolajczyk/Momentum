package com.j_mikolajczyk.backend.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestSet {

    @Test
    public void testCreateEmpty() {
        Set set = new Set();
        assertEquals(0.0, set.getWeight());
        assertEquals(0, set.getReps());
        assertEquals(false, set.getLogged());
    }

    @Test
    public void testCreateArgs() {
        Set set = new Set(100.0, 10);
        assertEquals(100.0, set.getWeight());
        assertEquals(10, set.getReps());
        assertEquals(false, set.getLogged());
    }

    @Test
    public void testSetters() {
        Set set = new Set();
        set.setWeight(150.0);
        set.setReps(8);
        set.setLogged(true);
        assertEquals(150.0, set.getWeight());
        assertEquals(8, set.getReps());
        assertEquals(true, set.getLogged());
    }
    
}
