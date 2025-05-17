package com.codingchallenge.service;

import com.codingchallenge.dto.outgoing.GetPriceHistoryDto;
import com.codingchallenge.model.PriceEntry;
import com.codingchallenge.model.Product;
import com.codingchallenge.repository.PriceEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PriceEntryService {

    private final PriceEntryRepository priceEntryRepository;

    public PriceEntryService(PriceEntryRepository priceEntryRepository) {
        this.priceEntryRepository = priceEntryRepository;
    }

    public List<PriceEntry> getPriceEntriesForProduct(Product product) {
        return priceEntryRepository.findByProductId(product.getProductId());
    }

    public List<GetPriceHistoryDto> getPriceHistory (String productId, String storeName, String productCategory, String brand) {
        return priceEntryRepository.getPriceHistory(productId, storeName, productCategory, brand);
    }

    public List<PriceEntry> getPriceEntries() {
        return priceEntryRepository.findAll();
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
