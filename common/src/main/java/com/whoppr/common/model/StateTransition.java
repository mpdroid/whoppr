package com.whoppr.common.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StateTransition {
  private OrderStatus current;
  private OrderStatus next;
}
