package com.whoppr.monolith.acceptance;

import com.whoppr.monolith.TestUtils;
import com.whoppr.monolith.model.Customer;
import com.whoppr.monolith.model.MenuItem;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;

import static com.whoppr.monolith.TestUtils.buildTestReceipt;

public class StepExecutionBase {

  // TODO find a way to inject at run time
  private static String BASE_URL = "http://localhost:8080/";

  private RestTemplate restTemplate;

  protected Customer testCustomer = TestUtils.buildTestCustomer();
  protected List<MenuItem> menuItems = TestUtils.buildTestMenuItems();
  protected File receipt = null;


  public TestContext testContext() {
    return TestContext.CONTEXT;
  }

  public StepExecutionBase() {
    RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
    this.restTemplate = restTemplateBuilder.basicAuthentication("joshua", "joshua").build();
  }

  <T> T executeGet(String url, Class<T> clazz) throws Exception {
    return restTemplate.getForObject(BASE_URL + url, clazz);
  }

  public <T> List<T> executeGetList(String url, ParameterizedTypeReference<List<T>> responseType) {
    return restTemplate.exchange(BASE_URL + url, HttpMethod.GET, null, responseType).getBody();
  }

  void executeDelete(String url) throws Exception {
    restTemplate.delete(BASE_URL + url);
  }

  <T> void executePost(String url, T object) throws Exception {
    restTemplate.postForLocation(BASE_URL + url, object);
  }

  <Req, Resp> Resp executePost(String url, Req object, Class<Resp> clazz) throws Exception {
    return restTemplate.postForObject(BASE_URL + url, object, clazz);
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
    restTemplate.postForEntity(BASE_URL + url, requestEntity, Void.class);
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
}
