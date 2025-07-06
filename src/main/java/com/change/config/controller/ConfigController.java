package com.change.config.controller;

import com.change.config.dto.ConfigChangeRequestDto;
import com.change.config.dto.ConfigChangeResponseDto;
import com.change.config.dto.FilterDto;
import com.change.config.mapper.ConfigChangeMapper;
import com.change.config.model.ConfigChange;
import com.change.config.service.ConfigChangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/configs")
@RequiredArgsConstructor
@Tag(name = "Configuration Changes", description = "API for managing configuration changes")
public class ConfigController {

  private final ConfigChangeService configChangeService;
  private final ConfigChangeMapper configChangeMapper;

  /**
   * Creates a new configuration change.
   *
   * @param requestDto The configuration change request DTO
   * @return The created configuration change response DTO
   */
  @Operation(
      summary = "Create a new configuration change",
      description = "Creates a new configuration change with the provided details"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "Configuration change created successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ConfigChangeResponseDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid input data",
          content = @Content
      )
  })

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<ConfigChangeResponseDto> createConfigChange(
      @Parameter(description = "Configuration change details", required = true)
      @Valid @RequestBody ConfigChangeRequestDto requestDto) {
    ConfigChange configChange = configChangeMapper.toEntity(requestDto);

    ConfigChange savedChange = configChangeService.createConfigChange(configChange);
    ConfigChangeResponseDto responseDto = configChangeMapper.toDto(savedChange);

    return ResponseEntity.created(buildUri(savedChange)).body(responseDto);
  }

  private URI buildUri(ConfigChange configChange) {
    return ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(configChange.getId())
        .toUri();
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
   * Lists configuration changes filtered by time range, type, and sort order.
   *
   * @param filter The filter parameters containing startTime, endTime, type, and sort
   * @return List of configuration change response DTOs matching the filter criteria
   */
  @Operation(
      summary = "List configuration changes with filters",
      description = "Retrieves a list of configuration changes filtered by time range, type, and sort order"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Successfully retrieved list of configuration changes",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ConfigChangeResponseDto.class)
          )
      )
  })
  @GetMapping
  public ResponseEntity<List<ConfigChangeResponseDto>> listConfigChangesByFilter(
      @Parameter(description = "Filter parameters for configuration changes")
      @ParameterObject FilterDto filter) {

    List<ConfigChange> configChanges = configChangeService.getConfigChangesByFilter(filter);

    return ResponseEntity.ok(configChanges.stream()
        .map(configChangeMapper::toDto)
        .collect(Collectors.toList()));
  }
}
