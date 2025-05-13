package com.codingchallenge.controller;

import com.codingchallenge.dto.incoming.CreateUserDto;
import com.codingchallenge.dto.outgoing.GetProductDto;
import com.codingchallenge.dto.outgoing.GetShoppingListDto;
import com.codingchallenge.dto.outgoing.GetUserDto;
import com.codingchallenge.mapper.ProductMapper;
import com.codingchallenge.mapper.ShoppingListMapper;
import com.codingchallenge.mapper.UserMapper;
import com.codingchallenge.model.Product;
import com.codingchallenge.model.ShoppingList;
import com.codingchallenge.model.User;
import com.codingchallenge.service.ProductService;
import com.codingchallenge.service.ShoppingListService;
import com.codingchallenge.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/users")
@Validated
public class UserController {
    private final UserService userService;

    private final ProductService productService;

    private final ShoppingListService shoppingListService;

    private final ShoppingListMapper shoppingListMapper;

    private final UserMapper userMapper;

    private final ProductMapper productMapper;

    public UserController(UserService userService, UserMapper userMapper, ProductService productService, ShoppingListService shoppingListService, ShoppingListMapper shoppingListMapper, ProductMapper productMapper) {
        this.userService = userService;
        this.productService = productService;
        this.userMapper = userMapper;
        this.shoppingListService = shoppingListService;
        this.shoppingListMapper = shoppingListMapper;
        this.productMapper = productMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<Boolean> login(@RequestBody @Valid CreateUserDto userDto) {
        try {
            User user = userMapper.toUser(userDto);
            if (!userService.login(user)) {
                log.error("Login failed for user: {}", userDto.getName());
                return ResponseEntity.badRequest().body(false);
            }
            return ResponseEntity.ok(true);
        } catch (IllegalArgumentException e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(false);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<GetUserDto> register(@RequestBody @Valid CreateUserDto userDto) {
        try {
            log.warn("Registering user: {}", userDto.toString());
            User user = userService.register(userDto.getName(), userDto.getPassword());
            log.warn("User registered: {}", user.toString());
            log.warn("User mapped to dto: {}", userMapper.toGetUserDto(user).toString());
            return ResponseEntity.ok(userMapper.toGetUserDto(user));
        } catch (IllegalArgumentException e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetUserDto> getUser(@PathVariable String id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(userMapper.toGetUserDto(user));
        } catch (IllegalArgumentException e) {
            log.error("Get user failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetUserDto> updateUser(@PathVariable String id, @RequestBody String oldPassword, @RequestBody @Valid CreateUserDto userDto) {
        try {
            User incomingUser = userMapper.toUser(userDto);
            User dbUser = userService.getUserById(id);
            User user = userService.updateUser(dbUser, oldPassword, incomingUser);
            return ResponseEntity.ok(userMapper.toGetUserDto(user));
        } catch (IllegalArgumentException e) {
            log.error("Update user failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .build();
        }
    }

    @GetMapping
    public ResponseEntity<List<GetUserDto>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(userMapper.toGetUserDtos(users));
        } catch (IllegalArgumentException e) {
            log.error("Get all users failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid ObjectId format for id") String id) {
        try {
            User user = userService.getUserById(id);
            userService.deleteUser(user);
            return ResponseEntity.noContent()
                    .build();
        } catch (IllegalArgumentException e) {
            log.error("Delete user failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .build();
        }
    }

    @DeleteMapping("/{id}/cart")
    public ResponseEntity<Void> clearCart(@PathVariable @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid ObjectId format for id") String id) {
        try {
            User user = userService.getUserById(id);
            userService.clearCart(user);
            return ResponseEntity.noContent()
                    .build();
        } catch (IllegalArgumentException e) {
            log.error("Clear cart failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .build();
        }
    }

    @PostMapping("/{id}/cart")
    public ResponseEntity<List<GetProductDto>> addToCart(@PathVariable @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid ObjectId format for id") String id, @RequestBody  @Pattern(regexp = "^[A-Z0-9]{1,20}$", message = "Product ID must be alphanumeric and between 1 and 20 characters.") String productId) {
        try {
            User user = userService.getUserById(id);
            Product product = productService.getProductById(productId);
            List<Product> cart = userService.addToCart(user, product);
            return ResponseEntity.ok(productMapper.toGetProductDtos(cart));
        } catch (IllegalArgumentException e) {
            log.error("Add to cart failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .build();
        }
    }

    @PostMapping("/{userId}/shopping-list")
    public ResponseEntity<GetShoppingListDto> generateShoppingList(@PathVariable @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid ObjectId format for id") String userId) {
        try {
            User user = userService.getUserById(userId);
            List<Product> cart = user.getShoppingCart();

            ShoppingList shoppingList = shoppingListService.getBestPrices(userId, cart);
            return ResponseEntity.ok(shoppingListMapper.toGetShoppingListDto(shoppingList));

        } catch (IllegalArgumentException e) {
            log.error("Generate shopping list failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }
}
