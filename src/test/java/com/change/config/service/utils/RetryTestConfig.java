package com.change.config.service.utils;

import com.change.config.service.impl.NotificationServiceImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@TestConfiguration
public class RetryTestConfig {

  @Bean
  @Primary
  public NotificationServiceImpl testNotificationService() {
    return new NotificationServiceImpl();
  }

  @Bean
  public RetryTestNotificationService retryTestService() {
    return new RetryTestNotificationService(2); // Will succeed on the 2nd attempt
  }

  @Bean
  public RetryTestNotificationService alwaysFailingService() {
    return new RetryTestNotificationService(Integer.MAX_VALUE); // Will always fail
  }
}
