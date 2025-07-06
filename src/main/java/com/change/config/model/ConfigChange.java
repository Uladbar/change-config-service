package com.change.config.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * Model class representing a configuration change.
 */
@Data
@Builder
public class ConfigChange {
    
    private String id;

    private ConfigChangeType type;

    private String key;
    private String value;

    private String note;

    private boolean critical;

    private LocalDateTime timestamp;
}