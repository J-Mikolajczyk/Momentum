package com.j_mikolajczyk.backend.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestUser {
    
    @Test
    public void testCreateEmpty() {
        User user = new User();
        assertEquals(null, user.getEmail());
        assertEquals(null, user.getPassword());
        assertEquals(null, user.getName());
        assertEquals(0, user.getTrainingBlockNames().size());
    }

    @Test
    public void testCreateArgs() {
        User user = new User("test@example.com", "password123", "Test User");
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("Test User", user.getName());
        assertEquals(0, user.getTrainingBlockNames().size());
    }

    @Test
    public void testSetters() {
        User user = new User();
        List<String> trainingBlockNames = new ArrayList<>();
        user.setEmail("new@example.com");
        user.setPassword("newpassword");
        user.setName("New Name");
        user.setTrainingBlockNames(trainingBlockNames);
        assertEquals("new@example.com", user.getEmail());
        assertEquals("newpassword", user.getPassword());
        assertEquals("New Name", user.getName());
        assertEquals(trainingBlockNames, user.getTrainingBlockNames());
    }

    @Test
    public void testAddBlock() throws Exception {
        User user = new User();
        user.addBlock("Block 1");
        assertEquals("Block 1", user.getTrainingBlockNames().get(0));
    }

    @Test
    public void testDeleteBlock() {
        User user = new User();
        try {
            user.addBlock("Block 1");
            user.deleteBlock("Block 1");
        } catch (Exception e) {
            fail("Exception was thrown");
        }
        assertEquals(0, user.getTrainingBlockNames().size());
    }

    @Test
    public void testRenameBlock() {
        User user = new User();
        try {
            user.addBlock("Block 1");
            user.renameBlock("Block 1", "New Name");
        } catch (Exception e) {
            fail("Exception was thrown");
        }
        assertEquals("New Name", user.getTrainingBlockNames().get(0));
    }

    @Test
    public void testRenameBlockNonExistent() {
        User user = new User();
        try {
            user.addBlock("Block 1");        
            user.renameBlock("Block 2", "Block 1");
        } catch (Exception e) {
            assertEquals("404", e.getMessage());
        }
    }

    @Test
    public void testRenameBlockDuplicate() {
        User user = new User();
        try {
            user.addBlock("Block 1");
            user.addBlock("Block 2");
            user.renameBlock("Block 2", "Block 1");
        } catch (Exception e) {
            assertEquals("409", e.getMessage());
        }
    }


    @Test
    public void testAddBlockDuplicate() {
        User user = new User();
        try {
            user.addBlock("Block 1");
            user.addBlock("Block 1");
            fail("Expected Exception was not thrown");
        } catch (Exception e) {
            assertEquals("409", e.getMessage());
        }
    }

    @Test
    public void testUpdateBlockPosition() throws Exception {
        User user = new User();
        try {
            user.addBlock("Block 1");
            user.addBlock("Block 2");
            user.addBlock("Block 3");
        } catch (Exception e) {
            fail("Exception was thrown");
        }
        
        user.updateBlockPosition("Block 2");
        assertEquals("Block 2", user.getTrainingBlockNames().get(0));
    }

}
