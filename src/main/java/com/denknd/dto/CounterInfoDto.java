package com.denknd.dto;

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
        Long addressId,
        @NotNull
        Long typeMeterId,
        @NotNull
        @Size(min = 1, max = 255)
        String serialNumber,
        @NotNull
        @Size(min = 1, max = 255)
        String meterModel) {
}
