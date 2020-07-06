package com.whoppr.monolith.services;

import com.whoppr.monolith.exceptions.PaymentDeclinedException;
import com.whoppr.monolith.model.MenuItem;
import com.whoppr.monolith.model.Order;
import com.whoppr.monolith.model.OrderItem;
import com.whoppr.monolith.model.ShoppingCart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static com.whoppr.monolith.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class BillingServiceTest {

  @InjectMocks
  private BillingService billingService;

  @Mock
  private MenuService menuService;

  List<MenuItem> menuItems = buildTestMenuItems();
  List<OrderItem> orderItems = buildTestOrderItems(menuItems);

  @BeforeEach
  void setUp() {
    initMocks(this);
    when(menuService.getMenuItem(menuItems.get(0).getId()))
        .thenReturn(menuItems.get(0));
  }

  @Test
  void checkoutShoppingCart() {
    ShoppingCart shoppingCart = buildTestShoppingCart(orderItems);
    Order actualOrder = billingService.computeOrderTotal(shoppingCart);
    assertThat(actualOrder).isEqualTo(
        Order.builder()
            .customerId(shoppingCart.getCustomerId())
            .applyRewards(shoppingCart.isApplyRewards())
            .orderItems(shoppingCart.getCartItems())
            .orderEvents(new ArrayList<>())
            .deliveryAddress(shoppingCart.getDeliveryAddress())
            .amount(9.99)
            .tax(0.8)
            .discount(2.0)
            .gratuity(shoppingCart.getGratuity())
            .totalAmount(10.79)
            .build()
    );
  }

  @Test
  void processPaymentHold() {

    assertNotNull(billingService.processPaymentHold("joshua", 12.99));

    assertThrows(PaymentDeclinedException.class, () -> {
      billingService.processPaymentHold("not-joshua", 12.99);
    });
  }
}
