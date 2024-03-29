package com.denknd.security.utils.authenticator.impl;

import com.denknd.mappers.UserMapper;
import com.denknd.security.entity.PreAuthenticatedAuthenticationToken;
import com.denknd.security.entity.UserCredentials;
import com.denknd.security.entity.UserSecurity;
import com.denknd.security.utils.authenticator.UserAuthenticator;
import com.denknd.services.UserService;
import com.denknd.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Реализация интерфейса аутентификации пользователей.
 */
@Component
@RequiredArgsConstructor
public class BasicUserAuthenticator implements UserAuthenticator {
  private final UserService userService;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  /**
   * Метод для аутентификации пользователя.
   *
   * @param authenticationToken токен аутентификации пользователя.
   * @return объект с данными пользователя в случае успешной аутентификации, в противном случае - null.
   */
  @Override
  public UserSecurity authentication(PreAuthenticatedAuthenticationToken authenticationToken) {
    if (authenticationToken.principal() instanceof UserCredentials userCredentials) {
      if (this.userService.existUserByEmail(userCredentials.email())) {
        var userByEmail = this.userService.getUserByEmail(userCredentials.email());
        var userSecurity = this.userMapper.mapUserToUserSecurity(userByEmail);
        if (userSecurity != null && this.passwordEncoder.matches(userCredentials.rawPassword(), userSecurity.password())) {
          return userSecurity;
        }
      }
    }
    return null;
  }
}