package com.whoppr.customer.services;

import com.whoppr.common.clients.BillingClient;
import com.whoppr.common.clients.OrderClient;
import com.whoppr.common.exceptions.NotFoundException;
import com.whoppr.common.model.Customer;
import com.whoppr.common.model.Order;
import com.whoppr.common.model.OrderStatus;
import com.whoppr.common.model.ShoppingCart;
import com.whoppr.customer.repos.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
@Service
public class CustomerService {

  @Autowired
  public CustomerRepository customerRepository;

  @Autowired
  public BillingClient billingClient;

  @Autowired
  public OrderClient orderClient;

  @DeleteMapping("/customers")
  public void deleteAllCustomers() {
    customerRepository.deleteAll();
  }

  @PostMapping(value = "/customer", consumes = "application/json")
  public void addCustomer(@RequestBody Customer customer) {
    customerRepository.save(customer);
  }

  @GetMapping(value = "/customer/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Customer getCustomer(@PathVariable("id") String id) {
    return customerRepository.findById(id).orElseThrow(NotFoundException::new);
  }

  @PostMapping(value = "/checkout", consumes = "application/json")
  public Order checkoutShoppingCart(@RequestBody ShoppingCart cart) {
    Customer customer = getCustomer(cart.getCustomerId());
    cart.setEarnedRewards(customer.getEarnedRewardAmount());
    return billingClient.computeOrderTotal(cart);
  }

  @PostMapping(value = "/confirm-order", consumes = "application/json")
  public void confirmOrder(@RequestBody Order order) {
    Customer customer = getCustomer(order.getCustomerId());
    String holdId = billingClient.processPaymentHold(
        customer.getCustomerToken(),
        order.getTotalAmount()
    );
    order.setHoldId(holdId);
    order.addOrderEvent(OrderStatus.RECEIVED);
    orderClient.addOrder(order);
  }


}
