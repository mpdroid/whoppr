package com.whoppr.monolith.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuItem {
  @Id
  private String id;
  private String name;
  private Double unitPrice;
  private Recipe recipe;
}
