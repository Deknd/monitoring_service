package com.denknd.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Контекст для веб приложения
 */
@Configuration
@Slf4j
@EnableWebMvc
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"com.denknd.in.controllers"})
public class WebConfig implements WebMvcConfigurer {

  /**
   * Добавления статических ресурсов
   *
   * @param registry объект для регистрации статических ресурсов
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
            .addResourceHandler("/swagger/**")
            .addResourceLocations(
                    "classpath:/static/",
                    "classpath:/META-INF/resources/webjars/swagger-ui/5.10.3/"
            );
  }

  /**
   * Добавления контроллера
   *
   * @param registry объект для добавления контроллера
   */
  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/swagger/")
            .setViewName("forward:/swagger/swagger.html");
  }
}