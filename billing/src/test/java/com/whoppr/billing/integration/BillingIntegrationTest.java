package com.whoppr.billing.integration;

import com.whoppr.billing.BillingApplication;
import com.whoppr.common.model.Customer;
import com.whoppr.common.model.MenuItem;
import com.whoppr.common.model.Order;
import com.whoppr.common.model.ShoppingCart;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BillingApplication.class)
@AutoConfigureStubRunner(
    ids = "com.whoppr:menu:+:stubs:8095",
    stubsMode = StubRunnerProperties.StubsMode.LOCAL)
class BillingIntegrationTest extends IntegrationTestBase {

  List<MenuItem> testMenuItems;
  Customer testCustomer;
  Order testOrder;
  ThreadLocal<Order> orderWithTotalThreadLocal = new ThreadLocal<Order>();

  @Autowired
  public BillingIntegrationTest(
      WebApplicationContext webApplicationContext,
      FilterChainProxy springSecurityFilter
  ) {
    super(webApplicationContext, springSecurityFilter);
  }

  @BeforeEach
  void setup() throws Exception {
    testMenuItems = buildTestMenuItems();
  }

  @Test
  void computeTotal() throws Exception {
    whenThereIsABillComputeRequest();
    ThenOrderTotalIsComputed();
  }

  private void whenThereIsABillComputeRequest() throws Exception {
    ShoppingCart cart = buildTestShoppingCart(
        buildTestOrderItems(testMenuItems)
    );

    Order actualOrder = executePost("/compute-total", cart, Order.class);
    orderWithTotalThreadLocal.set(actualOrder);
  }

  private void ThenOrderTotalIsComputed() {
    assertEquals(10.79, orderWithTotalThreadLocal.get().getTotalAmount());
  }

}
