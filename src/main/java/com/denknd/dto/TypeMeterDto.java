package com.denknd.dto;

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
        Long typeMeterId,
        @NotNull
        @Size(min = 1, max = 10)
        String typeCode,
        @NotNull
        @Size(min = 1, max = 255)
        String typeDescription,
        @NotNull
        @Size(min = 1, max = 10)
        String metric) {
}
