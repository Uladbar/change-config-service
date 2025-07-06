package com.change.config.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

  @Bean
  public OpenAPI configOpenAPI() {
    Server localServer = new Server()
        .url("http://localhost:8080")
        .description("Local Development Server");

    Info info = new Info()
        .title("Config Change Service API")
        .version("1.0.0")
        .description("API for managing configuration changes");

    return new OpenAPI()
        .info(info)
        .servers(List.of(localServer));
  }
}