package com.denknd.security.utils.authenticator;

import com.denknd.security.entity.PreAuthenticatedAuthenticationToken;
import com.denknd.security.entity.UserSecurity;
/**
 * Интерфейс для аутентификации пользователей.
 */
public interface UserAuthenticator {
  /**
   * Метод для аутентификации пользователя.
   *
   * @param authenticationToken токен аутентификации пользователя.
   * @return объект с данными пользователя в случае успешной аутентификации, в противном случае - null.
   */
  UserSecurity authentication(PreAuthenticatedAuthenticationToken authenticationToken);
}
