package com.denknd.adapter.repository;

import com.denknd.entity.Role;
import com.denknd.port.RoleRepository;

import java.util.*;

/**
 * Хранит связь роль и пользователь
 */
public class InMemoryRoleRepository implements RoleRepository {

    /**
     * для хранения связи userId -> его роли
     */
    private final Map<Long, Set<Role>> roleUserRelationship = new HashMap<>();

    /**
     * Сохраняет связь юзер -> роль
     * @param userId идентификатор пользователя
     * @param roles массив ролей
     * @return возвращает true
     */
    @Override
    public boolean save(Long userId, Role... roles) {
        if(this.roleUserRelationship.containsKey(userId)){
            var rolesList = this.roleUserRelationship.get(userId);
            rolesList.addAll(Arrays.asList(roles));
        }else {
            this.roleUserRelationship.put(userId, new HashSet<>(Arrays.stream(roles).toList()));
        }
        return true;
    }

    /**
     * Ищет в памяти, какие роли есть у пользователя
     * @param userId идентификатор пользователя
     * @return список доступных ролей
     */
    @Override
    public List<Role> findRolesByUserId(Long userId) {
        return this.roleUserRelationship.getOrDefault(userId, Set.of()).stream().toList();
    }
}
