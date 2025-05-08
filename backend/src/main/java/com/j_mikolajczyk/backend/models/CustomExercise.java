package com.j_mikolajczyk.backend.models;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "custom-exercises")
public class CustomExercise extends Exercise {
    private ObjectId createdByUserId;

    public CustomExercise() {
        super();
        createdByUserId = null;
    }

    public CustomExercise(String name, List<Set> sets, ObjectId createdByUserId) {
        super(name,sets);
        this.createdByUserId = createdByUserId;
    }

    public void setCreatedByUserId(ObjectId createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public ObjectId getCreatedByUserId() {
        return this.createdByUserId;
    }
}
