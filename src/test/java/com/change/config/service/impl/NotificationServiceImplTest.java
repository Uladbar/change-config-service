package com.change.config.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.change.config.model.ConfigChange;
import com.change.config.model.ConfigChangeType;
import com.change.config.service.utils.RetryTestConfig;
import com.change.config.service.utils.RetryTestNotificationService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
    "notification.retry.max-attempts=2",
    "notification.retry.delay=100"
})
@Import(RetryTestConfig.class)
class NotificationServiceImplTest {

  @Autowired
  private NotificationServiceImpl notificationService;

  @Autowired
  private RetryTestNotificationService retryTestService;

  @Autowired
  private RetryTestNotificationService alwaysFailingService;

  private ConfigChange criticalChange;

  @BeforeEach
  void setUp() {
    criticalChange = ConfigChange.builder()
        .id("test-id-123")
        .key("app.feature.enabled")
        .value("true")
        .type(ConfigChangeType.UPDATE)
        .note("Critical configuration update")
        .critical(true)
        .timestamp(LocalDateTime.now())
        .build();

    // Reset call counts before each test
    retryTestService.resetCallCount();
    alwaysFailingService.resetCallCount();
  }

  @Test
  void testNotifyCriticalChange_Success() {
    notificationService.notifyCriticalChange(criticalChange);
  }

  @Test
  void testNotifyCriticalChange_RetriesOnFailure() {

    assertDoesNotThrow(() -> retryTestService.notifyCriticalChange(criticalChange));

    int expectedCalls = 2;
    assertEquals(expectedCalls, retryTestService.getCallCount());
  }

  @Test
  void testNotifyCriticalChange_FailsAfterMaxRetries() {

    assertThrows(RuntimeException.class, () -> alwaysFailingService.notifyCriticalChange(criticalChange));

    int actualCalls = alwaysFailingService.getCallCount();


    // In our test environment, we're seeing 2 calls (initial + 1 retry)
    assertEquals(2, actualCalls);
  }
}
