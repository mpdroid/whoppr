package com.whoppr.monolith.services;

import com.whoppr.monolith.model.OrderStatus;
import com.whoppr.monolith.model.StateTransition;

import java.util.HashMap;
import java.util.Map;

import static com.whoppr.monolith.model.OrderStatus.*;

public class StateMachine {

  private StateMachine() {
  }

  private static Map<String, StateTransition> transitions =
      new HashMap<>();

  static {
    addEntry(RECEIVED, "kitchen", "pull", PREPARING);
    addEntry(PREPARING, "kitchen", "put", READY);
    addEntry(READY, "delivery", "pull", ENROUTE);
    addEntry(ENROUTE, "delivery", "put", DELIVERED);
  }

  private static void addEntry(OrderStatus current, String process, String action, OrderStatus next) {
    transitions.put(
        process + "-" + action,
        StateTransition.builder()
            .current(current)
            .next(next)
            .build()
    );
  }

  public static StateTransition getTransition(String process, String action) {
    try {
      return transitions.get(process + "-" + action);
    } catch (Exception ex) {
      throw new RuntimeException("Invalid workflow");
    }
  }

}
