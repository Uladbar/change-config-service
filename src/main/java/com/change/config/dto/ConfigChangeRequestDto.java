package com.change.config.dto;

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
public class ConfigChangeRequestDto {
    
    @NotNull(message = "Type is required")
    private ConfigChangeType type;
    
    @NotBlank(message = "Key is required")
    private String key;
    
    @NotBlank(message = "Value is required")
    private String value;
    
    private String description;
    
    private boolean critical;
}