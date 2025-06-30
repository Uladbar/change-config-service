package com.change.config.controller;

import com.change.config.exception.ConfigChangeNotFoundException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@ControllerAdvice
public class ConfigControllerAdvice {

  /**
   * Handles validation errors and returns appropriate error responses.
   *
   * @param ex The validation exception
   * @return Map of field errors
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    Map<String, Map<String, String>> response = new HashMap<>();
    response.put("errors", errors);
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(HttpMessageNotReadableException ex) {
    Map<String, String> response = new HashMap<>();
    var message = ex.getMessage();
    if (ex.getCause() instanceof InvalidFormatException cause) {
      if (cause.getTargetType().isEnum()) {
        message = ("Invalid enum value: " + cause.getValue() + ". Allowed values are: " + Arrays.toString(
            cause.getTargetType().getEnumConstants()));
      }
    }
    response.put("errors", message);
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    Map<String, String> response = new HashMap<>();
    if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
      Class<?> enumType = ex.getRequiredType();
      Object[] enumConstants = enumType.getEnumConstants();
      response.put("errors", "Invalid value '%s' for parameter '%s'. Allowed values are: %s.".formatted(
          ex.getValue(),
          ex.getName(),
          Arrays.toString(enumConstants)
      ));
    } else {
      response.put("errors", "Invalid value '%s' for parameter '%s'".formatted(ex.getValue(), ex.getName()));
    }
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(ConfigChangeNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(ConfigChangeNotFoundException ex) {
    log.info(ex.getMessage());
    return ResponseEntity.notFound().build();
  }
}
