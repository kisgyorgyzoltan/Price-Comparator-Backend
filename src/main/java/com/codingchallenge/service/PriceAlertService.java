package com.codingchallenge.service;

import com.codingchallenge.exception.NotFoundException;
import com.codingchallenge.model.PriceAlert;
import com.codingchallenge.model.PriceEntry;
import com.codingchallenge.repository.PriceAlertRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PriceAlertService {
    private final PriceAlertRepository priceAlertRepository;

    private final PriceEntryService priceEntryService;

    public PriceAlertService(PriceAlertRepository priceAlertRepository, PriceEntryService priceEntryService) {
        this.priceAlertRepository = priceAlertRepository;
        this.priceEntryService = priceEntryService;
    }

    public List<PriceAlert> getPriceAlertsByUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            return priceAlertRepository.findAll();
        }

        return priceAlertRepository.findByUserId(userId);
    }

    public PriceAlert getPriceAlertById(String id) {
        return priceAlertRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Price alert not found with id: " + id));
    }

    public PriceAlert createPriceAlert(PriceAlert priceAlert) {
        return priceAlertRepository.save(priceAlert);
    }

    public void deletePriceAlert(String id) {
        PriceAlert priceAlert = getPriceAlertById(id);
        priceAlertRepository.delete(priceAlert);
    }

    public PriceAlert updatePriceAlert(String id, PriceAlert existingPriceAlert, PriceAlert incomingPriceAlert) {
        existingPriceAlert.setTargetPrice(incomingPriceAlert.getTargetPrice());
        existingPriceAlert.setProductId(incomingPriceAlert.getProductId());
        existingPriceAlert.setUserId(incomingPriceAlert.getUserId());
        existingPriceAlert.setMessage(incomingPriceAlert.getMessage());

        return priceAlertRepository.save(existingPriceAlert);
    }

    /*
     * This method is scheduled to run every 3 hours to check for price alerts.
     * It retrieves all price alerts and checks if the product price has dropped
     * below the target price. If it has, the user is notified.
     * PS: Using a cache like Redis would be more efficient than querying the database
     */
    @Scheduled(cron = "0 0 0/3 * * ?")
    public void checkPriceAlerts() {
        List<PriceAlert> priceAlerts = priceAlertRepository.findAll();
        for (PriceAlert priceAlert : priceAlerts) {
            String productId = priceAlert.getProductId();
            PriceEntry latestPriceEntry = priceEntryService.getCheapestPriceEntry(productId);
            if (latestPriceEntry.getPrice() <= priceAlert.getTargetPrice()) {
                // Mock sending notification
                log.info("Price alert triggered for product: {}. Current price: {}, Target price: {}, User ID: {}",
                        productId, latestPriceEntry.getPrice(), priceAlert.getTargetPrice(), priceAlert.getUserId());
            }
        }
    }
}
