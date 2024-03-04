package com.denknd.security.utils.converter.impl;

import com.denknd.security.entity.PreAuthenticatedAuthenticationToken;
import com.denknd.security.entity.Token;
import com.denknd.security.utils.DefaultDeserializerToken;
import com.denknd.security.utils.converter.AuthenticationConverter;
import com.nimbusds.jose.JWEDecrypter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Реализация интерфейса для преобразования аутентификационных данных из HTTP запроса с использованием токена Cookie.
 */
@Component
@Setter
public class CookieAuthenticationConverter implements AuthenticationConverter {
  private Function<String, Token> deserializerToken;

  @Autowired
  public CookieAuthenticationConverter(JWEDecrypter jweDecrypter) {
    this.deserializerToken = new DefaultDeserializerToken(jweDecrypter);
  }

  /**
   * Метод для преобразования аутентификационных данных из HTTP запроса с использованием токена Cookie.
   *
   * @param httpRequest HTTP запрос.
   * @return токен аутентификации пользователя.
   */
  @Override
  public PreAuthenticatedAuthenticationToken convert(HttpServletRequest httpRequest) {
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
