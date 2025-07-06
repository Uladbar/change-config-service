package com.change.config.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.change.config.util.CorrelationIdUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

@ExtendWith(MockitoExtension.class)
class CorrelationIdFilterTest {

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  private CorrelationIdFilter filter;

  @BeforeEach
  void setUp() {
    CorrelationIdUtils correlationIdUtils = new CorrelationIdUtils();
    filter = new CorrelationIdFilter(correlationIdUtils);
  }

  @AfterEach
  void tearDown() {
    // Clear MDC after each test
    MDC.clear();
  }

  @Test
  void doFilterInternal_WithExistingCorrelationId_ShouldUseExistingId() throws Exception {
    // Arrange
    String existingCorrelationId = "existing-correlation-id";
    when(request.getHeader(CorrelationIdUtils.CORRELATION_ID_HEADER)).thenReturn(existingCorrelationId);

    // Act
    filter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(filterChain).doFilter(request, response);
    verify(response).addHeader(CorrelationIdUtils.CORRELATION_ID_HEADER, existingCorrelationId);

    // MDC should be cleared after the filter
    assertNull(MDC.get(CorrelationIdUtils.CORRELATION_ID_MDC_KEY));
  }

  @Test
  void doFilterInternal_WithoutExistingCorrelationId_ShouldGenerateNewId() throws Exception {
    // Arrange
    when(request.getHeader(CorrelationIdUtils.CORRELATION_ID_HEADER)).thenReturn(null);

    // Capture the generated correlation ID
    ArgumentCaptor<String> correlationIdCaptor = ArgumentCaptor.forClass(String.class);

    // Act
    filter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(filterChain).doFilter(request, response);
    verify(response).addHeader(eq(CorrelationIdUtils.CORRELATION_ID_HEADER), correlationIdCaptor.capture());

    String generatedCorrelationId = correlationIdCaptor.getValue();
    assertNotNull(generatedCorrelationId);
    assertEquals(36, generatedCorrelationId.length()); // UUID length

    // MDC should be cleared after the filter
    assertNull(MDC.get(CorrelationIdUtils.CORRELATION_ID_MDC_KEY));
  }
}
