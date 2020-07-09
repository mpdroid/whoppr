package com.whoppr.order.contracts;

import com.whoppr.common.model.Order;
import com.whoppr.common.model.OrderEvent;
import com.whoppr.common.model.OrderStatus;
import com.whoppr.order.OrderApplication;
import com.whoppr.order.repos.OrderRepository;
import com.whoppr.testutils.IntegrationTestBase;
import io.restassured.config.EncoderConfig;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.config.RestAssuredMockMvcConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static com.whoppr.testutils.TestDataBuddy.buildTestCustomer;
import static com.whoppr.testutils.TestDataBuddy.buildTestOrder;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = OrderApplication.class)
@DirtiesContext
@AutoConfigureStubRunner(
    ids = {"com.whoppr:menu:+:stubs:8095", "com.whoppr:billing:+:stubs:8094"},
    stubsMode = StubRunnerProperties.StubsMode.LOCAL)
@AutoConfigureMessageVerifier
public abstract class OrderGetBase {

  @Autowired
  WebApplicationContext webApplicationContext;

  @Autowired
  RemoteTokenServices remoteTokenServices;

  @Autowired
  OrderRepository orderRepository;

  @BeforeEach
  public void setup() {

    createTestOrder();

    //https://github.com/spring-cloud/spring-cloud-contract/issues/1428
    EncoderConfig encoderConfig = new EncoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false);
    RestAssuredMockMvcConfig restAssuredConf = new RestAssuredMockMvcConfig().encoderConfig(encoderConfig);
    RestAssuredMockMvc.config = restAssuredConf;
    MockMvc mockMvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .apply(springSecurity())
        .build();

    RestAssuredMockMvc.mockMvc(mockMvc);
    IntegrationTestBase.mockAuthentication(remoteTokenServices);


  }

  private void createTestOrder() {
    Order order = buildTestOrder(buildTestCustomer());
    order.getOrderEvents().add(OrderEvent.builder()
        .status(OrderStatus.RECEIVED)
        .eventTime(LocalDateTime.now()).build());
    order.setHoldId("a-hold-id");
    orderRepository.deleteAll();
    orderRepository.save(order);
  }

}
