package com.denknd.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация для аспектов
 */
@Configuration
@ComponentScan(basePackages = {"com.denknd.aspectj"})
public class AspectConfig {
}
