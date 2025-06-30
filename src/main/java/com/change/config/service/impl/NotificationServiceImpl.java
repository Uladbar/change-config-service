package com.change.config.service.impl;

import com.change.config.model.ConfigChange;
import com.change.config.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of the NotificationService that logs critical changes and simulates notifying an external monitoring service.
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

  @Override
  public void notifyCriticalChange(ConfigChange configChange) {
    // Log the critical change
    log.warn("CRITICAL CONFIGURATION CHANGE: {} - Key: {}, Value: {}, Type: {}",
        configChange.getDescription(),
        configChange.getKey(),
        configChange.getValue(),
        configChange.getType());

    // Simulate notification to an external monitoring service
    log.info("Sending notification to external monitoring service for critical change: {}", configChange.getId());

    // In a real implementation, this would make an HTTP call or use a message queue
    // to notify an external system about the critical change
    simulateExternalServiceCall(configChange);
  }

  private void simulateExternalServiceCall(ConfigChange configChange) {
    try {
      // Simulate network latency
      Thread.sleep(100);
      log.info("External monitoring service notified successfully for change: {}", configChange.getId());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("Failed to notify external monitoring service", e);
    }
  }
}