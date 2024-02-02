package com.denknd.dto;

import lombok.Builder;

/**
 * Объект для передачи адреса пользователю
 * @param addressId идентификатор адреса
 * @param region регион(область)
 * @param city город(населенный пункт)
 * @param street улица
 * @param house номер дома
 * @param apartment номер квартиры
 * @param postalCode почтовый индекс
 */
@Builder
public record AddressDto(
        Long addressId,
        String region,
        String city,
        String street,
        String house,
        String apartment,
        Long postalCode) {
}
