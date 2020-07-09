package com.whoppr.testutils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.whoppr.common.model.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

import static com.whoppr.testutils.TestDataBuddy.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IntegrationTestBase {
  protected WebApplicationContext webApplicationContext;
  protected MockMvc mockMvc;

  protected RestTemplate oauthRestTemplate;
  protected HttpHeaders headers;
  private ObjectMapper mapper;

  public IntegrationTestBase(
      WebApplicationContext webApplicationContext,
      RemoteTokenServices remoteTokenServices
  ) {
    this.webApplicationContext = webApplicationContext;

    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(this.webApplicationContext)
        .apply(springSecurity())
        .build();
    this.headers = new HttpHeaders() {{
      set("Authorization", "Bearer mock-access-token");
    }};
    this.mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mockAuthentication(remoteTokenServices);
  }

  protected List<MenuItem> createTestMenuItems() throws Exception {
    executeDelete("/menu-items");
    List<MenuItem> menuItems = buildTestMenuItems();
    executePost("/menu-item", menuItems.get(0));
    executePost("/menu-item", menuItems.get(1));
    return menuItems;
  }

  protected Customer createTestCustomer() throws Exception {
    executeDelete("/customers");
    Customer customer = buildTestCustomer();
    executePost("/customer", customer);
    return customer;
  }

  protected Order createConfirmedTestOrder(Customer customer) throws Exception {
    executeDelete("/orders");
    Order order = buildTestOrder(customer);
    order.getOrderEvents().add(OrderEvent.builder()
        .status(OrderStatus.RECEIVED)
        .eventTime(LocalDateTime.now()).build());
    order.setHoldId("a-hold-id");
    order.setHoldId("a-hold-id");
    executePost("/order", order);
    return order;
  }

  protected Order createTestOrder(Customer customer) throws Exception {
    executeDelete("/orders");
    Order order = buildTestOrder(customer);
    executePost("/order", order);
    return order;
  }


  protected <T> T executeGet(String url, Class<T> clazz) throws Exception {
    String result = this.mockMvc.perform(MockMvcRequestBuilders.get(url)
        .headers(headers)
        .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return mapper.readValue(result, clazz);
  }

  protected <T> List<T> executeGetList(String url, TypeReference<List<T>> responseType) throws Exception {
    String result = this.mockMvc.perform(MockMvcRequestBuilders.get(url)
        .headers(headers)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return mapper.readValue(result, responseType);
  }

  protected void executeDelete(String url) throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders.delete(url)
        .headers(headers)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());
  }

  protected <T> void executePost(String url, T object) throws Exception {
    String requestString = mapper.writeValueAsString(object);
    this.mockMvc.perform(MockMvcRequestBuilders.post(url)
        .headers(headers)
        .content(requestString)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());

  }

  protected <Req, Resp> Resp executePost(String url, Req object, Class<Resp> clazz) throws Exception {
    String requestString = mapper.writeValueAsString(object);
    String result = this.mockMvc.perform(MockMvcRequestBuilders.post(url)
        .headers(headers)
        .content(requestString)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful())
        .andReturn().getResponse().getContentAsString();
    return mapper.readValue(result, clazz);
  }

  protected void executeUpload(String url, File file) throws Exception {
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    MockMultipartFile multiPart = new MockMultipartFile("file", file.getName(),
        MediaType.MULTIPART_FORM_DATA_VALUE,
        Files.readAllBytes(file.toPath())
    );
    this.mockMvc.perform(MockMvcRequestBuilders.multipart(url)
        .file(multiPart)
        .headers(headers))
        .andExpect(status().is2xxSuccessful());
  }

  public static String createAuthHeader(String username, String password) {
    String auth = username + ":" + password;
    byte[] encodedAuth = Base64.encodeBase64(
        auth.getBytes(Charset.forName("US-ASCII")));
    return "Basic " + new String(encodedAuth);
  }

  private static HttpHeaders createHeaders(String username, String password) {
    return new HttpHeaders() {{
      set("Authorization", createAuthHeader(username, password));
    }};
  }


  public static void mockAuthentication(RemoteTokenServices remoteTokenServices) {
    RestTemplateBuilder authRestTemplateBuilder = new RestTemplateBuilder();
    RestTemplate oauthRestTemplate = authRestTemplateBuilder.basicAuthentication("whoppr", "whoppr-test").build();
    remoteTokenServices.setRestTemplate(oauthRestTemplate);

    MockRestServiceServer mockRestServiceServer = MockRestServiceServer.bindTo(oauthRestTemplate).build();

    mockRestServiceServer.expect(manyTimes(),
        requestTo("http://localhost:8096/auth/realms/whoppr/protocol/openid-connect/token/introspect"))
        .andExpect(method(HttpMethod.POST)
        )
        .andRespond(withSuccess("{\n" +
            "    \"exp\": 1594279108,\n" +
            "    \"iat\": 1594278808,\n" +
            "    \"jti\": \"6f629bc7-2594-44ff-9548-4b0ba88e84de\",\n" +
            "    \"iss\": \"http://localhost:8096/auth/realms/whoppr\",\n" +
            "    \"sub\": \"22a4d9fe-194c-4c6e-841a-8a55b402459f\",\n" +
            "    \"typ\": \"Bearer\",\n" +
            "    \"azp\": \"whoppr\",\n" +
            "    \"session_state\": \"69cb6e5b-e04b-4332-b1a4-b710db26fd55\",\n" +
            "    \"preferred_username\": \"joshua\",\n" +
            "    \"acr\": \"1\",\n" +
            "    \"scope\": \"profile\",\n" +
            "    \"client_id\": \"whoppr\",\n" +
            "    \"username\": \"joshua\",\n" +
            "    \"active\": true\n" +
            "}", MediaType.APPLICATION_JSON));

  }

}
