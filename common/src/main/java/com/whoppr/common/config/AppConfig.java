package com.whoppr.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

  @Primary
  @Bean
  RestTemplate RestTemplate() {
    return new RestTemplate();
  }
}
