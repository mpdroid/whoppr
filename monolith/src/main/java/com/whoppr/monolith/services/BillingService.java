package com.whoppr.monolith.services;

import com.whoppr.common.exceptions.PaymentDeclinedException;
import com.whoppr.common.model.Address;
import com.whoppr.common.model.Order;
import com.whoppr.common.model.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.UUID;

@RestController
@Service
public class BillingService {

  @Autowired
  MenuClient menuClient;

  public Order computeOrderTotal(ShoppingCart cart) {
    Order order = Order.builder()
        .customerId(cart.getCustomerId())
        .orderItems(cart.getCartItems())
        .applyRewards(cart.isApplyRewards())
        .gratuity(cart.getGratuity())
        .deliveryAddress(cart.getDeliveryAddress())
        .orderEvents(new ArrayList<>())
        .build();
    computeOrderItemTotal(order);
    applyRewards(order, cart.getEarnedRewards());
    computeSalesTax(order);
    computeTotal(order);
    return order;
  }

  private void computeOrderItemTotal(Order order) {
    Double amount = order.getOrderItems().stream()
        .map(orderItem ->
            orderItem.getQuantity()
                * getUnitPrice(orderItem.getMenuItemId()))
        .reduce(0.00, (a, b) -> a + b);
    order.setAmount(amount);
  }

  private Double getUnitPrice(String menuItemId) {
    return menuClient
        .getMenuItem(menuItemId)
        .getUnitPrice();
  }

  private void applyRewards(Order order, Double earnedRewards) {
    if (order.isApplyRewards()) {
      order.setDiscount(
          Math.min(
              earnedRewards,
              order.getAmount()
          )
      );

    }
  }

  private void computeSalesTax(Order order) {
    order.setTax(
        round((order.getAmount() - order.getDiscount())
            * taxRate(order.getDeliveryAddress())
        )
    );
  }

  private Double round(Double unrounded) {
    return Math.round(
        (unrounded) * 100.0
    ) / 100.0;
  }

  private Double taxRate(Address deliveryAddress) {
    return 0.1;
  }

  private void computeTotal(Order order) {
    order.setTotalAmount(
        round(order.getAmount()
            - order.getDiscount()
            + order.getTax()
            + order.getGratuity())
    );
  }

  public String processPaymentHold(String customerToken, Double amount) {
    if (!customerToken.equals("joshua")) {
      throw new PaymentDeclinedException();
    } else {
      return UUID.randomUUID().toString();
    }
  }

  public String confirmPayment(String holdId, Double totalAmount) {
    return UUID.randomUUID().toString();
  }
}
