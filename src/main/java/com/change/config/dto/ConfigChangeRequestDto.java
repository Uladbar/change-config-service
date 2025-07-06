package com.change.config.dto;

import com.change.config.validation.ValidValueForChangeType;
import com.change.config.model.ConfigChangeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for receiving configuration change requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidValueForChangeType(message = "Value is required for non-delete update, and is required for other types")
public class ConfigChangeRequestDto {

  @NotNull(message = "Type is required")
  private ConfigChangeType type;

  @NotBlank(message = "Key is required")
  private String key;

  private String value;

  private String note;

  private boolean critical;
}