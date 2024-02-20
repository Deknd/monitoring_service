package com.denknd.security.entity;

import lombok.Builder;

import java.time.OffsetDateTime;

/**
 * Токен для блокировки токена доступа
 * @param tokenBlockId идентификатор токена блокировки
 * @param tokenId идентификатор токена, который нужно заблокировать
 * @param expirationTime время действия заблокированного токена
 */
@Builder
public record TokenBlock(Long tokenBlockId, String tokenId, OffsetDateTime expirationTime) {
}
