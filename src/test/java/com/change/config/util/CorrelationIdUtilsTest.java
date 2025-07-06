package com.change.config.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class CorrelationIdUtilsTest {

  private final CorrelationIdUtils correlationIdUtils = new CorrelationIdUtils();

  @BeforeEach
  void tearDown() {
    MDC.clear();
  }

  @Test
  void generateCorrelationId_ShouldReturnUUID() {
    String correlationId = correlationIdUtils.generateCorrelationId();

    assertNotNull(correlationId);
    assertEquals(36, correlationId.length()); // UUID length
  }

  @Test
  void setAndGetCorrelationId_ShouldStoreAndRetrieveFromMDC() {
    String correlationId = "test-correlation-id";

    correlationIdUtils.setCorrelationId(correlationId);

    assertEquals(correlationId, MDC.get(CorrelationIdUtils.CORRELATION_ID_MDC_KEY));
    assertEquals(correlationId, correlationIdUtils.getCorrelationId());
  }

  @Test
  void clearCorrelationId_ShouldRemoveFromMDC() {
    String correlationId = "test-correlation-id";
    correlationIdUtils.setCorrelationId(correlationId);

    correlationIdUtils.clearCorrelationId();

    assertNull(MDC.get(CorrelationIdUtils.CORRELATION_ID_MDC_KEY));
    assertNull(correlationIdUtils.getCorrelationId());
  }
}