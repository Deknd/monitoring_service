package com.denknd.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
        @Schema(description = "Идентификатор пользователя")
        Long userId,

        @Schema(description = "Электронная почта")
        String email,

        @Schema(description = "Фамилия")
        String lastName,

        @Schema(description = "Имя")
        String firstName) {
}
