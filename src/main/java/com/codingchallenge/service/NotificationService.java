package com.codingchallenge.service;

import com.codingchallenge.model.Notification;
import com.codingchallenge.model.ShoppingList;
import com.codingchallenge.model.User;
import com.codingchallenge.model.User.CartItem;
import com.codingchallenge.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class NotificationService {

    private final ShoppingListService shoppingListService;

    private final UserService userService;

    private final NotificationRepository notificationRepository;

    public NotificationService(ShoppingListService shoppingListService, UserService userService, NotificationRepository notificationRepository) {
        this.shoppingListService = shoppingListService;
        this.userService = userService;
        this.notificationRepository = notificationRepository;
    }

    /**
     * This method is scheduled to run every day at 9 AM.
     * It retrieves all users from the database,
     * finds the best prices for the products in their carts,
     * and sends notifications to them.
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void notifyUsers() {
        List<User> users = userService.getAllUsers();

        for (User user : users) {
            List<CartItem> productsInCart = user.getShoppingCart();
            String userId = user.getId();
            ShoppingList newShoppingList = shoppingListService.generateBestPrices(userId, productsInCart);
            Notification notification = new Notification(
                    null,
                    userId,
                    newShoppingList,
                    false
            );
            notificationRepository.save(notification);
            // Mocking sending notification
            log.debug("Notifying user: {}", user.getName());

            // Update the notification status to notified
            notification.setNotified(true);
            notificationRepository.save(notification);
        }
    }

    /**
     * This method retrieves all notifications that have not been sent yet and sends them.
     * It is scheduled to run every 6 hours.
     */
    @Scheduled(cron = "0 0 */6 * * ?")
    public void sendNotifications() {
        List<Notification> unsentNotifications = notificationRepository.findAllByNotified(false);
        for (Notification notification : unsentNotifications) {
            // Mocking sending notification
            log.debug("Sending notification to user: {}", notification.getUserId());
            notification.setNotified(true);
            notificationRepository.save(notification);
        }
    }
}
