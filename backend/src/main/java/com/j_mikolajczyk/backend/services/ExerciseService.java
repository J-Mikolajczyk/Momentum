package com.j_mikolajczyk.backend.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.j_mikolajczyk.backend.models.Exercise;
import com.j_mikolajczyk.backend.repositories.ExerciseRepository;
import com.j_mikolajczyk.backend.requests.ExerciseRequest;

@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    @Autowired
    public ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

}
