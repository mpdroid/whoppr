package com.whoppr.monolith.integration;

import com.whoppr.monolith.MonolithicApplication;
import com.whoppr.monolith.model.Customer;
import com.whoppr.monolith.model.MenuItem;
import com.whoppr.monolith.model.Order;
import com.whoppr.monolith.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

import static com.whoppr.monolith.TestUtils.buildTestReceipt;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = MonolithicApplication.class)
class OrderIntegrationTest extends IntegrationTestBase {

  List<MenuItem> testMenuItems;
  Customer testCustomer;
  Order testOrder;
  File testReceipt;
  ThreadLocal<Order> orderWithTotalThreadLocal = new ThreadLocal<Order>();

  @Autowired
  public OrderIntegrationTest(
      WebApplicationContext webApplicationContext,
      FilterChainProxy springSecurityFilter
  ) {
    super(webApplicationContext, springSecurityFilter);
  }

  @BeforeEach
  void setup() throws Exception {
    testMenuItems = createTestMenuItems();
    testCustomer = createTestCustomer();
    testOrder = createTestOrder(testCustomer);
    testReceipt = buildTestReceipt();

  }

  @Test
  void scanCustomerReceipt() throws Exception {
    whenDriverScansCustomerReceipt();
    thenReceiptIsSaved();
    andOrderStatusIsUpdated();
  }

  private void whenDriverScansCustomerReceipt() throws Exception {
    executeUpload(
        "/receipt/" + testOrder.getId(),
        testReceipt
    );
  }

  private void thenReceiptIsSaved() throws Exception {
    Order actualOrder = executeGet("/order/" + testOrder.getId(), Order.class);
    assertEquals(
        Base64.getEncoder().encodeToString(Files.readAllBytes(testReceipt.toPath())),
        actualOrder.getReceiptBase64Image());
  }

  private void andOrderStatusIsUpdated() throws Exception {
    Order actualOrder = executeGet("/order/" + testOrder.getId(), Order.class);
    assertEquals(
        OrderStatus.DELIVERED,
        actualOrder.getOrderStatus());

  }

}
