package com.denknd.services.impl;

import com.denknd.entity.Roles;
import com.denknd.repository.RoleRepository;
import com.denknd.services.RoleService;

/**
 * Сервис для управления ролями пользователей.
 */
public class RoleServiceImpl implements RoleService {

  /**
   * Репозиторий для хранения связей между ролями и пользователями.
   */
  private final RoleRepository roleRepository;

  /**
   * Конструктор с параметром для инициализации репозитория и дефолтных ролей.
   *
   * @param roleRepository Репозиторий для хранения связей ролей и пользователей.
   */
  public RoleServiceImpl(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }


  /**
   * Добавляет роли пользователю и возвращает true, если роль существует.
   *
   * @param userId Идентификатор пользователя.
   * @param role   Роль, которую необходимо добавить пользователю.
   * @return true, если все переданные роли существуют.
   */
  @Override
  public boolean addRoles(Long userId, Roles role) {

    return this.roleRepository.save(userId, role);
  }

  /**
   * Возвращает список всех ролей, связанных с пользователем.
   *
   * @param userId Идентификатор пользователя.
   * @return Список ролей пользователя.
   */
  @Override
  public Roles getRoles(Long userId) {
    return this.roleRepository.findRolesByUserId(userId).orElse(null);
  }


}
