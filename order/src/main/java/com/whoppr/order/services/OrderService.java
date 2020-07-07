package com.whoppr.order.services;

import com.whoppr.common.clients.BillingClient;
import com.whoppr.common.clients.OrderClient;
import com.whoppr.common.exceptions.NoPendingOrders;
import com.whoppr.common.exceptions.NotFoundException;
import com.whoppr.common.model.KanBan;
import com.whoppr.common.model.Order;
import com.whoppr.common.model.OrderStatus;
import com.whoppr.common.model.StateTransition;
import com.whoppr.order.repos.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

@RestController
@Service
public class OrderService implements OrderClient {

  @Autowired
  private BillingClient billingClient;

  @Autowired
  private OrderRepository orderRepository;

  @Override
  @DeleteMapping("/orders")
  public void deleteAllOrders() {
    orderRepository.deleteAll();
  }

  @Override
  @GetMapping("/orders")
  public List<Order> getAllOrders() {
    return orderRepository.findAll();
  }

  @Override
  @PostMapping(value = "/order", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void addOrder(@RequestBody Order order) {
    orderRepository.save(order);
  }

  @Override
  @GetMapping("/order/{id}")
  public Order getOrder(@PathVariable("id") String id) {
    return orderRepository
        .findById(id)
        .orElseThrow(NotFoundException::new);
  }

  @Override
  @PostMapping(value = "/kanban/{process}/pull", produces = "application/json")
  public KanBan pullKanBan(@PathVariable("process") String process) {
    StateTransition transition = StateMachine.getTransition(process, "pull");
    Order order = getNextOrderInState(transition.getCurrent());
    moveToNextState(order.getId(), transition.getNext());
    return KanBan.builder()
        .orderId(order.getId())
        .build();
  }

  @Override
  @PostMapping(value = "/kanban/{process}/put", consumes = "application/json")
  public void putKanBan(@PathVariable("process") String process,
                        @RequestBody KanBan kanBan) {
    StateTransition transition = StateMachine.getTransition(process, "put");
    moveToNextState(kanBan.getOrderId(), transition.getNext());
  }

  @Override
  @GetMapping(value = "/order-status/{id}", produces = "application/json")
  public String getOrderStatus(@PathVariable("id") String id) {
    return orderRepository.findById(id)
        .map(order -> order.getOrderStatus().getDisplayName())
        .orElseThrow(NotFoundException::new);
  }

  private Order getNextOrderInState(OrderStatus orderStatus) {
    return orderRepository.findAll()
        .stream()
        .filter(order -> order.getOrderStatus().equals(orderStatus))
        .min(Comparator.comparing(Order::getOrderReceivedTime))
        .orElseThrow(NoPendingOrders::new);
  }

  private void moveToNextState(String id, OrderStatus orderStatus) {
    Order order = orderRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    order.addOrderEvent(orderStatus);
    orderRepository.save(order);
  }

  @Override
  @PostMapping(value = "/receipt/{orderId}")
  public void scanCustomerReceipt(
      @PathVariable("orderId") String orderId,
      @RequestParam("file") MultipartFile file
  ) throws IOException {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(NotFoundException::new);
    order.setReceiptBase64Image(Base64.getEncoder().encodeToString(file.getBytes()));
    String confirmationId = billingClient.confirmPayment(order.getHoldId(), order.getTotalAmount());
    order.setConfirmationId(confirmationId);
    orderRepository.save(order);
    moveToNextState(orderId, OrderStatus.DELIVERED);

  }

}
