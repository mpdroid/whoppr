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
public class Customer {
  @Id
  private String id;
  private String name;
  private String customerToken;
  @Builder.Default
  private Double earnedRewardAmount = 0.0;
  private Address deliveryAddress;
}
