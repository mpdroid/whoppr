package com.whoppr.acceptance;

import java.util.HashMap;
import java.util.Map;

import static java.lang.ThreadLocal.withInitial;
import static org.assertj.core.api.Assertions.assertThat;

// Based on https://medium.com/@bcarunmail/sharing-state-between-cucumber-step-definitions-using-java-and-spring-972bc31117af
public enum TestContext {

  CONTEXT;

  private static final String PAYLOAD = "PAYLOAD";
  private static final String REQUEST = "REQUEST";
  private static final String RESPONSE = "RESPONSE";
  private final ThreadLocal<Map<String, Object>> testContexts = withInitial(HashMap::new);

  public <T> T get(String name) {
    return (T) testContexts.get()
        .get(name);
  }

  public <T> T set(String name, T object) {
    testContexts.get()
        .put(name, object);
    return object;
  }


  public Object getPayload() {
    return get(PAYLOAD);
  }

  public <T> T getPayload(Class<T> clazz) {
    return clazz.cast(get(PAYLOAD));
  }

  public <T> void setPayload(T object) {
    set(PAYLOAD, object);
  }

  public void reset() {
    testContexts.get()
        .clear();
  }

  public void assertEquals(String expectedKey, String actualKey, String... ignoredFields) {
    Object expected = CONTEXT.get(expectedKey);
    Object actual = CONTEXT.get(expectedKey);
    assertThat(actual).isEqualToIgnoringGivenFields(expected, ignoredFields);
  }

}
