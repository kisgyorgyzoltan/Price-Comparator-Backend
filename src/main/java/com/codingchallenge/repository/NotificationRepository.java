package com.codingchallenge.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.codingchallenge.model.Notification;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findAllByNotified(boolean notified);
}
