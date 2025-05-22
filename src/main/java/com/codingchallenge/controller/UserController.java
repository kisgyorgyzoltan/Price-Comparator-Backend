package com.codingchallenge.controller;

import com.codingchallenge.dto.incoming.AddProductToCartDto;
import com.codingchallenge.dto.incoming.CreateUserDto;
import com.codingchallenge.dto.incoming.UpdateUserDto;
import com.codingchallenge.dto.outgoing.GetShoppingListDto;
import com.codingchallenge.dto.outgoing.GetUserDto;
import com.codingchallenge.mapper.ShoppingListMapper;
import com.codingchallenge.mapper.UserMapper;
import com.codingchallenge.model.Product;
import com.codingchallenge.model.ShoppingList;
import com.codingchallenge.model.User;
import com.codingchallenge.model.User.CartItem;
import com.codingchallenge.service.ProductService;
import com.codingchallenge.service.ShoppingListService;
import com.codingchallenge.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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

    public UserController(UserService userService,
                          UserMapper userMapper,
                          ProductService productService,
                          ShoppingListService shoppingListService,
                          ShoppingListMapper shoppingListMapper
    ) {
        this.userService = userService;
        this.productService = productService;
        this.userMapper = userMapper;
        this.shoppingListService = shoppingListService;
        this.shoppingListMapper = shoppingListMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<Boolean> login(
            @RequestBody
            @Valid CreateUserDto userDto
    ) {
        User user = userMapper.toUser(userDto);
        if (!userService.login(user)) {
            log.error("Login failed for user: {}", userDto.getName());
            return ResponseEntity.status(401)
                    .body(false);
        }

        return ResponseEntity.ok(true);
    }

    @PostMapping("/register")
        public ResponseEntity<GetUserDto> register(
            @RequestBody
            @Valid
            CreateUserDto userDto
    ) {
        log.warn("Registering user: {}", userDto.toString());
        User user = userService.register(userDto.getName(), userDto.getPassword());
        log.warn("User registered: {}", user.toString());
        log.warn("User mapped to dto: {}", userMapper.toGetUserDto(user).toString());

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                                          .buildAndExpand(user.getId()).toUri())
            .body(userMapper.toGetUserDto(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetUserDto> getUser(
            @PathVariable
            String id
    ) {
        User user = userService.getUserById(id);

        return ResponseEntity.ok(userMapper.toGetUserDto(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetUserDto> updateUser(
            @PathVariable
            String id,
            @RequestBody
            @Valid
            UpdateUserDto updateUserDto
    ) {
        User dbUser = userService.getUserById(id);
        User user = userService.updateUser(dbUser, updateUserDto);

        return ResponseEntity.ok(userMapper.toGetUserDto(user));
    }

    @GetMapping
    public ResponseEntity<List<GetUserDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        return ResponseEntity.ok(userMapper.toGetUserDtos(users));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid ObjectId format for id")
            String id
    ) {
        User user = userService.getUserById(id);
        userService.deleteUser(user);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/cart")
    public ResponseEntity<GetUserDto> addToCart(
            @PathVariable
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid ObjectId format for id")
            String userId,
            @RequestBody
            @Valid
            AddProductToCartDto addProductToCartDto
    ) {
        String productId = addProductToCartDto.getProductId();
        Integer quantity = addProductToCartDto.getQuantity();
        User user = userService.getUserById(userId);
        Product product = productService.getProductById(productId);
        List<CartItem> cartItems =  userService.addToCart(user, product, quantity);
        log.debug("Number of different items in cart: {}", cartItems.size());
        User updatedUser = userService.getUserById(userId);

        return ResponseEntity.ok(userMapper.toGetUserDto(updatedUser));
    }

    @DeleteMapping("/{userId}/cart")
    public ResponseEntity<GetUserDto> removeFromCart(
            @PathVariable
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid ObjectId format for userId")
            String userId,
            @RequestBody(required = false)
            @Valid
            AddProductToCartDto addProductToCartDto
    ) {
        User user = userService.getUserById(userId);

        if (addProductToCartDto == null) {
            userService.clearCart(user);
            User updatedUser = userService.getUserById(userId);

            return ResponseEntity.ok(userMapper.toGetUserDto(updatedUser));
        }

        String productId = addProductToCartDto.getProductId();
        Integer quantity = addProductToCartDto.getQuantity();

        Product product = productService.getProductById(productId);
        List<CartItem> cartItems =  userService.removeFromCart(user, product, quantity);
        log.debug("Remaining number of different items in cart: {}", cartItems.size());
        User updatedUser = userService.getUserById(userId);

        return ResponseEntity.ok(userMapper.toGetUserDto(updatedUser));
    }

    @PostMapping("/{userId}/shopping-list")
    public ResponseEntity<GetShoppingListDto> generateShoppingList(
            @PathVariable
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid ObjectId format for id")
            String userId
    ) {
        User user = userService.getUserById(userId);
        List<CartItem> cart = user.getShoppingCart();

        ShoppingList shoppingList = shoppingListService.generateBestPrices(userId, cart);

        return ResponseEntity.created(URI.create("/api/shopping-lists/" + shoppingList.getId()))
                .body(
            shoppingListMapper.toGetShoppingListDto(shoppingList));
    }

    @GetMapping("/{userId}/shopping-list")
    public ResponseEntity<List<GetShoppingListDto>> getShoppingLists(
            @PathVariable
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid ObjectId format for userId")
            String userId
    ) {
        User user = userService.getUserById(userId);
        List<ShoppingList> shoppingLists = shoppingListService.getShoppingListsByUserId(user);

        return ResponseEntity.ok(shoppingListMapper.toGetShoppingListDtos(shoppingLists));
    }
}
