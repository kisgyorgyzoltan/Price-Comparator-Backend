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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/discount-entries")
@Validated
public class DiscountEntryController {

    private final DiscountEntryService discountEntryService;

    private final DiscountEntryMapper discountEntryMapper;

    public DiscountEntryController(DiscountEntryService discountEntryService,
                                   DiscountEntryMapper discountEntryMapper
    ) {
        this.discountEntryService = discountEntryService;
        this.discountEntryMapper = discountEntryMapper;
    }

    @GetMapping
    public ResponseEntity<List<GetDiscountEntryDto>> getAllDiscountEntries(
            @RequestParam(value = "productId", required = false)
            @Pattern(regexp = "^[A-Z0-9]{1,20}$", message = "Product ID must be alphanumeric and between 1 and 20 characters.")
            String productId,
            @RequestParam(value = "newDiscounts", required = false)
            Boolean newDiscounts,
            @RequestParam(value = "orderByDiscountPercentageDesc", required = false)
            Boolean orderByDiscountPercentageDesc
    ) {
        List<DiscountEntry> discountEntries = discountEntryService.getDiscountEntries(productId, newDiscounts, orderByDiscountPercentageDesc);

        return ResponseEntity.ok(discountEntryMapper.toGetDiscountEntryDtos(discountEntries));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetDiscountEntryDto> getDiscountEntryById(
            @PathVariable(value = "id")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid ObjectId format for id")
            String id
    ) {
        DiscountEntry discountEntry = discountEntryService.getDiscountEntryById(id);

        return ResponseEntity.ok(discountEntryMapper.toGetDiscountEntryDto(discountEntry));
    }

    @PostMapping
    public ResponseEntity<GetDiscountEntryDto> createDiscountEntry(
            @RequestBody
            @Valid
            CreateDiscountEntryDto discountEntryDto
    ) {
        DiscountEntry discountEntry = discountEntryMapper.toDiscountEntry(discountEntryDto);
        DiscountEntry createdDiscountEntry = discountEntryService.createDiscountEntry(discountEntry);

        return ResponseEntity.created(ServletUriComponentsBuilder
                                          .fromCurrentRequest()
                                          .path("/{id}")
                                          .buildAndExpand(createdDiscountEntry.getId())
                                          .toUri())
            .body(discountEntryMapper.toGetDiscountEntryDto(createdDiscountEntry));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetDiscountEntryDto> updateDiscountEntry(
            @PathVariable
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid ObjectId format for id")
            String id,
            @RequestBody
            @Valid
            CreateDiscountEntryDto discountEntryDto
    ) {
        DiscountEntry existingDiscountEntry = discountEntryService.getDiscountEntryById(id);
        DiscountEntry incomingDiscountEntry = discountEntryMapper.toDiscountEntry(discountEntryDto);
        DiscountEntry updatedDiscountEntry = discountEntryService.updateDiscountEntry(existingDiscountEntry, incomingDiscountEntry);

        return ResponseEntity.ok(discountEntryMapper.toGetDiscountEntryDto(updatedDiscountEntry));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscountEntry(
            @PathVariable
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid ObjectId format for id")
            String id
    ) {
        discountEntryService.deleteDiscountEntry(id);

        return ResponseEntity.noContent().build();
    }
}
