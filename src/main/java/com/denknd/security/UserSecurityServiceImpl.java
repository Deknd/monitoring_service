package com.denknd.security;

import com.denknd.mappers.UserMapper;
import com.denknd.services.UserService;
import lombok.RequiredArgsConstructor;

/**
 * Сервис для получения объекта пользователя {@link UserSecurity} для системы безопасности.
 */
@RequiredArgsConstructor
public class UserSecurityServiceImpl implements UserSecurityService {
  /**
   * Сервис для работы с пользователями в базе данных.
   */
  private final UserService userService;
  /**
   * Маппер для пользователя.
   */
  private final UserMapper userMapper;

  /**
   * Получает пользователя из базы данных и преобразует его в объект {@link UserSecurity}.
   *
   * @param email Электронная почта для получения пользователя.
   * @return Возвращает полностью собранного пользователя {@link UserSecurity} или null, если пользователя с указанной электронной почтой не существует.
   */
  @Override
  public UserSecurity getUserSecurity(String email) {
    if (this.userService.existUserByEmail(email)) {
      var userByEmail = this.userService.getUserByEmail(email);
      return this.userMapper.mapUserToUserSecurity(userByEmail);
    }
    return null;
  }
}
