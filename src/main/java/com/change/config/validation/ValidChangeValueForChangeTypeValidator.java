package com.change.config.validation;

import com.change.config.dto.ConfigChangeRequestDto;
import com.change.config.model.ConfigChangeType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidChangeValueForChangeTypeValidator implements ConstraintValidator<ValidValueForChangeType, ConfigChangeRequestDto> {

  @Override
  public boolean isValid(ConfigChangeRequestDto configChange, ConstraintValidatorContext context) {
    if (configChange == null) {
      return false;
    }
    try {
      boolean shouldInvert = Optional.of(configChange)
          .map(ConfigChangeRequestDto::getType)
          .map(ConfigChangeType.DELETE::equals)
          .orElse(false);
      boolean isValueValid = Optional.of(configChange).map(ConfigChangeRequestDto::getValue).isPresent();
      return shouldInvert != isValueValid;

    } catch (Exception e) {
      log.error("Exception during validate required field: {}", e.getMessage(), e);
      return false;
    }
  }
}
