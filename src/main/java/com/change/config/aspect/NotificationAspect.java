package com.change.config.aspect;

import com.change.config.model.ConfigChange;
import com.change.config.service.NotificationService;
import com.change.config.util.CorrelationIdCompletableFuture;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationAspect {

  private final NotificationService notificationService;
  private final CorrelationIdCompletableFuture correlationIdCompletableFuture;

  @AfterReturning(
      pointcut = "execution(com.change.config.model.ConfigChange com.change.config.repository.ConfigChangeRepository.save(..))",
      returning = "savedChange"
  )
  public void notifyCriticalUpdate(ConfigChange savedChange) {
    try {
      if (isShouldNotify(savedChange)) {
        log.info("Should notify about : {}", savedChange);
        correlationIdCompletableFuture.runAsync(() -> notificationService.notifyCriticalChange(savedChange));
      }
    } catch (Exception e) {
      log.error("Fail to send notification about critical update", e);
    }
  }

  private boolean isShouldNotify(ConfigChange savedChange) {
    return Optional.ofNullable(savedChange).map(ConfigChange::isCritical).orElse(false);
  }
}
