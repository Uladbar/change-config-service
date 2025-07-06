package com.change.config.util;

import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Utility class for managing correlation IDs.
 * Correlation IDs are used to track requests across different components of the application.
 */
@Component
public class CorrelationIdUtils {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";

    /**
     * Generates a new correlation ID.
     *
     * @return A new correlation ID
     */
    public String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Sets the correlation ID in the MDC context.
     *
     * @param correlationId The correlation ID to set
     */
    public void setCorrelationId(String correlationId) {
        MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
    }

    /**
     * Gets the correlation ID from the MDC context.
     *
     * @return The correlation ID, or null if not set
     */
    public String getCorrelationId() {
        return MDC.get(CORRELATION_ID_MDC_KEY);
    }

    /**
     * Clears the correlation ID from the MDC context.
     */
    public void clearCorrelationId() {
        MDC.remove(CORRELATION_ID_MDC_KEY);
    }
}