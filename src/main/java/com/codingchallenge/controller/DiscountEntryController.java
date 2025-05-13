package com.codingchallenge.controller;

import com.codingchallenge.dto.incoming.CreateDiscountEntryDto;
import com.codingchallenge.dto.outgoing.GetDiscountEntryDto;
import com.codingchallenge.mapper.DiscountEntryMapper;
import com.codingchallenge.model.DiscountEntry;
import com.codingchallenge.service.DiscountEntryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Pattern;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/discount-entries")
@Validated
public class DiscountEntryController {

    private final DiscountEntryService discountEntryService;

    private final DiscountEntryMapper discountEntryMapper;

    public DiscountEntryController(DiscountEntryService discountEntryService, DiscountEntryMapper discountEntryMapper) {
        this.discountEntryService = discountEntryService;
        this.discountEntryMapper = discountEntryMapper;
    }

    @GetMapping
    public ResponseEntity<List<GetDiscountEntryDto>> getAllDiscountEntries(@RequestParam(value = "newest", required = false) boolean newest) {
        try {
            if (newest) {
                return ResponseEntity.ok(discountEntryMapper.toGetDiscountEntryDtos(discountEntryService.getNewestDiscountEntries()));
            }
            return ResponseEntity.ok(discountEntryMapper.toGetDiscountEntryDtos(discountEntryService.getAllDiscountEntries()));
        } catch (IllegalArgumentException e) {
            log.error("Failed to fetch discounts: {}", e.getMessage());

            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetDiscountEntryDto> getDiscountEntryById(@PathVariable(value = "id") @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid ObjectId format for id") String id) {
        try {
            DiscountEntry discountEntry = discountEntryService.getDiscountEntryById(id);

            return ResponseEntity.ok(discountEntryMapper.toGetDiscountEntryDto(discountEntry));
        } catch (IllegalArgumentException e) {
            log.error("Failed to fetch discount entry: {}", e.getMessage());

            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<GetDiscountEntryDto> createDiscountEntry(@RequestBody @Valid CreateDiscountEntryDto discountEntryDto) {
        try {
            DiscountEntry discountEntry = discountEntryMapper.toDiscountEntry(discountEntryDto);
            DiscountEntry createdDiscountEntry = discountEntryService.createDiscountEntry(discountEntry);

            return ResponseEntity.ok(discountEntryMapper.toGetDiscountEntryDto(createdDiscountEntry));
        } catch (IllegalArgumentException e) {
            log.error("Failed to create discount entry: {}", e.getMessage());

            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetDiscountEntryDto> updateDiscountEntry(@PathVariable @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid ObjectId format for id") String id, @RequestBody @Valid CreateDiscountEntryDto discountEntryDto) {
        try {
            DiscountEntry existingDiscountEntry = discountEntryService.getDiscountEntryById(id);
            DiscountEntry incomingDiscountEntry = discountEntryMapper.toDiscountEntry(discountEntryDto);
            DiscountEntry updatedDiscountEntry = discountEntryService.updateDiscountEntry(existingDiscountEntry, incomingDiscountEntry);

            return ResponseEntity.ok(discountEntryMapper.toGetDiscountEntryDto(updatedDiscountEntry));
        } catch (IllegalArgumentException e) {
            log.error("Failed to update discount entry: {}", e.getMessage());

            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscountEntry(@PathVariable @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid ObjectId format for id") String id) {
        try {
            discountEntryService.deleteDiscountEntry(id);

            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Failed to delete discount entry: {}", e.getMessage());

            return ResponseEntity.badRequest().build();
        }
    }
}
