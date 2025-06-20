package com.j_mikolajczyk.backend.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.j_mikolajczyk.backend.models.TrainingBlock;


@SpringBootTest
public class TestBlockDTO {

    @Test
    public void testBlockDTO() {
        ObjectId blockId = new ObjectId();
        ObjectId userId = new ObjectId();

        TrainingBlock block = new TrainingBlock("name", userId, new ArrayList<String>(), false);
        block.setId(blockId);
        block.setCreatedByUserID(userId);

        BlockDTO blockDTO = new BlockDTO(block);

        assertEquals(blockId.toHexString(), blockDTO.getId());
        assertEquals(block.getName(), blockDTO.getName());
        assertEquals(block.getCreatedByUserID().toHexString(), blockDTO.getCreatedByUserID());
        assertEquals(4, blockDTO.getWeeks().size());
        assertEquals(0, blockDTO.getMostRecentDayOpen());
        assertEquals(0, blockDTO.getMostRecentWeekOpen());
        assertEquals(false, blockDTO.getLogged());
    }
}
