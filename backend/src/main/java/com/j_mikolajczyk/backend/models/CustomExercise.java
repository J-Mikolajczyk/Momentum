package com.j_mikolajczyk.backend.models;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "custom-exercises")
public class CustomExercise extends Exercise {
    private String createdByUserID;
}
