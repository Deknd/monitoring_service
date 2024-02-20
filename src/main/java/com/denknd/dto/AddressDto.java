package com.denknd.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Объект для передачи адреса пользователю
 *
 * @param addressId  идентификатор адреса
 * @param region     регион(область)
 * @param city       город(населенный пункт)
 * @param street     улица
 * @param house      номер дома
 * @param apartment  номер квартиры
 * @param postalCode почтовый индекс
 */
@Builder
public record AddressDto(
        Long addressId,
        @NotNull
        @Size(min = 2, max = 50)
        String region,
        @NotNull
        @Size(min = 2, max = 50)
        String city,
        @NotNull
        @Size(min = 2, max = 50)
        String street,
        @NotNull
        @Size(min = 2, max = 50)
        String house,
        @Size(min = 0, max = 5)
        String apartment,
        @Digits(integer=6, fraction=0, message="Число должно состоять из 6 цифр")
        Long postalCode) {
}
