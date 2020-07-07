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
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = MenuApplication.class)
@DirtiesContext
@AutoConfigureMessageVerifier
public abstract class MenuBase {

  @Autowired
  WebApplicationContext webApplicationContext;

  @Autowired
  FilterChainProxy filterChainProxy;

  @Autowired
  private MenuItemRepository menuItemRepository;

  public String authHeader = IntegrationTestBase.createAuthHeader("joshua", "joshua");

  @BeforeEach
  public void setup() {

    MockMvc mockMvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .addFilter(filterChainProxy, "/*")
        .build();

    RestAssuredMockMvc.mockMvc(mockMvc);

    menuItemRepository.deleteAll();
    List<MenuItem> menuItems = TestDataBuddy.buildTestMenuItems();
    menuItemRepository.saveAll(menuItems);
  }

  public String getAuthHeader() {
    return authHeader;
  }
}
