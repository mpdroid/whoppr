package com.whoppr.customer.services;

import com.whoppr.common.clients.BillingClient;
import com.whoppr.common.clients.OrderClient;
import com.whoppr.common.model.*;
import com.whoppr.customer.repos.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static com.whoppr.testutils.TestDataBuddy.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class CustomerServiceTest {

  @InjectMocks
  private CustomerService customerService;

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private BillingClient billingClient;

  @Mock
  private OrderClient orderClient;


  private Customer testCustomer = buildTestCustomer();

  @BeforeEach
  void setUp() {
    initMocks(this);
    when(customerRepository.findById("customer-1"))
        .thenReturn(Optional.of(testCustomer));
  }

  @Test
  void deleteAllCustomers() {
    customerService.deleteAllCustomers();
    verify(customerRepository, times(1)).deleteAll();
  }

  @Test
  void addCustomer() {
    customerService.addCustomer(testCustomer);
    verify(customerRepository, times(1))
        .save(testCustomer);
  }

  @Test
  void getCustomer() {
    customerService.getCustomer("customer-1");
    verify(customerRepository, times(1)).findById("customer-1");
  }


  @Test
  void confirmOrder() {
    when(billingClient.processPaymentHold(
        eq(testCustomer.getCustomerToken()),
        any()
    )).thenReturn("mock-hold-id");
    Order order = buildTestOrder(testCustomer);

    ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);
    customerService.confirmOrder(order);
    verify(orderClient).addOrder(orderArgumentCaptor.capture());
    assertEquals("mock-hold-id", orderArgumentCaptor.getValue().getHoldId());
  }

  @Test
  void checkoutShoppingCart() {
    List<MenuItem> menuItems = buildTestMenuItems();
    List<OrderItem> orderItems = buildTestOrderItems(menuItems);
    ShoppingCart shoppingCart = buildTestShoppingCart(orderItems);
    ArgumentCaptor<ShoppingCart> cartArgumentCaptor =
        ArgumentCaptor.forClass(ShoppingCart.class);

    customerService.checkoutShoppingCart(shoppingCart);
    verify(billingClient).computeOrderTotal(cartArgumentCaptor.capture());
    assertEquals(testCustomer.getEarnedRewardAmount(), cartArgumentCaptor.getValue().getEarnedRewards());
  }


}
