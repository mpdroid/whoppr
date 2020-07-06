package com.whoppr.monolith.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCart {
  @Id
  private String id;
  private String customerId;
  private ShoppingCart shoppingCart;
  private List<OrderItem> cartItems;
  private Address deliveryAddress;
  @Builder.Default
  private boolean applyRewards = false;
  @Builder.Default
  private Double earnedRewards = 0.0;
  @Builder.Default
  private Double gratuity = 0.0;

}
