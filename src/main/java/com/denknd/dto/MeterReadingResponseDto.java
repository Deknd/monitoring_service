package com.denknd.dto;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.time.YearMonth;

/**
 * Объект показаний счетчика, передаваемый пользователю
 * @param meterId идентификатор показаний
 * @param addressId идентификатор адреса
 * @param typeDescription описание показаний
 * @param meterValue показания счетчика
 * @param metric единица измерения счетчика
 * @param code тип(код) показаний
 * @param submissionMonth месяц подачи показаний
 * @param timeSendMeter дата подачи показаний
 */
@Builder
public record MeterReadingResponseDto(
        Long meterId,
        Long addressId,
        String typeDescription,
        double meterValue,
        String metric,
        String code,
        YearMonth submissionMonth,
        OffsetDateTime timeSendMeter) {
}
