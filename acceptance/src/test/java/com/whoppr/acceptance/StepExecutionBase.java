package com.whoppr.acceptance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.whoppr.common.model.Customer;
import com.whoppr.common.model.MenuItem;
import com.whoppr.common.model.OAuth2Details;
import com.whoppr.testutils.IntegrationTestBase;
import lombok.SneakyThrows;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static com.whoppr.testutils.TestDataBuddy.*;

public class StepExecutionBase {


  private RestTemplate restTemplate;
  private RestTemplate authRestTemplate;

  private String baseUrl = "http://localhost:8090/";
  private String userName = "joshua";
  private String password = "joshua";
  protected Customer testCustomer = buildTestCustomer();
  protected List<MenuItem> menuItems = buildTestMenuItems();
  protected File receipt = null;


  public TestContext testContext() {
    return com.whoppr.acceptance.TestContext.CONTEXT;
  }

  public StepExecutionBase() {
    RestTemplateBuilder authRestTemplateBuilder = new RestTemplateBuilder();
    this.authRestTemplate = authRestTemplateBuilder.basicAuthentication("whoppr", "whoppr-test").build();
    RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
    this.restTemplate = restTemplateBuilder.additionalInterceptors(
        (ClientHttpRequestInterceptor) (request, body, execution) -> {
          if (!request.getHeaders().containsKey("Authorization")) {
            // No need to add headers to feign client requests, they will already contain
            // the auth header from top level request
            request.getHeaders().add("Authorization", "Bearer " + getAccessToken());
          }
          return execution.execute(request, body);
        }
    ).build();
    readProperties();

  }

  <T> T executeGet(String url, Class<T> clazz) throws Exception {
    return restTemplate.getForObject(baseUrl + url, clazz);
  }

  public <T> List<T> executeGetList(String url, ParameterizedTypeReference<List<T>> responseType) {
    return restTemplate.exchange(baseUrl + url, HttpMethod.GET, null, responseType).getBody();
  }

  void executeDelete(String url) throws Exception {
    restTemplate.delete(baseUrl + url);
  }

  <T> void executePost(String url, T object) throws Exception {
    restTemplate.postForLocation(baseUrl + url, object);
  }

  <Req, Resp> Resp executePost(String url, Req object, Class<Resp> clazz) throws Exception {
    return restTemplate.postForObject(baseUrl + url, object, clazz);
  }

  void executeUpload(String url, File file) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    MultiValueMap<String, Object> body
        = new LinkedMultiValueMap<>();
    MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
    ContentDisposition contentDisposition = ContentDisposition
        .builder("form-data")
        .name("file")
        .filename(file.getName())
        .build();
    fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());

    byte[] byteArray = Files.readAllBytes(file.toPath());
    HttpEntity<byte[]> fileEntity = new HttpEntity<>(byteArray, fileMap);
    body.add("file", fileEntity);
    HttpEntity<MultiValueMap<String, Object>> requestEntity
        = new HttpEntity<>(body, headers);
    restTemplate.postForEntity(baseUrl + url, requestEntity, Void.class);
  }

  protected void setupCustomer() throws Exception {
    executeDelete("customers");
    testContext().set("mockCustomer", testCustomer);
    executePost("customer", testCustomer);
  }

  protected void setupMenu() throws Exception {
    executeDelete("menu-items");
    testContext().set("expectedMenuItems", menuItems);
    executePost("menu-item", menuItems.get(0));
    executePost("menu-item", menuItems.get(1));
  }

  protected void setupTestReceipt() throws IOException, URISyntaxException {
    receipt = buildTestReceipt();
  }

  private String getAccessToken() {

    String encodedCredentials = IntegrationTestBase.createAuthHeader("whoppr", "whoppr-secret");

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    headers.add("Authorization", "Basic " + encodedCredentials);
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
    map.add("grant_type", "password");
    map.add("client_id", "whoppr");
    map.add("client_secret", "whoppr-secret");
    map.add("username", "joshua");
    map.add("password", "joshua");

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

    String access_token_url = "http://localhost:8086/auth/realms/whoppr/protocol/openid-connect/token";
    ResponseEntity<OAuth2Details> response = authRestTemplate.postForEntity(access_token_url, request, OAuth2Details.class);

    return response.getBody().getAccess_token();

  }

  @SneakyThrows
  private void readProperties() {
    ClassLoader classLoader = StepExecutionBase.class.getClassLoader();
    File file = new File(classLoader.getResource("cucumber.yml").getFile());
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    CucumberProperties cucumberProperties = mapper.readValue(file, CucumberProperties.class);
    baseUrl = cucumberProperties.url;
    userName = cucumberProperties.user;
    password = cucumberProperties.password;
  }
}
