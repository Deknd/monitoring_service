package com.denknd.dto;

import lombok.Builder;

/**
 * Объект полученный от пользователя, для регистрации нового пользователя в системе
 * @param email электронный адрес
 * @param password пароль
 * @param lastName фамилия
 * @param firstName имя
 */
@Builder
public record UserCreateDto(
        String email,
        String password,
        String lastName,
        String firstName
) {
}
