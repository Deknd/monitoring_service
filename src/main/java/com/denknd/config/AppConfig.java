package com.denknd.config;

import com.denknd.util.DbConfig;
import com.denknd.util.JwtConfig;
import com.denknd.util.LiquibaseConfig;
import com.denknd.util.YamlParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.FileNotFoundException;
import java.text.ParseException;

/**
 * Конфигурация приложения.
 */
@Configuration
@Slf4j
@ComponentScan(
        basePackages = { "com.denknd.in.filters", "com.denknd.mappers", "com.denknd.out.audit", "com.denknd.repository",
                "com.denknd.security", "com.denknd.services", "com.denknd.util"}
)
@EnableAspectJAutoProxy
public class AppConfig implements WebMvcConfigurer {
  /**
   * Конфигурация для jwt токенов
   *
   * @param yamlParser парсер для yml файла
   * @return конфигурация для jwt токена
   * @throws FileNotFoundException ошибка чтения файла
   */
  @Bean
  public JwtConfig jwtConfig(YamlParser yamlParser) throws FileNotFoundException {
    return yamlParser.jwtConfig();
  }

  /**
   * Конфигурация для базы данных.
   *
   * @param yamlParser парсер для yml файла.
   * @return конфигурация для базы данных.
   * @throws FileNotFoundException ошибка чтения файла
   */
  @Bean
  public DbConfig dbConfig(YamlParser yamlParser) throws FileNotFoundException {
    return yamlParser.dbConfig();
  }

  /**
   * Конфигурация для ликвибаз.
   *
   * @param yamlParser парсер для yml файла.
   * @return Конфигурация для ликвибаз.
   * @throws FileNotFoundException ошибка чтения файла
   */
  @Bean
  public LiquibaseConfig liquibaseConfig(YamlParser yamlParser) throws FileNotFoundException {
    return yamlParser.liquibaseConfig();
  }

  /**
   * Маппер из json в объект и обратно.
   *
   * @return маппер из json в объект и обратно.
   */
  @Bean
  public ObjectMapper objectMapper() {
    var objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  /**
   * Кодировщик токенов.
   *
   * @param jwtConfig Конфигурация для jwt токенов.
   * @return Кодировщик токенов.
   * @throws ParseException        ошибка парсинга секретного ключа.
   * @throws KeyLengthException    ошибка связаная с длиной ключа.
   * @throws FileNotFoundException ошибка чтения файла.
   */
  @Bean
  public JWEEncrypter jweEncrypter(JwtConfig jwtConfig
  ) throws ParseException, KeyLengthException{
    return new DirectEncrypter(OctetSequenceKey.parse(jwtConfig.secretKey()));
  }

  /**
   * Декодировщик токенов.
   *
   * @param jwtConfig Конфигурация для jwt токенов.
   * @return Декодировщик токенов.
   * @throws Exception ошибка при декодировании токена.
   */
  @Bean
  public JWEDecrypter jweDecrypter(
          JwtConfig jwtConfig
  ) throws Exception {
    return new DirectDecrypter(OctetSequenceKey.parse(jwtConfig.secretKey()));
  }
}
