package com.whoppr.common.clients;

import com.whoppr.common.model.Order;
import com.whoppr.common.model.ShoppingCart;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("billing")
public interface BillingClient {
  @PostMapping(value = "/compute-total",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Order computeOrderTotal(@RequestBody ShoppingCart cart);

  @PostMapping(value = "/payment-hold/{customerToken}/{amount}", produces = "application/json")
  String processPaymentHold(@PathVariable("customerToken") String customerToken,
                            @PathVariable("amount") Double amount);

  @PostMapping(value = "/payment-confirm/{holdId}/{amount}", produces = "application/json")
  String confirmPayment(@PathVariable("holdId") String holdId, @PathVariable("amount") Double totalAmount);
}
