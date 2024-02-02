package com.denknd.security;

import com.denknd.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;

/**
 * Сервис для работы с авторизацией пользователя.
 */
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {
  /**
   * Сервис для работы с пользователями в системе.
   */
  private final UserSecurityService userSecurityService;
  /**
   * Кодировщик паролей.
   */
  private final PasswordEncoder passwordEncoder;
  /**
   * Аутентифицированный пользователь.
   */
  private UserSecurity userSecurity = null;

  /**
   * Аутентификация пользователя по электронной почте и паролю.
   *
   * @param email       Электронная почта пользователя.
   * @param rawPassword Не зашифрованный пароль пользователя.
   * @return Аутентифицированный пользователь или null, если аутентификация не удалась.
   */
  @Override
  public UserSecurity authentication(String email, String rawPassword) {
    var userSecurity = this.userSecurityService.getUserSecurity(email);
    if (userSecurity != null && this.passwordEncoder.matches(rawPassword, userSecurity.password()))
    {
      this.userSecurity = userSecurity;
      return this.userSecurity;
    }
    return null;
  }

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
   * Удаление информации об аутентифицированном пользователе из памяти.
   *
   * @return true, если аутентифицированный пользователь успешно удален, иначе false.
   */
  @Override
  public boolean logout() {
    this.userSecurity = null;
    return this.userSecurity == null;
  }
}
