package com.denknd.security;

/**
 * Интерфейс для получения объекта пользователя UserSecurity.
 */
public interface UserSecurityService {
  /**
   * Получает пользователя по электронной почте.
   *
   * @param email Электронная почта для получения пользователя.
   * @return Если пользователь с указанной электронной почтой существует, возвращает полностью заполненный объект пользователя UserSecurity, иначе возвращает null.
   */
  UserSecurity getUserSecurity(String email);
}
