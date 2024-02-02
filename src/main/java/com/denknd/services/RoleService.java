package com.denknd.services;

import com.denknd.entity.Roles;

import java.util.List;
import java.util.Set;

/**
 * Интерфейс для работы с ролями.
 */
public interface RoleService {
  /**
   * Добавляет роль для пользователя.
   *
   * @param userId Идентификатор пользователя.
   * @param role  Роль пользователя.
   * @return Возвращает true, если роль добавилась.
   */
  boolean addRoles(Long userId, Roles role);

  /**
   * Возвращает все доступные роли пользователя.
   *
   * @param userId Идентификатор пользователя.
   * @return Возвращает роль доступную пользователю.
   */
  Roles getRoles(Long userId);



}
