package com.whoppr.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaServer
@SpringBootApplication
@ComponentScan("com.whoppr.registry")
public class RegistryApplication {

  public static void main(String[] args) {
    SpringApplication.run(RegistryApplication.class, args);
  }

}
