package com.denknd.security.entity;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

/**
 * Токен доступа к авторизации
 * @param id идентификатор токена
 * @param userId идентификатор пользователя
 * @param firstName имя пользователя
 * @param role роль пользователя
 * @param createdAt дата создания
 * @param expiresAt дата прекращения действия токена
 */
@Builder
public record Token(
        UUID id,
        Long userId,
        String firstName,
        String role,
        Instant createdAt,
        Instant expiresAt) {
}
