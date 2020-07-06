package com.whoppr.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Order {
  @Id
  private String id;
  private String customerId;
  private List<OrderItem> orderItems;
  private List<OrderEvent> orderEvents;
  private boolean applyRewards;
  private Double amount;
  private Double discount;
  private Double tax;
  @Builder.Default
  private Double gratuity = 0.0;
  private Double totalAmount;
  private Address deliveryAddress;
  private String holdId;
  private String receiptBase64Image;
  private String confirmationId;

  public void addOrderEvent(OrderStatus status) {
    if (orderEvents == null) {
      orderEvents = new ArrayList<>();
    }
    orderEvents.add(OrderEvent.builder()
        .id(UUID.randomUUID().toString())
        .status(status)
        .eventTime(LocalDateTime.now())
        .build());
  }

  @JsonIgnore
  public OrderStatus getOrderStatus() {
    return orderEvents.stream()
        .max(Comparator.comparing(OrderEvent::getEventTime))
        .map(OrderEvent::getStatus)
        .orElse(null);
  }

  @JsonIgnore
  public LocalDateTime getOrderReceivedTime() {
    return orderEvents.stream()
        .filter(orderEvent -> orderEvent.getStatus().equals(OrderStatus.RECEIVED))
        .findFirst()
        .map(OrderEvent::getEventTime)
        .orElseThrow(IllegalStateException::new);
  }

}
