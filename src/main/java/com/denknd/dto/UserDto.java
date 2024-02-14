package com.denknd.dto;

import lombok.Builder;

/**
 * Объект для передачи информации о пользователе
 *
 * @param userId    идентификатор пользователя
 * @param email     электронная почта
 * @param lastName  фамилия
 * @param firstName имя
 */
@Builder
public record UserDto(
        Long userId,
        String email,
        String lastName,
        String firstName) {
}
