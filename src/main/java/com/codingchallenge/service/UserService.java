package com.codingchallenge.service;

import com.codingchallenge.model.Product;
import com.codingchallenge.model.User;
import com.codingchallenge.model.User.CartItem;
import com.codingchallenge.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final String ENCRYPT_ALGORITHM = "SHA-256";

    private static final String STORED_SALT = "stored_salt"; // shouldn't be hardcoded in real applications

    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found")); // TODO: handle this exception properly
    }

    public User register(String name, String password) {
        if (userRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("User already exists"); // TODO: handle this exception properly
        }
        User user = new User();
        user.setName(name);
        user.setPassword(encryptPassword(password, STORED_SALT));
        return userRepository.save(user);
    }

    public boolean login(User incomingUser) {
        User dbUser = userRepository.findByName(incomingUser.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found")); // TODO: handle this exception properly
        String encryptedPassword = encryptPassword(incomingUser.getPassword(), STORED_SALT);
        return encryptedPassword.equals(dbUser.getPassword());
    }

    public User updateUser(User dbUser, String oldPassword, User incomingUser) {
        String oldPasswordHash = encryptPassword(oldPassword, STORED_SALT);
        if (!oldPasswordHash.equals(dbUser.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect"); // TODO: handle this exception properly
        }

        dbUser.setName(incomingUser.getName());
        dbUser.setPassword(encryptPassword(incomingUser.getPassword(), STORED_SALT));
        return userRepository.save(dbUser);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public void clearCart(User user) {
        user.getShoppingCart().clear();
        user.setLastCartUpdate(user.getLastCartUpdate());
        userRepository.save(user);
    }

    /**
     * Adds a product to the user's shopping cart.
     * If the product already exists in the cart, it updates the quantity.
     **/
    public List<CartItem> addToCart(User user, Product product, Integer quantity) {
        String productId = product.getProductId();
        Optional<CartItem> cartItem = user.getShoppingCart().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (cartItem.isPresent()) {
            CartItem existingItem = cartItem.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setProductId(productId);
            newCartItem.setQuantity(quantity);
            user.getShoppingCart().add(newCartItem);
        }

        user.setLastCartUpdate(LocalDateTime.now());
        userRepository.save(user);
        return user.getShoppingCart();
    }

    public static String encryptPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ENCRYPT_ALGORITHM);
            byte[] input = (password + salt).getBytes();
            md.reset();
            md.update(input);
            byte[] digest = md.digest();

            StringBuilder sb = new StringBuilder();

            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e); // TODO: handle this exception properly
        }

    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<CartItem> removeFromCart(User user, Product product, @Positive @NotNull Integer quantity) {
        String productId = product.getProductId();
        Optional<CartItem> cartItem = user.getShoppingCart().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (cartItem.isPresent()) {
            CartItem existingItem = cartItem.get();
            int newQuantity = existingItem.getQuantity() - quantity;
            if (newQuantity <= 0) {
                user.getShoppingCart().remove(existingItem);
            } else {
                existingItem.setQuantity(newQuantity);
            }
        }

        user.setLastCartUpdate(LocalDateTime.now());
        userRepository.save(user);
        return user.getShoppingCart();
    }
}
