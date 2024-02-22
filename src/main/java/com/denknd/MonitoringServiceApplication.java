package com.denknd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class MonitoringServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(MonitoringServiceApplication.class, args);
  }
}
