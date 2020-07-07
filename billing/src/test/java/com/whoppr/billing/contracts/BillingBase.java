package com.whoppr.billing.contracts;

import com.whoppr.billing.BillingApplication;
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
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = BillingApplication.class)
@DirtiesContext
@AutoConfigureStubRunner(
    ids = "com.whoppr:menu:+:stubs:8095",
    stubsMode = StubRunnerProperties.StubsMode.LOCAL)
@AutoConfigureMessageVerifier
public abstract class BillingBase {

  @Autowired
  WebApplicationContext webApplicationContext;

  @Autowired
  FilterChainProxy filterChainProxy;


  public String authHeader = IntegrationTestBase.createAuthHeader("joshua", "joshua");

  @BeforeEach
  public void setup() {
    //https://github.com/spring-cloud/spring-cloud-contract/issues/1428
    EncoderConfig encoderConfig = new EncoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false);
    RestAssuredMockMvcConfig restAssuredConf = new RestAssuredMockMvcConfig().encoderConfig(encoderConfig);
    RestAssuredMockMvc.config = restAssuredConf;
    MockMvc mockMvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .addFilter(filterChainProxy, "/*")
        .build();

    RestAssuredMockMvc.mockMvc(mockMvc);

  }

  public String getAuthHeader() {
    return authHeader;
  }
}