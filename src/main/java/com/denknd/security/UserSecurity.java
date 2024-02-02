package com.denknd.security;

import com.denknd.entity.Roles;
import lombok.Builder;

/**
 * Пользователь в системе безопасности.
 *
 * @param name     Имя пользователя.
 * @param userId   Идентификатор пользователя.
 * @param role     Роли пользователя.
 * @param password Пароль пользователя.
 */
@Builder
public record UserSecurity(String name, Long userId, Roles role, String password) {
}
