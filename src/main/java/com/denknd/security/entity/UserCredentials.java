package com.denknd.security.entity;

/**
 * Данные пользователя для регистрации
 * @param email электронный адрес пользователя
 * @param rawPassword сырой пароль
 */
public record UserCredentials(String email, String rawPassword) {
}
