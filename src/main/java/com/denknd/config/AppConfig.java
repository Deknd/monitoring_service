package com.denknd.config;

import com.denknd.audit.api.UserIdentificationService;
import com.denknd.security.service.SecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.FileNotFoundException;
import java.text.ParseException;

/**
 * Конфигурация приложения.
 */
@Configuration
@Log4j2
public class AppConfig implements WebMvcConfigurer {
  @Bean
  public ObjectMapper objectMapper() {
    var objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  @Bean
  public JWEEncrypter jweEncrypter(
          @Value("${jwt.cookie-token-key}") String cookieTokenKey
  ) throws ParseException, KeyLengthException {
    return new DirectEncrypter(OctetSequenceKey.parse(cookieTokenKey));
  }

  @Bean
  public JWEDecrypter jweDecrypter(
          @Value("${jwt.cookie-token-key}") String cookieTokenKey
  ) throws Exception {
    return new DirectDecrypter(OctetSequenceKey.parse(cookieTokenKey));
  }

  @Bean
  public UserIdentificationService userIdentificationService(SecurityService securityService) {
    return () -> securityService.isAuthentication() ? securityService.getUserSecurity().userId() : null;
  }
}