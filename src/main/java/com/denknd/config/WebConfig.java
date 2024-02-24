package com.denknd.config;

import com.denknd.in.filters.AuthenticationFilter;
import com.denknd.security.service.SecurityService;
import com.denknd.security.utils.authenticator.UserAuthenticator;
import com.denknd.security.utils.converter.AuthenticationConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterRegistration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Контекст для веб приложения
 */
@Configuration
@Slf4j
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

  public FilterRegistrationBean<AuthenticationFilter> authenticationFilterRegistration(
          SecurityService securityService,
          List<AuthenticationConverter> authenticationConverterList,
          List<UserAuthenticator> userAuthenticatorList,
          ObjectMapper objectMapper
  ){
    var registrationBean = new FilterRegistrationBean<AuthenticationFilter>();
    var authenticationFilter = new AuthenticationFilter(securityService, authenticationConverterList, userAuthenticatorList, objectMapper);
    registrationBean.setFilter(authenticationFilter);
    registrationBean.addUrlPatterns("/*");
    registrationBean.setMatchAfter(false);
    return registrationBean;
  }
}