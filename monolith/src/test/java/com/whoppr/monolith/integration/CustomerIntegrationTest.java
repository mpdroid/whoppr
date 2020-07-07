package com.whoppr.monolith.integration;

import com.whoppr.common.model.*;
import com.whoppr.monolith.MonolithicApplication;
import com.whoppr.testutils.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static com.whoppr.testutils.TestDataBuddy.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = MonolithicApplication.class)
@AutoConfigureStubRunner(
    ids = "com.whoppr:menu:+:stubs:8085",
    stubsMode = StubRunnerProperties.StubsMode.LOCAL)
class CustomerIntegrationTest extends IntegrationTestBase {

  List<MenuItem> testMenuItems;
  Customer testCustomer;
  Order testOrder;
  ThreadLocal<Order> orderWithTotalThreadLocal = new ThreadLocal<Order>();

  @Autowired
  public CustomerIntegrationTest(
      WebApplicationContext webApplicationContext,
      FilterChainProxy springSecurityFilter
  ) {
    super(webApplicationContext, springSecurityFilter);
  }

  @BeforeEach
  void setup() throws Exception {
    testMenuItems = buildTestMenuItems();
    testCustomer = createTestCustomer();
    testOrder = createTestOrder(testCustomer);
  }

  @Test
  void checkoutShoppingCart() throws Exception {
    whenCustomerChecksOutShoppingCart();
    ThenOrderTotalIsComputed();
  }

  private void whenCustomerChecksOutShoppingCart() throws Exception {
    ShoppingCart cart = buildTestShoppingCart(
        buildTestOrderItems(testMenuItems)
    );

    Order actualOrder = executePost("/checkout", cart, Order.class);
    orderWithTotalThreadLocal.set(actualOrder);
  }

  private void ThenOrderTotalIsComputed() {
    assertEquals(10.79, orderWithTotalThreadLocal.get().getTotalAmount());
  }

  @Test
  void confirmOrder() throws Exception {
    WhenCustomerSubmitsOrder();
    ThenPaymentIsProcessed();
    AndOrderIsSentToKitchen();
  }

  private void WhenCustomerSubmitsOrder() throws Exception {
    executePost("/order/confirm", testOrder);
  }

  private void ThenPaymentIsProcessed() throws Exception {
    Order savedOrder = executeGet("/order/" + testOrder.getId(), Order.class);
    assertNotNull(savedOrder.getHoldId());
  }

  private void AndOrderIsSentToKitchen() throws Exception {
    Order savedOrder = executeGet("/order/" + testOrder.getId(), Order.class);
    assertEquals(OrderStatus.RECEIVED, savedOrder.getOrderStatus());
  }
}
