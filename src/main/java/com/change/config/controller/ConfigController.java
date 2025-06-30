package com.change.config.controller;

import com.change.config.dto.ConfigChangeRequestDto;
import com.change.config.dto.ConfigChangeResponseDto;
import com.change.config.dto.FilterDto;
import com.change.config.mapper.ConfigChangeMapper;
import com.change.config.model.ConfigChange;
import com.change.config.model.ConfigChangeType;
import com.change.config.model.SortType;
import com.change.config.service.ConfigChangeService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/configs")
@RequiredArgsConstructor
public class ConfigController {

  private final ConfigChangeService configChangeService;
  private final ConfigChangeMapper configChangeMapper;

  /**
   * Creates a new configuration change.
   *
   * @param requestDto The configuration change request DTO
   * @return The created configuration change response DTO
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ConfigChangeResponseDto createConfigChange(@Valid @RequestBody ConfigChangeRequestDto requestDto) {
    ConfigChange configChange = configChangeMapper.toEntity(requestDto);

    ConfigChange savedChange = configChangeService.createConfigChange(configChange);

    return configChangeMapper.toDto(savedChange);
  }

  /**
   * Retrieves a configuration change by its ID.
   *
   * @param id The ID of the configuration change
   * @return The configuration change response DTO, or 404 if not found
   */
  @GetMapping("/{id}")
  public ResponseEntity<ConfigChangeResponseDto> getConfigChangeById(@PathVariable String id) {
    ConfigChange configChange = configChangeService.getConfigChangeById(id);
    return ResponseEntity.ok(configChangeMapper.toDto(configChange));
  }

  /**
   * Lists configuration changes filtered by time range and type.
   *
   * @param startTime The start time
   * @param endTime   The end time
   * @return List of configuration change response DTOs within the time range
   */
  @GetMapping
  public ResponseEntity<List<ConfigChangeResponseDto>> listConfigChangesByTime(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
      @RequestParam(required = false) ConfigChangeType type,
      @RequestParam(required = false) SortType sort) {

    var filter = FilterDto.builder()
        .startTime(startTime)
        .endTime(endTime)
        .sort(sort)
        .type(type).build();

    List<ConfigChange> configChanges = configChangeService.getConfigChangesByFilter(filter);

    return ResponseEntity.ok(configChanges.stream()
        .map(configChangeMapper::toDto)
        .collect(Collectors.toList()));
  }
}
