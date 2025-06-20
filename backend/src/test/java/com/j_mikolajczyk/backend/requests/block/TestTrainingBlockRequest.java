package com.j_mikolajczyk.backend.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.context.SpringBootTest;

import com.j_mikolajczyk.backend.requests.TrainingBlockRequest;

import java.util.ArrayList;
import org.bson.types.ObjectId;


@SpringBootTest
public class TestTrainingBlockRequest {

    @Test
    public void testCreateEmpty() {
        TrainingBlockRequest trainingBlockRequest = new TrainingBlockRequest();
        assertNull(trainingBlockRequest.getBlockName());
        assertNull(trainingBlockRequest.getUserId());
    }

    @Test
    public void testCreateArgs() {
        ObjectId id = new ObjectId("680cfa09c0a0f6f066789cbe");
        TrainingBlockRequest trainingBlockRequest = new TrainingBlockRequest("blockname", id);
        assertEquals("blockname", trainingBlockRequest.getBlockName());
        assertEquals(id, trainingBlockRequest.getUserId());
    }
}
