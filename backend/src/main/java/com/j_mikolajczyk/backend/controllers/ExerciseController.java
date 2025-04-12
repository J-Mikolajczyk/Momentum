package com.j_mikolajczyk.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.j_mikolajczyk.backend.models.Exercise;
import com.j_mikolajczyk.backend.requests.ExerciseRequest;
import com.j_mikolajczyk.backend.services.ExerciseService;

@RestController
@RequestMapping("/exercise")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @Autowired
    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }


}
