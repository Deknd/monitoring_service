package com.denknd.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Объект для получения показаний от пользователя
 *
 * @param addressId   идентификатор адреса
 * @param typeMeterId идентификатор типа показаний
 * @param meterValue  данные со счетчика
 */
@Builder
public record MeterReadingRequestDto(
        @NotNull
        @Schema(description = "Идентификатор адреса")
        Long addressId,

        @NotNull
        @Schema(description = "Идентификатор типа показаний")
        Long typeMeterId,

        @NotNull
        @Digits(integer = 10, fraction = 3)
        @Schema(description = "Данные со счетчика", example = "123.456")
        double meterValue
) {
}
