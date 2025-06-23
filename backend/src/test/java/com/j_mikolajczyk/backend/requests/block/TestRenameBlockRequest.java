package com.j_mikolajczyk.backend.requests.block;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.context.SpringBootTest;

import com.j_mikolajczyk.backend.requests.block.RenameBlockRequest;

import java.util.ArrayList;
import org.bson.types.ObjectId;


@SpringBootTest
public class TestRenameBlockRequest {

    @Test
    public void testCreateEmpty() {
        RenameBlockRequest renameBlockRequest = new RenameBlockRequest();
        assertNull(renameBlockRequest.getBlockName());
        assertNull(renameBlockRequest.getNewName());
        assertNull(renameBlockRequest.getUserId());
    }

    @Test
    public void testCreateArgs() {
        ObjectId id = new ObjectId("680cfa09c0a0f6f066789cbe");
        RenameBlockRequest renameBlockRequest = new RenameBlockRequest("blockname", "newName", id);
        assertEquals("blockname", renameBlockRequest.getBlockName());
        assertEquals("newName", renameBlockRequest.getNewName());
        assertEquals(id, renameBlockRequest.getUserId());
    }
}
