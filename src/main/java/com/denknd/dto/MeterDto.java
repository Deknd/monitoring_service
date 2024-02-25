package com.denknd.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

/**
 * Класс, для передачи пользователю информации о счетчике
 *
 * @param meterCountId     идентификатор счетчика
 * @param addressId        идентификатор адреса по которому находится счетчик
 * @param typeMeterId      идентификатор типа показаний, которые счетчик считает
 * @param serialNumber     серийный номер счетчика
 * @param meterModel       модель счетчика
 * @param registrationDate дата первой подаче показаний
 * @param lastCheckDate    дата последней проверки
 */
public record MeterDto(
        @Schema(description = "Идентификатор счетчика")
        Long meterCountId,

        @Schema(description = "Идентификатор адреса, по которому находится счетчик")
        Long addressId,

        @Schema(description = "Идентификатор типа показаний, которые счетчик считает")
        Long typeMeterId,

        @Schema(description = "Серийный номер счетчика")
        String serialNumber,

        @Schema(description = "Модель счетчика")
        String meterModel,

        @Schema(description = "Дата первой подаче показаний")
        OffsetDateTime registrationDate,

        @Schema(description = "Дата последней проверки")
        OffsetDateTime lastCheckDate) {
}
