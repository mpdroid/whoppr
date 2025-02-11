package com.whoppr.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
  @Id
  private String id;
  private String menuItemId;
  private Integer quantity;
}
