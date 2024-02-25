package com.denknd.config;

import com.denknd.in.filters.AuthenticationFilter;
import com.denknd.in.filters.BasicAuthenticationFilter;
import com.denknd.security.service.SecurityService;
import com.denknd.security.utils.authenticator.UserAuthenticator;
import com.denknd.security.utils.converter.AuthenticationConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Контекст для веб приложения
 */
@Configuration
@Slf4j
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
  @Bean
  public FilterRegistrationBean<AuthenticationFilter> authenticationFilterRegistration(
          SecurityService securityService,
          List<AuthenticationConverter> authenticationConverterList,
          List<UserAuthenticator> userAuthenticatorList,
          ObjectMapper objectMapper,
          BasicAuthenticationFilter basicAuthenticationFilter
  ) {
    var registrationBean = new FilterRegistrationBean<AuthenticationFilter>();
    var authenticationFilter = new AuthenticationFilter(securityService, authenticationConverterList, objectMapper, userAuthenticatorList);
    authenticationFilter.addIgnoredRequest("/v3/api-docs", "GET");
    authenticationFilter.addIgnoredRequest("/swagger-ui/.*", "GET");
    authenticationFilter.addIgnoredRequest("/swagger/.*", "GET");
    authenticationFilter.addIgnoredRequest(basicAuthenticationFilter.getURL_PATTERNS(), "POST");
    registrationBean.setFilter(authenticationFilter);
    registrationBean.addUrlPatterns("/*");
    registrationBean.setMatchAfter(false);
    return registrationBean;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
            .addResourceHandler("/swagger/**")
            .addResourceLocations(
                    "classpath:/static/",
                    "classpath:/META-INF/resources/webjars/swagger-ui/5.10.3/"
            );
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/swagger/")
            .setViewName("forward:/swagger/swagger.html");
  }
}