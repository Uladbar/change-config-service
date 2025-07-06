package com.change.config.repository;

import com.change.config.model.ConfigChange;
import java.util.Collection;

public interface ConfigChangeRepository {

  ConfigChange save(ConfigChange change);

  ConfigChange findById(String id);

  Collection<ConfigChange> findAll();
}
