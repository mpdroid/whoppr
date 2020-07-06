package com.whoppr.monolith.services;

import com.whoppr.monolith.model.*;
import com.whoppr.monolith.repos.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

import static com.whoppr.monolith.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class OrderServiceTest {
  @InjectMocks
  private OrderService orderService;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  BillingService billingService;

  Customer testCustomer = buildTestCustomer();
  List<MenuItem> menuItems = buildTestMenuItems();
  List<OrderItem> orderItems = buildTestOrderItems(menuItems);
  Order testOrder = buildTestOrder(testCustomer);

  ThreadLocal<KanBan> kanBanThreadLocal = new ThreadLocal<KanBan>();
  ThreadLocal<Order> orderThreadLocal = new ThreadLocal<Order>();


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
    whenKitchenOrderInQueue();
    KanBan kanBan = orderService.pullKanBan("kitchen");
    assertEquals(orderThreadLocal.get().getId(), kanBan.getOrderId());
    verify(orderRepository).save(orderArgumentCaptor.capture());
    assertEquals(OrderStatus.PREPARING, orderArgumentCaptor.getValue().getOrderStatus());
  }

  private void whenKitchenOrderInQueue() {
    Order kitchenOrder = buildTestOrder(testCustomer);
    kitchenOrder.addOrderEvent(OrderStatus.RECEIVED);
    when(orderRepository.findAll())
        .thenReturn(Arrays.asList(kitchenOrder));
    orderThreadLocal.set(kitchenOrder);
  }

  @Test
  void putKanBan() {
    ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);
    givenKitchenOrderInQueue();
    andKitchenOrderPulledFromQueue();

    whenKitchenOrderIsCompleted();
    verify(orderRepository, times(2)).save(orderArgumentCaptor.capture());
    assertEquals(OrderStatus.READY, orderArgumentCaptor
        .getAllValues()
        .get(1)
        .getOrderStatus());
  }

  private void givenKitchenOrderInQueue() {
    whenKitchenOrderInQueue();
  }

  private void andKitchenOrderPulledFromQueue() {
    KanBan kanBan = orderService.pullKanBan("kitchen");
    kanBanThreadLocal.set(kanBan);
  }


  private void whenKitchenOrderIsCompleted() {
    orderService.putKanBan("kitchen", kanBanThreadLocal.get());
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

    when(billingService.confirmPayment(testOrder.getHoldId(), testOrder.getTotalAmount()))
        .thenReturn("mock-confirmation-id");

    orderService.scanCustomerReceipt(testOrder.getId(), multiPart);
    verify(orderRepository, times(2)).save(orderArgumentCaptor.capture());
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
