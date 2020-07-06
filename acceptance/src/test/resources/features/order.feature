Feature: 1 - Order
  Customer creates order

  Scenario: Customer views the menu
    When customer requests to view the menu
    Then menu is provided to the customer

  Scenario: Customer views their account
    When customer requests to view their profile
    Then profile is provided to the customer

  Scenario: Customer checks out shopping cart
    When the customer checks out their shopping cart
    Then the total order amount is provided

  Scenario: Customer order is confirmed and ready for preparation
    Given a customer order
    Then payment is processed
    And the order status is set to "Received..."
    And the order is sent to the kitchen

  Scenario: Customer views their order status
    Given a customer order
    When the customer checks the status of their order
    Then the order status is shown to the customer



