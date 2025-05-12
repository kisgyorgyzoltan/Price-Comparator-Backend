package com.codingchallenge.repository;

import com.codingchallenge.model.ProcessedFile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface ProcessedFileRepository extends MongoRepository<ProcessedFile, String> {
    Optional<ProcessedFile> findByFileName(String fileName);
}
