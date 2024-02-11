package com.denknd.security.service;

import com.denknd.security.entity.UserSecurity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.SQLException;

/**
 * Интерфейс для работы с авторизацией и аутентификацией пользователя.
 */
public interface SecurityService {


  /**
   * Возвращает объект пользователя безопасности (UserSecurity), если проверка прошла успешно,
   * или null, если проверка не удалась.
   *
   * @return Возвращает объект пользователя безопасности (UserSecurity), если проверка прошла успешно,
   *         или null, если проверка не удалась.
   */
  UserSecurity getUserSecurity();

  /**
   * Проверяет, аутентифицирован ли пользователь.
   *
   * @return true, если пользователь аутентифицирован, иначе false.
   */
  boolean isAuthentication();
  void addPrincipal(UserSecurity userSecurity);

  /**
   * Удаляет информацию о текущем пользователе из памяти.
   *
   * @return true, если пользователь успешно вышел из системы, иначе false.
   */
  boolean logout(HttpServletResponse response);
  /**
   * Создание токена доступа и добавления его в ответ пользователю
   *
   * @param response ответ для пользователя
   */
  void onAuthentication(HttpServletResponse response);
}