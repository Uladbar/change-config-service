package com.change.config.service;

import com.change.config.dto.FilterDto;
import com.change.config.model.ConfigChange;
import java.util.List;

/**
 * Service for managing configuration changes.
 */
public interface ConfigChangeService {

  /**
   * Creates a new configuration change.
   *
   * @param configChange The configuration change to create
   * @return The created configuration change with generated ID and timestamp
   */
  ConfigChange createConfigChange(ConfigChange configChange);

  /**
   * Retrieves a configuration change by its ID.
   *
   * @param id The ID of the configuration change
   * @return The configuration change, or null if not found
   */
  ConfigChange getConfigChangeById(String id);

  /**
   * Lists configuration changes filtered by time range and type.
   *
   * @param filterDto Container for filter params
   * @return List of configuration changes within the time range
   */
  List<ConfigChange> getConfigChangesByFilter(FilterDto filterDto);
}