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
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;


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
  RemoteTokenServices remoteTokenServices;

  @BeforeEach
  public void setup() {
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

}
