package com.denknd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Сущность для хранения адресов
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    /**
     * Идентификатор
     */
    private Long addressId;
    /**
     * Владелец данного адреса
     */
    private User owner;
    /**
     * Почтовый индекс(6 цифр)
     */
    private Long postalCode;
    /**
     * Регион России
     */
    private String region;
    /**
     * Населенный пункт
     */
    private String city;
    /**
     * Улица
     */
    private String street;
    /**
     * Дом
     */
    private String house;
    /**
     * Квартира
     */
    private String apartment;
    /**
     * Для дальнейшей связи с показаниями
     */
    private List<MeterReading> meterReadings;

    @Override
    public String toString() {
        return  "Регион: '" + region + '\'' +
                ", населенный пункт: '" + city + '\'' +
                ", улица: '" + street + '\'' +
                ", дом: '" + house + '\'' +
                ", квартира: '" + apartment + '\'';
    }
}
