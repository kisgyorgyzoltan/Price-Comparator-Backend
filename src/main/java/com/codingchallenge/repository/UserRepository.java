package com.codingchallenge.repository;

import com.codingchallenge.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByName(String name);

    List<User> findByLastCartUpdateBefore(LocalDateTime cutoff);

}
