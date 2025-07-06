package com.change.config.mapper;

import com.change.config.dto.ConfigChangeRequestDto;
import com.change.config.dto.ConfigChangeResponseDto;
import com.change.config.model.ConfigChange;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between ConfigChange domain model and DTOs.
 */
@Component
public class ConfigChangeMapper {

  public ConfigChange toEntity(ConfigChangeRequestDto requestDto) {
    if (requestDto == null) {
      return null;
    }

    return ConfigChange.builder()
        .type(requestDto.getType())
        .key(requestDto.getKey())
        .value(requestDto.getValue())
        .note(requestDto.getNote())
        .critical(requestDto.isCritical())
        .build();
  }

  public ConfigChangeResponseDto toDto(ConfigChange entity) {
    if (entity == null) {
      return null;
    }

    return ConfigChangeResponseDto.builder()
        .id(entity.getId())
        .type(entity.getType())
        .key(entity.getKey())
        .value(entity.getValue())
        .note(entity.getNote())
        .critical(entity.isCritical())
        .timestamp(entity.getTimestamp())
        .build();
  }
}