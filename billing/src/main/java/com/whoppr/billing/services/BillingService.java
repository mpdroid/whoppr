package com.whoppr.billing.services;

import com.whoppr.common.clients.BillingClient;
import com.whoppr.common.clients.MenuClient;
import com.whoppr.common.exceptions.PaymentDeclinedException;
import com.whoppr.common.model.Address;
import com.whoppr.common.model.Order;
import com.whoppr.common.model.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.UUID;

@RestController
@Service
public class BillingService implements BillingClient {

  @Autowired
  MenuClient menuClient;

  @Override
  @PostMapping(value = "/compute-total",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Order computeOrderTotal(@RequestBody ShoppingCart cart) {
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
    } else {
      order.setDiscount(0.0);
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

  @Override
  @PostMapping(value = "/payment-hold/{customerToken}/{amount}", produces = "application/json")
  public String processPaymentHold(@PathVariable("customerToken") String customerToken,
                                   @PathVariable("amount") Double amount) {
    if (!customerToken.equals("joshua")) {
      throw new PaymentDeclinedException();
    } else {
      return UUID.randomUUID().toString();
    }
  }

  @Override
  @PostMapping(value = "/payment-confirm/{holdId}/{amount}", produces = "application/json")
  public String confirmPayment(@PathVariable("holdId") String holdId, @PathVariable("amount") Double totalAmount) {
    return UUID.randomUUID().toString();
  }
}
