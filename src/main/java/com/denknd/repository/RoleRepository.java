package com.denknd.repository;


import com.denknd.entity.Roles;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для работы с ролями пользователей.
 */
public interface RoleRepository {
  /**
   * Сохраняет в память связь между ролью и пользователем.
   *
   * @param userId Идентификатор пользователя.
   * @param role  Роль пользователя.
   * @return true, если сохранение прошло успешно.
   */
  boolean save(Long userId, Roles role);

  /**
   * Извлекает из памяти все доступные роли пользователя.
   *
   * @param userId Идентификатор пользователя.
   * @return Роль пользователя.
   */
  Optional<Roles> findRolesByUserId(Long userId);
}
