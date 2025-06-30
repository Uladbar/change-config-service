package com.change.config.dto;

import com.change.config.model.ConfigChangeType;
import com.change.config.model.SortType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilterDto {

  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private ConfigChangeType type;
  private SortType sort;
}
