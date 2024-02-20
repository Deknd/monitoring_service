package com.denknd.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
        @Email
        String email,
        @NotNull
        @Size(min = 2, max = 50)
        String password,
        @NotNull
        @Size(min = 2, max = 50)
        String lastName,
        @NotNull
        @Size(min = 2, max = 50)
        String firstName
) {
}
