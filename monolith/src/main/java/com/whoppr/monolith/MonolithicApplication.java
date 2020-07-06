package com.whoppr.monolith;

import com.whoppr.common.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@EnableZuulProxy
@SpringBootApplication
@Import({AppConfig.class})
@ComponentScan("com.whoppr") // for spring to load configurations from common library classes
public class MonolithicApplication {

  public static void main(String[] args) {
    SpringApplication.run(MonolithicApplication.class, args);
  }

}
