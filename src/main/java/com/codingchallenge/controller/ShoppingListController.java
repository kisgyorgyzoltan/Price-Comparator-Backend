package com.codingchallenge.controller;

import com.codingchallenge.dto.outgoing.GetShoppingListDto;
import com.codingchallenge.mapper.ShoppingListMapper;
import com.codingchallenge.model.ShoppingList;
import com.codingchallenge.service.ShoppingListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/shopping-lists")
public class ShoppingListController {
    private final ShoppingListService shoppingListService;

    private final ShoppingListMapper shoppingListMapper;

    public ShoppingListController(ShoppingListService shoppingListService,
                                  ShoppingListMapper shoppingListMapper
    ) {
        this.shoppingListService = shoppingListService;
        this.shoppingListMapper = shoppingListMapper;
    }

    @GetMapping
    public ResponseEntity<List<GetShoppingListDto>> getAllShoppingLists() {
        List<ShoppingList> shoppingLists = shoppingListService.getAllShoppingLists();

        return ResponseEntity.ok(shoppingListMapper.toGetShoppingListDtos(shoppingLists));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetShoppingListDto> getShoppingListById(
            @PathVariable
            String id
    ) {
        ShoppingList shoppingList = shoppingListService.getShoppingListById(id);

        return ResponseEntity.ok(shoppingListMapper.toGetShoppingListDto(shoppingList));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShoppingList(
            @PathVariable
            String id
    ) {
        shoppingListService.deleteShoppingList(id);

        return ResponseEntity.noContent().build();
    }
}
