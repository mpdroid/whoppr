package com.whoppr.menu;

import com.whoppr.common.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({AppConfig.class})
@ComponentScan("com.whoppr")
public class MenuApplication {

  public static void main(String[] args) {
    SpringApplication.run(MenuApplication.class, args);
  }

}
