package com.change.config.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.change.config.exception.ConfigChangeNotFoundException;
import com.change.config.model.ConfigChange;
import com.change.config.model.ConfigChangeType;
import com.change.config.repository.ConfigChangeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConfigChangeServiceImplTest {

  @Mock
  private ConfigChangeRepository configChangeRepository;

  @InjectMocks
  private ConfigChangeServiceImpl configChangeService;


  @Test
  void createConfigChange_ShouldGenerateIdAndTimestamp() {
    // Arrange
    ConfigChange inputChange = ConfigChange.builder()
        .key("app.feature.enabled")
        .value("true")
        .type(ConfigChangeType.ADD)
        .description("Enable new feature")
        .critical(true)
        .build();

    when(configChangeRepository.save(inputChange)).thenAnswer(invocation -> invocation.<ConfigChange>getArgument(0));

    ConfigChange result = configChangeService.createConfigChange(inputChange);

    assertNotNull(result);
    assertNotNull(result.getId());
    assertNotNull(result.getTimestamp());

    assertEquals(inputChange.getKey(), result.getKey());
    assertEquals(inputChange.getValue(), result.getValue());
    assertEquals(inputChange.getType(), result.getType());
    assertEquals(inputChange.getDescription(), result.getDescription());
    assertEquals(inputChange.isCritical(), result.isCritical());
  }

  @Test
  void getConfigChangeById_WhenIdNotFound_ShouldReturnNull() {
    when(configChangeRepository.findById("nonexistent")).thenReturn(null);

    assertThrows(ConfigChangeNotFoundException.class, () -> configChangeService.getConfigChangeById("nonexistent"));

    verify(configChangeRepository).findById("nonexistent");
  }
}
