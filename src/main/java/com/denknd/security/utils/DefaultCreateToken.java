package com.denknd.security.utils;

import com.denknd.security.entity.Token;
import com.denknd.security.entity.UserSecurity;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;

/**
 * Класс для создания токена доступа
 */
@RequiredArgsConstructor
public class DefaultCreateToken implements Function<UserSecurity, Token> {
    /**
     * Время жизни токена
     */
    private final Duration expiration;

    /**
     * Создает токен доступа из UserSecurity
     * @param authentication пользователь из системы безопасности
     * @return токен доступа
     */
    @Override
    public Token apply(UserSecurity authentication) {
        var now = Instant.now();
        return new Token(UUID.randomUUID(), authentication.userId(),
                authentication.firstName(),
                authentication.role().name(),
                now, now.plus(expiration));
    }
}
