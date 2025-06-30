package com.change.config.repository;

import com.change.config.model.ConfigChange;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ConfigChangeRepositoryImpl implements ConfigChangeRepository {

  // In-memory storage for configuration changes
  private final Map<String, ConfigChange> configChanges = new ConcurrentHashMap<>();

  @Override
  public ConfigChange save(ConfigChange change) {

    configChanges.put(change.getId(), change);
    // Map.put(...) return null or previous value, so get value by id to confirm that saved
    return configChanges.get(change.getId());
  }

  @Override
  public ConfigChange findById(String id) {
    return configChanges.get(id);
  }

  @Override
  public Collection<ConfigChange> findAll() {
    return configChanges.values();
  }
}
