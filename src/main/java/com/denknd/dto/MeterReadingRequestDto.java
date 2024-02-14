package com.denknd.dto;

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
        Long addressId,
        @NotNull
        Long typeMeterId,
        @NotNull
        @Digits(integer = 10, fraction = 3)
        double meterValue
) {
}
