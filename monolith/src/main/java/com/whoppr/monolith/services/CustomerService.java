package com.whoppr.monolith.services;

import com.whoppr.monolith.exceptions.NotFoundException;
import com.whoppr.monolith.model.Customer;
import com.whoppr.monolith.model.Order;
import com.whoppr.monolith.model.OrderStatus;
import com.whoppr.monolith.model.ShoppingCart;
import com.whoppr.monolith.repos.CustomerRepository;
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
  public BillingService billingService;

  @Autowired
  public OrderService orderService;

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
    return billingService.computeOrderTotal(cart);
  }

  @PostMapping(value = "/order/confirm", consumes = "application/json")
  public void confirmOrder(@RequestBody Order order) {
    Customer customer = getCustomer(order.getCustomerId());
    String holdId = billingService.processPaymentHold(
        customer.getCustomerToken(),
        order.getTotalAmount()
    );
    order.setHoldId(holdId);
    order.addOrderEvent(OrderStatus.RECEIVED);
    orderService.addOrder(order);
  }


}
