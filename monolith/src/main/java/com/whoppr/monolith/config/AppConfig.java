package com.whoppr.monolith.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

  @Primary
  @Bean
  RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
