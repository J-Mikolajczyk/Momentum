package com.j_mikolajczyk.backend.dto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import org.bson.types.ObjectId;

import com.j_mikolajczyk.backend.dto.UserDTO;
import com.j_mikolajczyk.backend.models.User;

@SpringBootTest
public class TestUserDTO {
    
    @Test
    void testUserDTO() {
        ObjectId id = new ObjectId();
        User user = new User("test@example.com", "password", "test");
        user.setId(id);
        UserDTO userDTO = new UserDTO(user);
        assertEquals(user.getEmail(), userDTO.getEmail());
        assertEquals(user.getName(), userDTO.getName());
        assertEquals(user.getId().toHexString(), userDTO.getId());
        assertEquals(0, userDTO.getTrainingBlockNames().size());
    }
}
