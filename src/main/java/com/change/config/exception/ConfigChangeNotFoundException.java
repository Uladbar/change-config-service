package com.change.config.exception;

public class ConfigChangeNotFoundException extends RuntimeException {

  public ConfigChangeNotFoundException(String message) {
    super(message);
  }
}
