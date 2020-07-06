package com.whoppr.monolith;

import com.whoppr.monolith.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({AppConfig.class})
public class MonolithicApplication {

  public static void main(String[] args) {
    SpringApplication.run(MonolithicApplication.class, args);
  }

}
