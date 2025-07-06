package com.change.config.dto;

import com.change.config.model.ConfigChangeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for sending configuration change responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigChangeResponseDto {
    
    private String id;
    
    private ConfigChangeType type;
    
    private String key;
    
    private String value;
    
    private String note;
    
    private boolean critical;
    
    private LocalDateTime timestamp;
}