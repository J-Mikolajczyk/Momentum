package com.j_mikolajczyk.backend.requests.block;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.context.SpringBootTest;

import com.j_mikolajczyk.backend.requests.block.UpdateBlockRequest;
import com.j_mikolajczyk.backend.models.Week;

import java.util.ArrayList;

@SpringBootTest
public class TestUpdateBlockRequest {

    @Test
    public void testCreateEmpty() {
        UpdateBlockRequest updateBlockRequest = new UpdateBlockRequest();
        assertNull(updateBlockRequest.getId());
        assertNull(updateBlockRequest.getName());
        assertNull(updateBlockRequest.getWeeks());
        assertEquals(-1, updateBlockRequest.getWeekIndex());
        assertEquals(-1, updateBlockRequest.getDayIndex());
    }

    @Test
    public void testCreateArgs() {
        ArrayList<Week> weeks = new ArrayList<Week>();
        UpdateBlockRequest updateBlockRequest = new UpdateBlockRequest("680cfa09c0a0f6f066789cbe", "name", weeks, 0, 0);
        assertEquals("680cfa09c0a0f6f066789cbe", updateBlockRequest.getId().toHexString());
        assertEquals("name", updateBlockRequest.getName());
        assertEquals(weeks, updateBlockRequest.getWeeks());
        assertEquals(0, updateBlockRequest.getWeekIndex());
        assertEquals(0, updateBlockRequest.getDayIndex());
    }
}
