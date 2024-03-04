package com.denknd.time.config;

import com.denknd.time.aspect.MeasureExecutionTimeAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация модуля, для измерении времени работы метода
 */
@Configuration
public class MeasureTimeConfig {

  @Bean
  public MeasureExecutionTimeAspect measureExecutionTimeAspect(){
    return new MeasureExecutionTimeAspect();
  }
}
