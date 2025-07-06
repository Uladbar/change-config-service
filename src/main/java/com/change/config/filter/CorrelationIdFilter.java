package com.change.config.filter;

import com.change.config.util.CorrelationIdUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter that adds a correlation ID to each request. If the request already has a correlation ID header, it will be used. Otherwise, a new
 * correlation ID will be generated. The correlation ID is added to the MDC context for logging and to the response header.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CorrelationIdFilter extends OncePerRequestFilter {

  private final CorrelationIdUtils correlationIdUtils;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String correlationId = extractCorrelationId(request);

    try {
      correlationIdUtils.setCorrelationId(correlationId);

      // Add the correlation ID to the response header
      response.addHeader(CorrelationIdUtils.CORRELATION_ID_HEADER, correlationId);

      filterChain.doFilter(request, response);
    } finally {
      correlationIdUtils.clearCorrelationId();
    }
  }

  private String extractCorrelationId(HttpServletRequest request) {
    String correlationId = request.getHeader(CorrelationIdUtils.CORRELATION_ID_HEADER);

    if (!StringUtils.hasText(correlationId)) {
      correlationId = correlationIdUtils.generateCorrelationId();
    } else {
      log.debug("Using existing correlation ID from request: {}", correlationId);
    }

    return correlationId;
  }
}
