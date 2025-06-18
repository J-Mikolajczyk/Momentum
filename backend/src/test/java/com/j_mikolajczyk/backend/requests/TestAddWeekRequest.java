package com.j_mikolajczyk.backend.requests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.j_mikolajczyk.backend.requests.AddWeekRequest;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestAddWeekRequest {

    @Test
    public void testCreateEmpty() {
        AddWeekRequest request = new AddWeekRequest();
        assertNull(request.getBlockName());
        assertNull(request.getUserId());
    }

    @Test
    public void testCreateWithArgs() {
        ObjectId id = new ObjectId();
        AddWeekRequest request = new AddWeekRequest("blockName", id);
        assertEquals("blockName", request.getBlockName());
        assertEquals(id, request.getUserId());
    }

}