package com.whoppr.order.services;

import com.whoppr.common.clients.BillingClient;
import com.whoppr.common.model.*;
import com.whoppr.order.repos.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static com.whoppr.testutils.TestDataBuddy.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class OrderServiceTest {
  @InjectMocks
  private OrderService orderService;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  BillingClient billingClient;

  Customer testCustomer = buildTestCustomer();
  List<MenuItem> menuItems = buildTestMenuItems();
  List<OrderItem> orderItems = buildTestOrderItems(menuItems);
  Order testOrder = buildTestOrder(testCustomer);
  
  @BeforeEach
  void setUp() {
    initMocks(this);
    testOrder.setHoldId("mock-hold-id");
    when(orderRepository.findById(testOrder.getId()))
        .thenReturn(Optional.of(testOrder));
  }

  @Test
  void deleteAllOrders() {
    orderService.deleteAllOrders();
    verify(orderRepository, times(1)).deleteAll();
  }

  @Test
  void getAllOrders() {
    orderService.getAllOrders();
    verify(orderRepository, times(1)).findAll();
  }

  @Test
  void getOrder() {
    orderService.getOrder(testOrder.getId());
    verify(orderRepository, times(1))
        .findById(testOrder.getId());
  }


  @Test
  void addOrder() {
    orderService.addOrder(testOrder);
    verify(orderRepository, times(1))
        .save(testOrder);
  }

  @Test
  void pullKanBan() {
    ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);
    Order kitchenOrder = whenKitchenOrderInQueue();
    KanBan kanBan = orderService.pullKanBan("kitchen");
    assertEquals(kitchenOrder.getId(), kanBan.getOrderId());
    verify(orderRepository).save(orderArgumentCaptor.capture());
    assertEquals(OrderStatus.PREPARING, orderArgumentCaptor.getValue().getOrderStatus());
  }

  private Order whenKitchenOrderInQueue() {
    Order kitchenOrder = buildTestOrder(testCustomer);
    kitchenOrder.addOrderEvent(OrderStatus.RECEIVED);
    when(orderRepository.findAll())
        .thenReturn(Arrays.asList(kitchenOrder));
    return kitchenOrder;
  }

  @Test
  void putKanBan() {
    ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);
    Order kitchenOrder = givenKitchenOrderInQueue();
    KanBan kanban = andKitchenOrderPulledFromQueue();
    whenKitchenOrderIsCompleted(kanban);
    InOrder orderVerifier = Mockito.inOrder(orderRepository);
    orderVerifier.verify(orderRepository).save(orderArgumentCaptor.capture());
    orderVerifier.verify(orderRepository).save(orderArgumentCaptor.capture());

    assertEquals(OrderStatus.READY, orderArgumentCaptor
        .getValue()
        .getOrderStatus());
  }

  private Order givenKitchenOrderInQueue() {
    return whenKitchenOrderInQueue();
  }

  private KanBan andKitchenOrderPulledFromQueue() {
    return orderService.pullKanBan("kitchen");
  }


  private void whenKitchenOrderIsCompleted(KanBan kanban) {
    orderService.putKanBan("kitchen", kanban);
  }


  @Test
  void getOrderStatus() {
    Order kitchenOrder = buildTestOrder(testCustomer);
    kitchenOrder.addOrderEvent(OrderStatus.RECEIVED);
    when(orderRepository.findById(kitchenOrder.getId()))
        .thenReturn(Optional.of(kitchenOrder));
    String orderStatus = orderService.getOrderStatus(kitchenOrder.getId());
    assertEquals("Received...", orderStatus);
  }


  @Test
  void scanCustomerReceipt() throws IOException, URISyntaxException {

    ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);
    MockMultipartFile multiPart = buildMockMultipartFile();

    when(billingClient.confirmPayment(testOrder.getHoldId(), testOrder.getTotalAmount()))
        .thenReturn("mock-confirmation-id");

    orderService.scanCustomerReceipt(testOrder.getId(), multiPart);
    InOrder orderVerifier = Mockito.inOrder(orderRepository);
    orderVerifier.verify(orderRepository).save(orderArgumentCaptor.capture());
    orderVerifier.verify(orderRepository).save(orderArgumentCaptor.capture());

    assertReceiptIsSaved(orderArgumentCaptor, multiPart);
    assertOrderStatusIsUpdated(orderArgumentCaptor);
  }

  private MockMultipartFile buildMockMultipartFile() throws IOException, URISyntaxException {
    File file = buildTestReceipt();
    return new MockMultipartFile("file", file.getName(),
        MediaType.MULTIPART_FORM_DATA_VALUE,
        Files.readAllBytes(file.toPath())
    );
  }

  private void assertOrderStatusIsUpdated(ArgumentCaptor<Order> orderArgumentCaptor) {
    Order actualOrderAfterStateChange = orderArgumentCaptor.getAllValues().get(1);
    assertEquals(OrderStatus.DELIVERED,
        actualOrderAfterStateChange.getOrderStatus());
  }

  private void assertReceiptIsSaved(ArgumentCaptor<Order> orderArgumentCaptor, MockMultipartFile file) throws IOException {
    Order actualOrder = orderArgumentCaptor.getAllValues().get(0);
    assertEquals(Base64.getEncoder().encodeToString(file.getBytes()),
        actualOrder.getReceiptBase64Image());
    assertEquals("mock-confirmation-id", actualOrder.getConfirmationId());
  }
}
