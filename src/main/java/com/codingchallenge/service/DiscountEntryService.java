package com.codingchallenge.service;

import com.codingchallenge.model.DiscountEntry;
import com.codingchallenge.model.Product;
import com.codingchallenge.repository.DiscountEntryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DiscountEntryService {
    private final DiscountEntryRepository discountEntryRepository;

    public DiscountEntryService(DiscountEntryRepository discountEntryRepository) {
        this.discountEntryRepository = discountEntryRepository;
    }

    List<DiscountEntry> getDiscountEntriesForProduct(Product product) {
        return discountEntryRepository.findByProductId(product.getProductId());
    }

    public List<DiscountEntry> getAllDiscountEntries() {
        return discountEntryRepository.findAll();
    }

    public DiscountEntry getDiscountEntryById(String id) {
        return discountEntryRepository.findById(id).orElseThrow(
                () -> new RuntimeException("DiscountEntry not found with id: " + id)); // TODO: handle this better
    }

    public DiscountEntry createDiscountEntry(DiscountEntry discountEntry) {
        return discountEntryRepository.save(discountEntry);
    }

    public DiscountEntry updateDiscountEntry(DiscountEntry dbDiscountEntry, DiscountEntry incomingDiscountEntry) {
        dbDiscountEntry.setProductId(incomingDiscountEntry.getProductId());
        dbDiscountEntry.setFromDate(incomingDiscountEntry.getFromDate());
        dbDiscountEntry.setToDate(incomingDiscountEntry.getToDate());
        dbDiscountEntry.setPercentageOfDiscount(incomingDiscountEntry.getPercentageOfDiscount());
        dbDiscountEntry.setStoreName(incomingDiscountEntry.getStoreName());
        return discountEntryRepository.save(dbDiscountEntry);
    }

    public void deleteDiscountEntry( String id) {
        Optional<DiscountEntry> discountEntry = discountEntryRepository.findById(id);
        discountEntry.ifPresent(discountEntryRepository::delete);
    }

    public List<DiscountEntry> getNewestDiscountEntries() {
        return discountEntryRepository.findByDateAfter(LocalDate.now().minusDays(1));
    }
}
