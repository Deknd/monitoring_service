package com.denknd.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Класс для передачи информации о счетчике.
 *
 * @param addressId    идентификатор адреса
 * @param typeMeterId  идентификатор типа показаний, который он представляет
 * @param serialNumber серийный номер счетчика.
 * @param meterModel   модель счетчика.
 */
@Builder
public record CounterInfoDto(
        @NotNull
        @Schema(description = "Идентификатор адреса")
        Long addressId,

        @NotNull
        @Schema(description = "Идентификатор типа показаний, который он представляет")
        Long typeMeterId,

        @NotNull
        @Size(min = 1, max = 255)
        @Schema(description = "Серийный номер счетчика", maxLength = 255)
        String serialNumber,

        @NotNull
        @Size(min = 1, max = 255)
        @Schema(description = "Модель счетчика", maxLength = 255)
        String meterModel) {
}
