package com.j_mikolajczyk.backend.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.j_mikolajczyk.backend.models.User;

@Repository ()
public interface UserRepository extends MongoRepository<User, ObjectId> {
    User findByEmail(String email);
}
