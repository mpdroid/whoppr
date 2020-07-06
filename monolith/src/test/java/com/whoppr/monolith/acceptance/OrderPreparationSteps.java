package com.whoppr.monolith.acceptance;

import com.whoppr.monolith.model.KanBan;
import com.whoppr.monolith.model.Order;
import com.whoppr.monolith.model.Recipe;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderPreparationSteps extends StepExecutionBase {

  @Before
  public void beforeEachScenario() throws Exception {
    setupMenu();
    setupCustomer();
  }

  @When("^the chef picks up the order for preparation$")
  public void chefPicksUpOrder() throws Exception {
    KanBan kitchenKanBan = executePost("kanban/kitchen/pull", null, KanBan.class);
    testContext().set("kitchenKanBan", kitchenKanBan);
  }

  @Then("^the order is removed from kitchen queue$")
  public void orderRemovedFromKitchenQueue() {
    assertThrows(Exception.class, () -> {
      executePost("kanban/kitchen/pull", null, KanBan.class);
    });
  }

  @When("^the chef looks up recipe for an order item$")
  public void chefLooksUpRecipe() throws Exception {
    KanBan kitchenKanBan = testContext().get("kitchenKanBan");
    Order actualCustomerOrder = executeGet("order/" + kitchenKanBan.getOrderId(), Order.class);
    Recipe recipe = executeGet("menu-item/"
        + actualCustomerOrder.getOrderItems().get(0).getMenuItemId()
        + "/recipe", Recipe.class);
    testContext().set("actualRecipe", recipe);
  }

  @Then("^the recipe is provided$")
  public void recipeIsProvided() throws Exception {
    Recipe actualRecipe = testContext().get("actualRecipe");
    assertEquals(menuItems.get(0).getRecipe(), actualRecipe);
  }

  @When("^the chef completes preparing the order$")
  public void chefCompletesOrder() throws Exception {
    KanBan kitchenKanBan = testContext().get("kitchenKanBan");
    executePost("kanban/kitchen/put", kitchenKanBan);
  }

  @Then(".*order.*in.*delivery queue$")
  public void orderIsInDeliveryQueue() throws Exception {
    KanBan kitchenKanBan = testContext().get("kitchenKanBan");
    KanBan driverkanBan = executePost("kanban/delivery/pull", null, KanBan.class);
    assertEquals(kitchenKanBan, driverkanBan);
  }
}
