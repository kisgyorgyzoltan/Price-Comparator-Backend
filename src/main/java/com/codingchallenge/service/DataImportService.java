package com.codingchallenge.service;

import com.codingchallenge.dto.internal.DiscountParseResult;
import com.codingchallenge.dto.internal.PriceParseResult;
import com.codingchallenge.model.Product;
import com.codingchallenge.repository.DiscountEntryRepository;
import com.codingchallenge.repository.PriceEntryRepository;
import com.codingchallenge.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

/*
 * This service is responsible for importing data from CSV files into the database.
 * It handles both price and discount files, parsing them and saving the relevant entries.
 */
@Service
@Slf4j
public class DataImportService {

    private final CsvParserService csvParserService;

    private final DiscountEntryRepository discountEntryRepository;

    private final PriceEntryRepository priceEntryRepository;

    private final ProductRepository productRepository;

    public DataImportService(CsvParserService csvParserService, DiscountEntryRepository discountEntryRepository, PriceEntryRepository priceEntryRepository, ProductRepository productRepository) {
        this.csvParserService = csvParserService;
        this.discountEntryRepository = discountEntryRepository;
        this.priceEntryRepository = priceEntryRepository;
        this.productRepository = productRepository;
    }

    public void importPricesFromFile(Path filePath) {
        String fileName = filePath.getFileName().toString();
        String[] parts = fileName.split("_");
        String storeName = parts[0];
        LocalDate date;

        try (InputStream in = Files.newInputStream(filePath)) {
            if (fileName.contains("discount")) {
                date = LocalDate.parse(parts[2].replace(".csv", ""));
                DiscountParseResult discountParseResult = csvParserService.parseDiscountFile(in, storeName, date);
                discountEntryRepository.saveAll(discountParseResult.getDiscountEntries());

                syncProducts(discountParseResult.getProducts());
            } else {
                date = LocalDate.parse(fileName.split("_")[1].replace(".csv", ""));
                PriceParseResult priceParseResult = csvParserService.parsePriceFile(in, storeName, date);
                priceEntryRepository.saveAll(priceParseResult.getPriceEntries());

                syncProducts(priceParseResult.getProducts());
            }
        } catch (IOException e) {
            log.error("Error reading file {}: {}", fileName, e.getMessage(), e);
        }
    }

    private void syncProducts(Set<Product> products) {
        for (Product product : products) {
            Optional<Product> existingProductOpt = productRepository.findById(product.getProductId());
            if (existingProductOpt.isEmpty()) {
                productRepository.save(product);
            }
        }
    }
}

