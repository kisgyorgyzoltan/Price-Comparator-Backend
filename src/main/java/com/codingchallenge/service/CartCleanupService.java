package com.codingchallenge.service;

import com.codingchallenge.model.User;
import com.codingchallenge.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class CartCleanupService {

    private final UserRepository userRepository;

    public CartCleanupService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Scheduled task to clear shopping carts of users who haven't updated their cart in the last 24 hours.
     * This method runs daily at midnight.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void clearStaleCarts() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        List<User> staleUsers = userRepository.findByLastCartUpdateBefore(cutoff);

        staleUsers.forEach(user -> {
            user.setShoppingCart(Collections.emptyList());
            user.setLastCartUpdate(null);
        });
        userRepository.saveAll(staleUsers);

        log.info("Cleared shopping carts for {} users who haven't updated their cart in the last 24 hours.", staleUsers.size());
    }

}
