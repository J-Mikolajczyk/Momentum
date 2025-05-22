package com.j_mikolajczyk.backend.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.j_mikolajczyk.backend.models.Exercise;
import com.j_mikolajczyk.backend.requests.ExerciseRequest;
import com.j_mikolajczyk.backend.services.ExerciseService;

@RestController
@RequestMapping("/secure/exercises")
public class ExerciseController {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseController.class);

    private final ExerciseService exerciseService;

    @Autowired
    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Exercise>> getAllExercises() {
        logger.info("Fetching all exercises");
        try {
            List<Exercise> exercises = exerciseService.getAllExercises();
            logger.info("Successfully fetched {} exercises", exercises.size());
            return ResponseEntity.ok(exercises);
        } catch (Exception e) {
            logger.error("Failed to fetch exercises: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

}
