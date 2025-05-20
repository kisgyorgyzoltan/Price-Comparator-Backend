package com.codingchallenge.controller;

import com.codingchallenge.dto.incoming.CreatePriceAlertDto;
import com.codingchallenge.dto.outgoing.GetPriceAlertDto;
import com.codingchallenge.mapper.PriceAlertMapper;
import com.codingchallenge.model.PriceAlert;
import com.codingchallenge.service.PriceAlertService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/price-alerts")
@Validated
public class PriceAlertController {
    private final PriceAlertService priceAlertService;

    private final PriceAlertMapper priceAlertMapper;

    public PriceAlertController(PriceAlertService priceAlertService, PriceAlertMapper priceAlertMapper) {
        this.priceAlertService = priceAlertService;
        this.priceAlertMapper = priceAlertMapper;
    }

    @GetMapping
    public ResponseEntity<List<GetPriceAlertDto>> getPriceAlerts(
        @RequestParam(value = "userId", required = false)
        String userId
    ) {
        List<PriceAlert> priceAlerts = priceAlertService.getPriceAlertsByUser(userId);

        return ResponseEntity.ok(priceAlertMapper.toGetPriceAlertDtos(priceAlerts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetPriceAlertDto> getPriceAlertById(
        @PathVariable(value = "id")
        String id
    ) {
        PriceAlert priceAlert = priceAlertService.getPriceAlertById(id);

        return ResponseEntity.ok(priceAlertMapper.toGetPriceAlertDto(priceAlert));
    }

    @PostMapping
    public ResponseEntity<GetPriceAlertDto> createPriceAlert(
        @RequestBody
        @Valid
        CreatePriceAlertDto createPriceAlertDto
    ) {
        PriceAlert priceAlert = priceAlertMapper.toPriceAlert(createPriceAlertDto);
        PriceAlert createdPriceAlert = priceAlertService.createPriceAlert(priceAlert);

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                                          .path("/{id}")
                                          .buildAndExpand(createdPriceAlert.getId())
                                          .toUri())
            .body(priceAlertMapper.toGetPriceAlertDto(createdPriceAlert));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetPriceAlertDto> updatePriceAlert(
        @PathVariable(value = "id")
        String id,
        @RequestBody
        @Valid
        CreatePriceAlertDto createPriceAlertDto
    ) {
        PriceAlert existingPriceAlert = priceAlertService.getPriceAlertById(id);
        PriceAlert incomingPriceAlert = priceAlertMapper.toPriceAlert(createPriceAlertDto);
        PriceAlert updatedPriceAlert = priceAlertService.updatePriceAlert(id, existingPriceAlert, incomingPriceAlert);

        return ResponseEntity.ok(priceAlertMapper.toGetPriceAlertDto(updatedPriceAlert));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePriceAlert(
        @PathVariable(value = "id")
        String id
    ) {
        priceAlertService.deletePriceAlert(id);

        return ResponseEntity.noContent().build();
    }
}
