package com.denknd.services.impl;

import com.denknd.entity.Role;
import com.denknd.port.RoleRepository;
import com.denknd.services.RoleService;

import java.util.*;

/**
 * Сервис для работы с Ролями
 */
public class RoleServiceImpl implements RoleService {
    /**
     * Место с актуальными ролями
     */
    private final Set<Role> defaultRoles;
    /**
     * репозиторий для хранения связей между ролями и пользователем
     */
    private final RoleRepository roleRepository;

    /**
     * Создается две роли
     * @param repository репозиторий для хранения связей между ролями и пользователем
     */
    public RoleServiceImpl(RoleRepository repository) {
        this.roleRepository = repository;
        this.defaultRoles = new HashSet<>();

        var roleUser = Role.builder().roleId(0L).roleName("USER").build();
        var roleADMIN = Role.builder().roleId(1L).roleName("ADMIN").build();

        this.defaultRoles.add(roleUser);
        this.defaultRoles.add(roleADMIN);
    }

    /**
     * Метод для добавления связи пользователя и роли
     * @param userId идентификатор пользователя
     * @param roles роль с которой он связывается
     * @return true если такая роль существует
     */
    @Override
    public boolean addRoles(Long userId, Role... roles) {
        var roleArray = Arrays.stream(roles)
                .filter(role -> {
                    for (var roleDefault : this.defaultRoles) {
                        if (role.equals(roleDefault)) {
                            return true;
                        }
                    }
                    return false;
                }).toArray(Role[]::new);

        if(roleArray.length == 0){
            return false;
        }

        return this.roleRepository.save(userId, roleArray);
    }

    /**
     * Список всех связанных с пользователем ролей
     * @param userId айди пользователя
     * @return список ролей
     */
    @Override
    public List<Role> getRoles(Long userId) {
        return this.roleRepository.findRolesByUserId(userId);
    }

    /**
     * Выдает список дефолтных ролей
     * @return роли доступные пользователям
     */
    @Override
    public Set<Role> getDefaultRole() {
        return this.defaultRoles;
    }
}
