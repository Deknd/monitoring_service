package com.denknd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private Long addressId;
    private User owner;
    private Long postalCode;
    private String region;
    private String city;
    private String street;
    private String house;
    private String apartment;
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
