package com.whoppr.monolith.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent {
  @Id
  private String id;
  private OrderStatus status;
  private LocalDateTime eventTime;
}
