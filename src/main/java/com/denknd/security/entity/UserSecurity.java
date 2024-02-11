package com.denknd.security.entity;

import com.denknd.entity.Roles;
import lombok.Builder;

/**
 * Пользователь в системе безопасности.
 *
 * @param firstName     Имя пользователя.
 * @param userId   Идентификатор пользователя.
 * @param role     Роли пользователя.
 * @param password Пароль пользователя.
 */
@Builder
public record UserSecurity(String firstName, Long userId, Roles role, String password, Token token) {
}
