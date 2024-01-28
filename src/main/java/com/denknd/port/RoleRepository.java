package com.denknd.port;

import com.denknd.entity.Role;

import java.util.List;
import java.util.Set;

/**
 * Интерфейс для работы с ролями
 */
public interface RoleRepository {
    /**
     * Сохраняет в память связь между ролью и пользователем
     * @param userId идентификатор пользователя
     * @param role массив его ролей
     * @return возвращает true при удачном сохранении
     */
    boolean save(Long userId, Role... role);

    /**
     * Достает из памяти все доступные роли
     * @param userId идентификатор пользователя
     * @return список доступных ролей
     */
    List<Role> findRolesByUserId(Long userId);
 }
