package com.denknd.security.utils.converter.impl;

import com.denknd.exception.BadCredentialsException;
import com.denknd.security.entity.PreAuthenticatedAuthenticationToken;
import com.denknd.security.entity.UserCredentials;
import com.denknd.security.utils.converter.AuthenticationConverter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Реализация интерфейса для преобразования аутентификационных данных из HTTP запроса с использованием базовой аутентификации.
 */
@Component
@Slf4j
public class BasicAuthenticationConverter implements AuthenticationConverter {
  /**
   * Заголовок для AUTHORIZATION
   */
  private static final String AUTHORIZATION = "Authorization";
  /**
   * Название аутентификации
   */
  private static final String AUTHENTICATION_SCHEME_BASIC = "Basic";
  /**
   * Кодировка
   */
  private static final Charset CREDENTIALS_CHARSET = StandardCharsets.UTF_8;

  /**
   * Метод для преобразования аутентификационных данных из HTTP запроса с использованием базовой аутентификации.
   *
   * @param httpRequest HTTP запрос.
   * @return токен аутентификации пользователя.
   * @throws BadCredentialsException если происходит ошибка во время обработки аутентификационных данных.
   */
  @Override
  public PreAuthenticatedAuthenticationToken convert(HttpServletRequest httpRequest) throws BadCredentialsException {
    var header = httpRequest.getHeader(AUTHORIZATION);
    if (header == null) {
      return null;
    }
    header = header.trim();
    if (!StringUtils.startsWithIgnoreCase(header, AUTHENTICATION_SCHEME_BASIC)) {
      return null;
    }
    if (header.equalsIgnoreCase(AUTHENTICATION_SCHEME_BASIC)) {
      throw new BadCredentialsException("Пустой токен базовой аутентификации");
    }
    var base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
    var decoded = decode(base64Token);
    var token = new String(decoded, CREDENTIALS_CHARSET);
    var delim = token.indexOf(":");
    if (delim == -1) {
      throw new BadCredentialsException("Неверный токен базовой аутентификации");
    }
    return new PreAuthenticatedAuthenticationToken(new UserCredentials(token.substring(0, delim), token.substring(delim + 1)), token);
  }

  /**
   * Метод для декодирования base64-кодированного токена аутентификации.
   *
   * @param base64Token base64-кодированный токен аутентификации.
   * @return декодированный токен аутентификации.
   * @throws BadCredentialsException если возникает ошибка при декодировании токена.
   */
  private byte[] decode(byte[] base64Token) throws BadCredentialsException {
    try {
      return Base64.getDecoder().decode(base64Token);
    } catch (IllegalArgumentException ex) {
      log.error("Ошибка декодирования информации о пользователе." + ex.getMessage());
      throw new BadCredentialsException("Не удалось декодировать базовый токен аутентификации");
    }
  }
}
