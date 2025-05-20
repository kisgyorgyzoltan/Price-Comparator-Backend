package com.codingchallenge.service;

import com.codingchallenge.model.ProcessedFile;
import com.codingchallenge.repository.ProcessedFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.stream.Stream;

/*
 * This service is responsible for periodically importing CSV files from a specified directory.
 * It checks if the files have already been processed and imports them if not.
 * The scheduled task runs every 24 hours.
 */
@Service
@Slf4j
public class CsvScheduledTask {

    private final DataImportService dataImportService;

    private final ProcessedFileRepository processedFileRepository;

    private static final String DATA_DIRECTORY_PATH = "src/main/resources/data";

    public CsvScheduledTask(DataImportService dataImportService, ProcessedFileRepository processedFileRepository) {
        this.dataImportService = dataImportService;
        this.processedFileRepository = processedFileRepository;
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void importCsvFiles() {
        Path dataDirectory = Paths.get(DATA_DIRECTORY_PATH);
        try (Stream<Path> paths = Files.walk(dataDirectory)) {
            paths.filter(Files::isRegularFile)
                    .forEach(file -> {
                        String fileName = file.getFileName().toString();
                        // Check if this file has already been processed
                        if (processedFileRepository.findByFileName(fileName).isEmpty()) {
                            log.info("Processing file: {}", fileName);
                            dataImportService.importPricesFromFile(file);

                            // Mark this file as processed by saving it to the database
                            ProcessedFile processedFile = new ProcessedFile();
                            processedFile.setFileName(fileName);
                            processedFile.setProcessedDate(LocalDate.now());
                            processedFileRepository.save(processedFile);
                        }
                    });
        } catch (IOException e) {
            log.error("Error reading files from directory {}: {}", dataDirectory, e.getMessage(), e);
        }
    }
}
