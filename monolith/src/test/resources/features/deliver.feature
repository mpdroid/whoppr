Feature: 3 - Deliver
  Driver delivers order

  Scenario: Delivery driver picks up customer order for delivery
    Given a customer order
    And the chef picks up the order for preparation
    And the chef completes preparing the order
    When the delivery driver picks up the order for delivery
    Then the order is removed from the delivery queue
    And the order status is set to "En route..."

  Scenario: Delivery driver delivers order to customer
    Given a customer order
    And the chef picks up the order for preparation
    And the chef completes preparing the order
    And the delivery driver picks up the order for delivery
    When the delivery driver scans in the customer signed receipt
    Then the order status is set to "Delivered."
    And the receipt is saved with the order

