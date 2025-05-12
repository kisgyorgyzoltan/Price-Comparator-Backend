package com.codingchallenge.service;

import com.codingchallenge.dto.internal.DiscountParseResult;
import com.codingchallenge.dto.internal.PriceParseResult;
import com.codingchallenge.model.DiscountEntry;
import com.codingchallenge.model.PriceEntry;
import com.codingchallenge.model.Product;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/*
    * This service is responsible for parsing CSV files containing product information.
    * It provides methods to parse price and discount files, extracting relevant data and returning it in a structured format.
 */
@Service
@Slf4j
public class CsvParserService {
    private static Product getProductCsv(String[] line, int categoryIndex, int brandIndex, int quantityIndex, int unitIndex) {
        String productId = line[0];
        String productName = line[1];
        String productCategory = line[categoryIndex];
        String brand = line[brandIndex];
        double packageQuantity = Double.parseDouble(line[quantityIndex]);
        String packageUnit = line[unitIndex];

        return Product.builder()
                .productId(productId)
                .productName(productName)
                .productCategory(productCategory)
                .brand(brand)
                .packageQuantity(packageQuantity)
                .packageUnit(packageUnit)
                .build();
    }


    private static PriceEntry getPriceEntryCsv(String storeName, LocalDate date, String[] line) {
        String productId = line[0];
        double price = Double.parseDouble(line[6]);
        String currency = line[7];

        return PriceEntry.builder()
                .productId(productId)
                .price(price)
                .currency(currency)
                .storeName(storeName)
                .date(date)
                .build();
    }

    private static DiscountEntry getDiscountEntryCsv(String storeName, LocalDate date, String[] line) {
        String productId = line[0];
        LocalDate fromDate = LocalDate.parse(line[6]);
        LocalDate toDate = LocalDate.parse(line[7]);
        int discountPercentage = Integer.parseInt(line[8]);

        return DiscountEntry.builder().
                productId(productId)
                .fromDate(fromDate)
                .toDate(toDate)
                .percentageOfDiscount(discountPercentage)
                .storeName(storeName)
                .date(date)
                .build();
    }

    public PriceParseResult parsePriceFile(InputStream csvInputStream, String storeName, LocalDate date) {
        List<PriceEntry> priceEntries = new ArrayList<>();
        Set<Product> products = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvInputStream))) {
            try (CSVReader csvReader = new CSVReaderBuilder(reader)
                .withSkipLines(1)
                .withCSVParser(new CSVParserBuilder()
                    .withSeparator(';')
                    .build())
                .build()) {

                String[] line;
                while ((line = csvReader.readNext()) != null) {
                    Product product = getProductCsv(line, 2, 3, 4, 5);
                    PriceEntry entry = getPriceEntryCsv(storeName, date, line);

                    priceEntries.add(entry);
                    products.add(product);
                }
            }

        } catch (IOException | CsvValidationException e) {
            log.error("Error reading CSV file: {}", e.getMessage(), e);
        }

        return new PriceParseResult(priceEntries, products);
    }

    public DiscountParseResult parseDiscountFile(InputStream csvInputStream, String storeName, LocalDate date) {
        List<DiscountEntry> entries = new ArrayList<>();
        Set<Product> products = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvInputStream))) {
            try (CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withSkipLines(1)
                    .withCSVParser(new CSVParserBuilder()
                                   .withSeparator(';')
                                   .build())
                    .build()) {
                String[] line;

                while ((line = csvReader.readNext()) != null) {
                    Product product = getProductCsv(line, 5, 2, 3, 4);
                    DiscountEntry entry = getDiscountEntryCsv(storeName, date, line);

                    entries.add(entry);
                    products.add(product);
                }
            }

        } catch (IOException | CsvValidationException e) {
            log.error("Error reading CSV file: {}", e.getMessage(), e);
        }

        return new DiscountParseResult(entries, products);
    }
}
