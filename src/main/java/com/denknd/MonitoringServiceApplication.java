package com.denknd;

import com.denknd.time.api.EnableMeasureTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableMeasureTime
public class MonitoringServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(MonitoringServiceApplication.class, args);
  }
}
