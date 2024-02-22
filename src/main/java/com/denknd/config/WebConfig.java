package com.denknd.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Контекст для веб приложения
 */
@Configuration
@Slf4j
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

}