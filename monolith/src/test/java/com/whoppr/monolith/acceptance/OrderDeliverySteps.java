package com.whoppr.monolith.acceptance;

import com.whoppr.monolith.model.KanBan;
import com.whoppr.monolith.model.Order;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.nio.file.Files;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderDeliverySteps extends StepExecutionBase {

  @Before
  public void beforeEachScenario() throws Exception {
    setupMenu();
    setupCustomer();
    setupTestReceipt();
  }

  @When("^the delivery driver picks up the order for delivery$")
  public void deliveryDriverPicksUpOrder() throws Exception {
    KanBan kanBan = executePost("kanban/delivery/pull", null, KanBan.class);
    testContext().set("deliveryKanBan", kanBan);
  }

  @Then("^the order is removed from the delivery queue$")
  public void orderRemovedFromDeliveryQueue() {
    assertThrows(Exception.class, () -> {
      executePost("kanban/delivery/pull", null, KanBan.class);
    });
  }

  @When("^the delivery driver scans in the customer signed receipt$")
  public void driverScansReceipt() throws Exception {
    KanBan deliveryKanBan = testContext().get("deliveryKanBan");

    executeUpload(
        "receipt/" + deliveryKanBan.getOrderId(),
        receipt
    );
  }

  @Then("^the receipt is saved with the order$")
  public void receiptIsSaved() throws Exception {
    KanBan deliveryKanBan = testContext().get("deliveryKanBan");
    Order deliveredOrder = executeGet("order/"
        + deliveryKanBan.getOrderId(), Order.class);
    String base64Receipt = deliveredOrder.getReceiptBase64Image();
    byte[] actualBytes = Base64.getDecoder().decode(base64Receipt);
    assertEquals(Base64.getEncoder().encodeToString(Files.readAllBytes(receipt.toPath())), base64Receipt);
  }
}
