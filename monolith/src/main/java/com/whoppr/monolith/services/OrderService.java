package com.whoppr.monolith.services;

import com.whoppr.monolith.exceptions.NoPendingOrders;
import com.whoppr.monolith.exceptions.NotFoundException;
import com.whoppr.monolith.model.KanBan;
import com.whoppr.monolith.model.Order;
import com.whoppr.monolith.model.OrderStatus;
import com.whoppr.monolith.model.StateTransition;
import com.whoppr.monolith.repos.OrderRepository;
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
public class OrderService {

  @Autowired
  private BillingService billingService;

  @Autowired
  private OrderRepository orderRepository;

  @DeleteMapping("/orders")
  public void deleteAllOrders() {
    orderRepository.deleteAll();
  }

  @GetMapping("/orders")
  public List<Order> getAllOrders() {
    return orderRepository.findAll();
  }

  @PostMapping(value = "/order", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void addOrder(@RequestBody Order order) {
    orderRepository.save(order);
  }

  @GetMapping("/order/{id}")
  public Order getOrder(@PathVariable("id") String id) {
    return orderRepository
        .findById(id)
        .orElseThrow(NotFoundException::new);
  }

  @PostMapping(value = "/kanban/{process}/pull", produces = "application/json")
  public KanBan pullKanBan(@PathVariable("process") String process) {
    StateTransition transition = StateMachine.getTransition(process, "pull");
    Order order = getNextOrderInState(transition.getCurrent());
    moveToNextState(order.getId(), transition.getNext());
    return KanBan.builder()
        .orderId(order.getId())
        .build();
  }

  @PostMapping(value = "/kanban/{process}/put", consumes = "application/json")
  public void putKanBan(@PathVariable("process") String process,
                        @RequestBody KanBan kanBan) {
    StateTransition transition = StateMachine.getTransition(process, "put");
    moveToNextState(kanBan.getOrderId(), transition.getNext());
  }

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

  @PostMapping(value = "/receipt/{orderId}")
  public void scanCustomerReceipt(
      @PathVariable("orderId") String orderId,
      @RequestParam("file") MultipartFile file
  ) throws IOException {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(NotFoundException::new);
    order.setReceiptBase64Image(Base64.getEncoder().encodeToString(file.getBytes()));
    String confirmationId = billingService.confirmPayment(order.getHoldId(), order.getTotalAmount());
    order.setConfirmationId(confirmationId);
    orderRepository.save(order);
    moveToNextState(orderId, OrderStatus.DELIVERED);

  }

}
