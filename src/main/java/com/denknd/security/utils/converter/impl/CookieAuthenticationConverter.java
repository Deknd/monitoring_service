package com.denknd.security.utils.converter.impl;

import com.denknd.exception.BadCredentialsException;
import com.denknd.security.entity.PreAuthenticatedAuthenticationToken;
import com.denknd.security.entity.Token;
import com.denknd.security.utils.converter.AuthenticationConverter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Реализация интерфейса для преобразования аутентификационных данных из HTTP запроса с использованием токена Cookie.
 */
@RequiredArgsConstructor
public class CookieAuthenticationConverter implements AuthenticationConverter {
  /**
   * Функция для десериализации токена из строки.
   */
  private final Function<String, Token> deserializerToken;

  /**
   * Метод для преобразования аутентификационных данных из HTTP запроса с использованием токена Cookie.
   *
   * @param httpRequest HTTP запрос.
   * @return токен аутентификации пользователя.
   * @throws BadCredentialsException если происходит ошибка во время обработки аутентификационных данных.
   */
  @Override
  public PreAuthenticatedAuthenticationToken convert(HttpServletRequest httpRequest) throws BadCredentialsException {
    if (httpRequest.getCookies() == null) {
      return null;
    }
    return Stream.of(httpRequest.getCookies())
            .filter(cookie ->
                    cookie != null
                            && cookie.getName() != null
                            && cookie.getName().equals("__Host-auth-token"))
            .findFirst()
            .map(cookie -> {
              var token = this.deserializerToken.apply(cookie.getValue());
              return new PreAuthenticatedAuthenticationToken(token, cookie);
            })
            .orElse(null);
  }
}
