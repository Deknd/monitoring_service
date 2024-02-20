package com.denknd.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Объект для передачи типов показаний
 *
 * @param typeMeterId     Идентификатор объекта.
 * @param typeCode        тип(код) показаний
 * @param typeDescription описания показаний
 * @param metric          единица измерения показаний
 */
@Builder
public record TypeMeterDto(
        @Schema(description = "Идентификатор объекта")
        Long typeMeterId,

        @NotNull
        @Size(min = 1, max = 10)
        @Schema(description = "Тип (код) показаний", minLength = 1, maxLength = 10)
        String typeCode,

        @NotNull
        @Size(min = 1, max = 255)
        @Schema(description = "Описание показаний", minLength = 1, maxLength = 255)
        String typeDescription,

        @NotNull
        @Size(min = 1, max = 10)
        @Schema(description = "Единица измерения показаний", minLength = 1, maxLength = 10)
        String metric) {
}
