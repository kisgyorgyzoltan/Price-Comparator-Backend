package com.codingchallenge.controller;

import com.codingchallenge.dto.incoming.CreatePriceEntryDto;
import com.codingchallenge.dto.outgoing.GetPriceEntryDto;
import com.codingchallenge.dto.outgoing.GetPriceHistoryDto;
import com.codingchallenge.mapper.PriceEntryMapper;
import com.codingchallenge.model.PriceEntry;
import com.codingchallenge.service.PriceEntryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/price-entries")
@Validated
public class PriceEntryController {

    private final PriceEntryService priceEntryService;

    private final PriceEntryMapper priceEntryMapper;

    public PriceEntryController(PriceEntryService priceEntryService,
                                PriceEntryMapper priceEntryMapper
    ) {
        this.priceEntryService = priceEntryService;
        this.priceEntryMapper = priceEntryMapper;
    }

    @GetMapping
    public ResponseEntity<List<GetPriceEntryDto>> getPriceEntries(@RequestParam(value = "productId", required = false)
                                                                  @Pattern(regexp = "^[A-Z0-9]{1,20}$", message = "Product ID must be alphanumeric and between 1 and 20 characters.")
                                                                  String productId,
                                                                  @RequestParam(value = "orderByValue", required = false)
                                                                  Boolean orderByValue
    ) {
        try {
            List<PriceEntry> priceEntries = priceEntryService.getPriceEntries(productId,orderByValue);

            return ResponseEntity.ok(priceEntryMapper.toGetPriceEntryDtos(priceEntries));
        } catch (IllegalArgumentException e) {
            log.error("Failed to fetch price entries {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetPriceEntryDto> getPriceEntryById(
            @PathVariable
            String id
    ) {
        try {
            PriceEntry priceEntry = priceEntryService.getPriceEntryById(id);
            return ResponseEntity.ok(priceEntryMapper.toGetPriceEntryDto(priceEntry));
        } catch (IllegalArgumentException e) {
            log.error("Failed to fetch price entry {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<GetPriceEntryDto> createPriceEntry(
            @RequestBody
            @Valid
            CreatePriceEntryDto createPriceEntryDto
    ) {
        try {
            PriceEntry priceEntry = priceEntryMapper.toPriceEntry(createPriceEntryDto);
            PriceEntry createdPriceEntry = priceEntryService.createPriceEntry(priceEntry);
            return ResponseEntity.ok(priceEntryMapper.toGetPriceEntryDto(createdPriceEntry));
        } catch (IllegalArgumentException e) {
            log.error("Failed to create price entry {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetPriceEntryDto> updatePriceEntry(
            @PathVariable
            String id,
            @RequestBody
            @Valid
            CreatePriceEntryDto createPriceEntryDto
    ) {
        try {
            PriceEntry existingPriceEntry = priceEntryService.getPriceEntryById(id);
            PriceEntry incomingPriceEntry = priceEntryMapper.toPriceEntry(createPriceEntryDto);
            PriceEntry updatedPriceEntry = priceEntryService.updatePriceEntry(existingPriceEntry, incomingPriceEntry);
            return ResponseEntity.ok(priceEntryMapper.toGetPriceEntryDto(updatedPriceEntry));
        } catch (IllegalArgumentException e) {
            log.error("Failed to update price entry {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePriceEntry(
            @PathVariable
            String id
    ) {
        try {
            priceEntryService.deletePriceEntry(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Failed to delete price entry {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Fetches the price history based on the provided filters.
     * If no filters are provided, it returns the entire price history.
     */
    @GetMapping("/history")
    public ResponseEntity<List<GetPriceHistoryDto>> getPriceHistory(
          @RequestParam(value = "productId", required = false)
          @Pattern(regexp = "^[A-Z0-9]{1,20}$", message = "Product ID must be alphanumeric and between 1 and 20 characters.")
          String productId,
          @RequestParam(value = "storeName", required = false)
          @Pattern(regexp = "^[A-Za-z0-9\\s]{1,100}$", message = "Store name must be alphanumeric and between 1 and 100 characters.")
          String storeName,
          @RequestParam(value = "productCategory", required = false)
          @Pattern(regexp = "^[A-Za-z0-9\\s]{1,100}$", message = "Product description must be alphanumeric and between 1 and 100 characters.")
          String productCategory,
          @RequestParam(value = "brand", required = false)
          @Pattern(regexp = "^[A-Za-z0-9\\s]{1,100}$", message = "Brand name must be alphanumeric and between 1 and 100 characters.")
          String brand
    ) {
        try {
            List<GetPriceHistoryDto> priceEntries = priceEntryService.getPriceHistory(productId, storeName, productCategory, brand);

            return ResponseEntity.ok(priceEntries);
        } catch (IllegalArgumentException e) {
            log.error("Failed to fetch price history {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

}
