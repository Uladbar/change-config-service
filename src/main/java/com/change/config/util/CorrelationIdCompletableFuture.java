package com.change.config.util;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Utility class for creating CompletableFuture instances that propagate correlation IDs.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CorrelationIdCompletableFuture {

  private final CorrelationIdUtils correlationIdUtils;

  /**
   * Creates a CompletableFuture that runs the given Runnable asynchronously and propagates the correlation ID to the asynchronous task.
   *
   * @param runnable The Runnable to run asynchronously
   * @return A CompletableFuture that completes when the Runnable completes
   */
  public CompletableFuture<Void> runAsync(Runnable runnable) {
    String correlationId = correlationIdUtils.getCorrelationId();

    if (correlationId != null) {
      log.debug("Propagating correlation ID to CompletableFuture: {}", correlationId);

      return CompletableFuture.runAsync(() -> {
        try {
          correlationIdUtils.setCorrelationId(correlationId);
          runnable.run();
        } finally {
          correlationIdUtils.clearCorrelationId();
        }
      });
    }

    return CompletableFuture.runAsync(runnable);
  }
}