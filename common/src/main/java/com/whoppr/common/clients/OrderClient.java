package com.whoppr.common.clients;

import com.whoppr.common.model.KanBan;
import com.whoppr.common.model.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@FeignClient("order")
public interface OrderClient {
  @DeleteMapping("/orders")
  void deleteAllOrders();

  @GetMapping("/orders")
  List<Order> getAllOrders();

  @PostMapping(value = "/order", consumes = MediaType.APPLICATION_JSON_VALUE)
  void addOrder(@RequestBody Order order);

  @GetMapping("/order/{id}")
  Order getOrder(@PathVariable("id") String id);

  @PostMapping(value = "/kanban/{process}/pull", produces = "application/json")
  KanBan pullKanBan(@PathVariable("process") String process);

  @PostMapping(value = "/kanban/{process}/put", consumes = "application/json")
  void putKanBan(@PathVariable("process") String process,
                 @RequestBody KanBan kanBan);

  @GetMapping(value = "/order-status/{id}", produces = "application/json")
  String getOrderStatus(@PathVariable("id") String id);

  @PostMapping(value = "/receipt/{orderId}")
  void scanCustomerReceipt(
      @PathVariable("orderId") String orderId,
      @RequestParam("file") MultipartFile file
  ) throws IOException;
}
