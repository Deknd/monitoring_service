package com.denknd.security.service.impl;

import com.denknd.security.entity.Token;
import com.denknd.security.entity.UserSecurity;
import com.denknd.security.service.SecurityService;
import com.denknd.security.service.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

/**
 * Сервис для работы с авторизацией пользователя.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {
  private final TokenService tokenService;
  private static final String COOKIE_NAME = "__Host-auth-token";
  private UserSecurity userSecurity = null;
  private final Function<UserSecurity, Token> createToken;
  private final Function<Token, String> serializerToken;

  /**
   * Получение аутентифицированного пользователя.
   *
   * @return Аутентифицированный пользователь или null, если аутентификация не выполнена.
   */
  @Override
  public UserSecurity getUserSecurity() {
    return this.userSecurity;
  }


  /**
   * Проверка, аутентифицирован ли пользователь.
   *
   * @return true, если пользователь аутентифицирован, иначе false.
   */
  @Override
  public boolean isAuthentication() {
    return userSecurity != null;
  }

  /**
   * Для добавления пользователя
   *
   * @param userSecurity пользователь прошедши проверку
   */
  @Override
  public void addPrincipal(UserSecurity userSecurity) {
    this.userSecurity = userSecurity;
  }

  /**
   * Удаление информации об аутентифицированном пользователе из памяти.
   * Блокировка токена доступа.
   *
   * @return true, если аутентифицированный пользователь успешно удален, иначе false.
   */
  @Override
  public boolean logout(HttpServletResponse response) {
    if (this.isAuthentication()) {
      this.tokenService.lockToken(this.userSecurity.token());
      Cookie cookie = new Cookie(COOKIE_NAME, null);
      cookie.setPath("/");
      cookie.setDomain("");
      cookie.setSecure(true);
      cookie.setHttpOnly(true);
      cookie.setMaxAge(0);
      response.addCookie(cookie);
      this.userSecurity = null;
    }
    return this.userSecurity == null;
  }


  /**
   * Создание токена доступа и добавления его в ответ пользователю
   *
   * @param response ответ для пользователя
   */
  @Override
  public void onAuthentication(HttpServletResponse response) {
    if (this.isAuthentication()) {
      var token = this.createToken.apply(this.userSecurity);
      var tokenString = this.serializerToken.apply(token);
      var cookie = new Cookie(COOKIE_NAME, tokenString);
      cookie.setPath("/");
      cookie.setDomain("");
      cookie.setSecure(true);
      cookie.setHttpOnly(true);
      cookie.setMaxAge((int) ChronoUnit.SECONDS.between(Instant.now(), token.expiresAt()));
      response.addCookie(cookie);
    }
  }

}
