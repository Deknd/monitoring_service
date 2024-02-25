package com.denknd.security.utils.authenticator.impl;

import com.denknd.entity.Roles;
import com.denknd.security.entity.PreAuthenticatedAuthenticationToken;
import com.denknd.security.entity.Token;
import com.denknd.security.entity.UserSecurity;
import com.denknd.security.service.TokenService;
import com.denknd.security.utils.authenticator.UserAuthenticator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Реализация интерфейса аутентификации пользователей с использованием токена из Cookie.
 */
@Component
@RequiredArgsConstructor
public class CookieUserAuthenticator implements UserAuthenticator {
  private final TokenService tokenService;

  /**
   * Метод для аутентификации пользователя на основе токена из Cookie.
   *
   * @param authenticationToken токен аутентификации пользователя.
   * @return объект с данными пользователя в случае успешной аутентификации, в противном случае - null.
   */
  @Override
  public UserSecurity authentication(PreAuthenticatedAuthenticationToken authenticationToken) {
    if (authenticationToken.principal() instanceof Token token) {
      if (token.expiresAt().isBefore(Instant.now()) || this.tokenService.existsByTokenId(token.id().toString())) {
        return null;
      }
      return UserSecurity.builder()
              .firstName(token.firstName())
              .userId(token.userId())
              .role(Roles.valueOf(token.role()))
              .token(token)
              .build();

    }
    return null;
  }
}
