package com.j_mikolajczyk.backend.requests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.j_mikolajczyk.backend.requests.CreateTrainingBlockRequest;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;


@SpringBootTest
public class TestCreateTrainingBlockRequest {

    @Test
    public void testCreateEmpty() {
        CreateTrainingBlockRequest request = new CreateTrainingBlockRequest();
        assertNull(request.getBlockName());
        assertNull(request.getUserId());
        assertNull(request.getSortedDays());
    }

    @Test
    public void testCreateArgs() {
        ObjectId id = new ObjectId();
        List<String> days = new ArrayList<String>();
        CreateTrainingBlockRequest request = new CreateTrainingBlockRequest("blockName", id, days);
        assertEquals("blockName", request.getBlockName());
        assertEquals(id, request.getUserId());
        assertEquals(days, request.getSortedDays());
    }

}