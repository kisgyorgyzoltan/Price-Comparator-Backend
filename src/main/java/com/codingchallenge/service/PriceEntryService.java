package com.codingchallenge.service;

import com.codingchallenge.dto.outgoing.GetPriceHistoryDto;
import com.codingchallenge.model.PriceEntry;
import com.codingchallenge.repository.PriceEntryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PriceEntryService {

    private final PriceEntryRepository priceEntryRepository;

    public PriceEntryService(PriceEntryRepository priceEntryRepository) {
        this.priceEntryRepository = priceEntryRepository;
    }

    public List<GetPriceHistoryDto> getPriceHistory (String productId, String storeName, String productCategory, String brand) {
        return priceEntryRepository.getPriceHistory(productId, storeName, productCategory, brand);
    }

    public List<PriceEntry> getPriceEntries(String productId, Boolean orderByValue) {
        Sort sort = (orderByValue != null && orderByValue) ? Sort.by(Sort.Order.desc("valuePerUnit")) : Sort.unsorted();
        if (productId != null && !productId.isEmpty()) {
            return priceEntryRepository.findByProductId(productId, sort);
        }
        return priceEntryRepository.findAll(sort);
    }

    public PriceEntry getPriceEntryById(String id) {
        return priceEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Price entry not found with id: " + id)); // TODO: Handle this exception properly
    }

    public PriceEntry createPriceEntry(PriceEntry priceEntry) {
        return priceEntryRepository.save(priceEntry);
    }

    public PriceEntry updatePriceEntry(PriceEntry existingPriceEntry, PriceEntry incomingPriceEntry) {
        existingPriceEntry.setPrice(incomingPriceEntry.getPrice());
        existingPriceEntry.setCurrency(incomingPriceEntry.getCurrency());
        existingPriceEntry.setStoreName(incomingPriceEntry.getStoreName());
        existingPriceEntry.setDate(incomingPriceEntry.getDate());
        return priceEntryRepository.save(existingPriceEntry);
    }

    public void deletePriceEntry(String id) {
        Optional<PriceEntry> priceEntry = priceEntryRepository.findById(id);
        priceEntry.ifPresent(priceEntryRepository::delete);
    }
}
