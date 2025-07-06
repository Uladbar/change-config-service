package com.change.config.service.utils;

import com.change.config.model.ConfigChange;
import com.change.config.service.impl.NotificationServiceImpl;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This test class extends NotificationServiceImpl and overrides the notifyCriticalChange method to throw exceptions for testing retry
 * functionality.
 */
public class RetryTestNotificationService extends NotificationServiceImpl {

  private final AtomicInteger callCount = new AtomicInteger(0);
  private final int failUntilAttempt;

  public RetryTestNotificationService(int failUntilAttempt) {
    this.failUntilAttempt = failUntilAttempt;
  }

  @Override
  public void notifyCriticalChange(ConfigChange configChange) {
    int attempt = callCount.incrementAndGet();
    System.out.println("[DEBUG_LOG] Service call attempt: " + attempt);

    if (attempt < failUntilAttempt) {
      throw new RuntimeException("Simulated failure on attempt " + attempt);
    }

    // Call the original method if we're not throwing an exception
    super.notifyCriticalChange(configChange);
  }

  public int getCallCount() {
    return callCount.get();
  }

  public void resetCallCount() {
    callCount.set(0);
  }
}
