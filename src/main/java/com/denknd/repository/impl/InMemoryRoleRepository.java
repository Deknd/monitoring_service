package com.denknd.repository.impl;

import com.denknd.entity.Roles;
import com.denknd.repository.RoleRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Реализация интерфейса для хранения связей ролей и пользователей в памяти.
 */
public class InMemoryRoleRepository implements RoleRepository {

  /**
   * Хранение связей userId -> его роли.
   */
  private final Map<Long, Roles> roleUserRelationship = new HashMap<>();

  /**
   * Сохраняет связь пользователя и роли.
   *
   * @param userId Идентификатор пользователя.
   * @param role   Массив ролей.
   * @return true, если успешно сохранено.
   */
  @Override
  public boolean save(Long userId, Roles role) {
    if (this.roleUserRelationship.containsKey(userId)) {
      return false;
    } else {
      this.roleUserRelationship.put(userId, role);
    }
    return true;
  }

  /**
   * Ищет в памяти, какие роли есть у пользователя.
   *
   * @param userId Идентификатор пользователя.
   * @return Список доступных ролей.
   */
  @Override
  public Optional<Roles> findRolesByUserId(Long userId) {
    return Optional.ofNullable(this.roleUserRelationship.get(userId));
  }
}
