package com.change.config.service.impl;

import com.change.config.exception.ConfigChangeNotFoundException;
import com.change.config.dto.ConfigChangeFilterDto;
import com.change.config.model.ConfigChange;
import com.change.config.repository.ConfigChangeRepository;
import com.change.config.service.ConfigChangeService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of the ConfigChangeService that uses in-memory storage.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigChangeServiceImpl implements ConfigChangeService {

  // In-memory storage for configuration changes
  private final ConfigChangeRepository configChangeRepository;

  @Override
  public ConfigChange createConfigChange(ConfigChange configChange) {

    String id = UUID.randomUUID().toString();
    configChange.setId(id);
    configChange.setTimestamp(LocalDateTime.now(ZoneOffset.UTC));
    log.info("Create new config change: {}", configChange);
    return configChangeRepository.save(configChange);
  }

  @Override
  public ConfigChange getConfigChangeById(String id) {

    log.info("Find config change by id: {}", id);
    var configChange = configChangeRepository.findById(id);
    if (configChange == null) {
      throw new ConfigChangeNotFoundException("There is no config change with this id %s".formatted(id));
    }
    return configChange;
  }

  @Override
  public List<ConfigChange> getConfigChangesByFilter(ConfigChangeFilterDto filterDto) {

    log.info("Find config changes by filter: {}", filterDto.toString());
    var stream = configChangeRepository.findAll().stream();

    if (filterDto.getEndTime() != null) {
      stream = stream.filter(e -> e.getTimestamp() .isBefore(filterDto.getEndTime()));
    }
    if (filterDto.getStartTime() != null) {
      stream = stream.filter(e -> e.getTimestamp().isAfter(filterDto.getStartTime()));
    }
    if (filterDto.getType() != null) {
      stream = stream.filter(e -> e.getType().equals(filterDto.getType()));
    }

    if (filterDto.getSort() != null) {
      switch (filterDto.getSort()) {
        case TIME -> stream = stream.sorted(Comparator.comparing(ConfigChange::getTimestamp));
        case TYPE -> stream = stream.sorted(Comparator.comparing(ConfigChange::getType));
      }
    }
    return stream.collect(Collectors.toList());
  }
}