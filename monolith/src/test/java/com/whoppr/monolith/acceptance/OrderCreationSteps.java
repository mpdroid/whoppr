package com.whoppr.monolith.acceptance;

import com.whoppr.monolith.TestUtils;
import com.whoppr.monolith.model.*;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderCreationSteps extends StepExecutionBase {

  @Before
  public void beforeEachScenario() throws Exception {
    setupMenu();
    setupCustomer();
  }

  @When("^customer requests to view the menu$")
  public void customerRequestsMenu() throws Exception {
    List<MenuItem> actualMenuItems = executeGetList("menu-items",
        new ParameterizedTypeReference<List<MenuItem>>() {
        }
    );
    testContext().set("actualMenuItems", actualMenuItems);
  }

  @Then("^menu is provided to the customer$")
  public void menuIsProvided() {
    testContext().assertEquals("expectedMenuItems", "actualMenuItems");
  }

  @When("^customer requests to view their profile$")
  public void customerRequestsProfile() throws Exception {
    testContext().set("actualCustomer",
        executeGet(
            "customer/" + testCustomer.getId(),
            Customer.class)
    );
  }

  @Then("^profile is provided to the customer$")
  public void profileIsProvided() throws Exception {
    testContext().assertEquals("mockCustomer", "actualCustomer");
  }

  @When("^the customer checks out their shopping cart$")
  public void customerSelectsItemsToViewAmount() throws Exception {

    ShoppingCart cart = TestUtils.buildTestShoppingCart(
        TestUtils.buildTestOrderItems(menuItems)
    );
    Order orderWithPricing = executePost("checkout", cart, Order.class);
    testContext().set("orderWithPricing", orderWithPricing);
  }

  @Then("^the total order amount is provided")
  public void orderAmountProvided() {
    Order orderWithPricing = testContext().get("orderWithPricing");
    assertEquals(9.99, orderWithPricing.getAmount());
    assertEquals(0.80, orderWithPricing.getTax());
    assertEquals(2.00, orderWithPricing.getDiscount());
    assertEquals(2.00, orderWithPricing.getGratuity());
    assertEquals(10.79, orderWithPricing.getTotalAmount());
  }

  @Given("^a customer order$")
  public void customerSubmitsOrder() throws Exception {
    executeDelete("orders");

    Order testOrder = TestUtils.buildTestOrder(testCustomer);
    testContext().set("customerOrder", testOrder);
    executePost("order/confirm", testOrder);
  }

  @Then("^payment is processed$")
  public void paymentIsProcessed() throws Exception {
    Order testOrder = testContext().get("customerOrder");
    Order actualCustomerOrder = executeGet("order/" + testOrder.getId(), Order.class);
    assertNotNull(true, actualCustomerOrder.getHoldId());
  }

  @Then(".*order is sent to the kitchen$")
  public void orderIsInKitchenQueue() throws Exception {
    KanBan kanBan = executePost("kanban/kitchen/pull", null, KanBan.class);
    Order testOrder = testContext().get("customerOrder");
    assertEquals(testOrder.getId(), kanBan.getOrderId());
  }

  @Then("^the order status is set to \"(.*)\"$")
  public void orderIsInStatus(String expectedStatus) throws Exception {
    Order customerOrder = testContext().get("customerOrder");
    String actualStatus = executeGet("order-status/" + customerOrder.getId(), String.class);
    assertEquals(expectedStatus, actualStatus);
  }

  @When("^the customer checks the status of their order$")
  public void checkOrderStatus() throws Exception {
    Order customerOrder = testContext().get("customerOrder");
    String actualCustomerOrderStatus = executeGet("order-status/" + customerOrder.getId(), String.class);
    testContext().set("actualCustomerOrderStatus", actualCustomerOrderStatus);
  }

  @Then("^the order status is shown to the customer$")
  public void orderIsInStatus() throws Exception {
    String actualStatus = testContext().get("actualCustomerOrderStatus");
    assertEquals("Received...", actualStatus);
  }
}
