package com.whoppr.menu.contracts;

import com.whoppr.common.model.MenuItem;
import com.whoppr.menu.MenuApplication;
import com.whoppr.menu.repos.MenuItemRepository;
import com.whoppr.testutils.IntegrationTestBase;
import com.whoppr.testutils.TestDataBuddy;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = MenuApplication.class)
@DirtiesContext
@AutoConfigureMessageVerifier
public abstract class MenuBase {


  @Autowired
  WebApplicationContext webApplicationContext;

  @Autowired
  RestTemplate restTemplate;

  @Autowired
  RemoteTokenServices remoteTokenServices;

  @Autowired
  private MenuItemRepository menuItemRepository;

  public String authHeader = "Basic mock-client-creds";

  @BeforeEach
  public void setup() {

    MockMvc mockMvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .apply(springSecurity())
        .build();

    RestAssuredMockMvc.mockMvc(mockMvc);

    IntegrationTestBase.mockAuthentication(remoteTokenServices);
    menuItemRepository.deleteAll();
    List<MenuItem> menuItems = TestDataBuddy.buildTestMenuItems();
    menuItemRepository.saveAll(menuItems);
  }

  public String getAuthHeader() {
    return authHeader;
  }
}
