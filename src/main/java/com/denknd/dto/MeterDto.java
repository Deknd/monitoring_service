package com.denknd.dto;

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
        Long meterCountId,
        Long addressId,
        Long typeMeterId,
        String serialNumber,
        String meterModel,
        OffsetDateTime registrationDate,
        OffsetDateTime lastCheckDate) {
}
