package com.change.config.dto;

import com.change.config.model.ConfigChangeType;
import com.change.config.model.SortType;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
public class FilterDto {

  @Parameter(description = "Start time for filtering (ISO format)")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  LocalDateTime startTime;

  @Parameter(description = "End time for filtering (ISO format)")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  LocalDateTime endTime;

  @Parameter(description = "Configuration change type filter")
  ConfigChangeType type;

  @Parameter(description = "Sort order for results")
  SortType sort;
}
