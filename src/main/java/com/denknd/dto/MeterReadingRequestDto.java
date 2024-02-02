package com.denknd.dto;

import lombok.Builder;

/**
 * Объект для получения показаний от пользователя
 * @param addressId идентификатор адреса
 * @param code тип(код) показаний
 * @param meterValue данные со счетчика
 */
@Builder
public record MeterReadingRequestDto(
        Long addressId,
        String code,
        double meterValue
) {
}
