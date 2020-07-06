Feature: 2 - Cook
  Chef prepares order

  Scenario: Chef works on a customer order
    Given a customer order
    When the chef picks up the order for preparation
    Then the order status is set to "Preparing..."
    And the order is removed from kitchen queue

  Scenario: Chef looks up recipe for order item
    Given a customer order
    And the chef picks up the order for preparation
    When the chef looks up recipe for an order item
    Then the recipe is provided

  Scenario: Chef completes the customer order
    Given a customer order
    And the chef picks up the order for preparation
    When the chef completes preparing the order
    Then the order status is set to "Waiting for pickup..."
    And the order moves into delivery queue

