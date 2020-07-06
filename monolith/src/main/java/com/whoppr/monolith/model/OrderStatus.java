package com.whoppr.monolith.model;

public enum OrderStatus {
  RECEIVED("Received..."),
  PREPARING("Preparing..."),
  READY("Waiting for pickup..."),
  ENROUTE("En route..."),
  DELIVERED("Delivered.");

  private String displayName;

  OrderStatus(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return this.displayName;
  }
}
