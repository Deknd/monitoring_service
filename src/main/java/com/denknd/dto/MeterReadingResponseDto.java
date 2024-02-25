package com.denknd.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.time.YearMonth;

/**
 * Объект показаний счетчика, передаваемый пользователю
 *
 * @param meterId         идентификатор показаний
 * @param addressId       идентификатор адреса
 * @param typeDescription описание показаний
 * @param meterValue      показания счетчика
 * @param metric          единица измерения счетчика
 * @param code            тип(код) показаний
 * @param submissionMonth месяц подачи показаний
 * @param timeSendMeter   дата подачи показаний
 */
@Builder
public record MeterReadingResponseDto(
        @Schema(description = "Идентификатор показаний")
        Long meterId,
        @Schema(description = "Идентификатор адреса")
        Long addressId,
        @Schema(description = "Описание показаний")
        String typeDescription,
        @Schema(description = "Показания счетчика")
        double meterValue,
        @Schema(description = "Единица измерения счетчика")
        String metric,
        @Schema(description = "Тип (код) показаний")
        String code,
        @Schema(description = "Месяц подачи показаний")
        YearMonth submissionMonth,
        @Schema(description = "Дата подачи показаний")
        OffsetDateTime timeSendMeter) {
}
