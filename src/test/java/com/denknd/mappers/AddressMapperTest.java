package com.denknd.mappers;

import com.denknd.dto.AddressDto;
import com.denknd.entity.Address;
import com.denknd.entity.MeterReading;
import com.denknd.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AddressMapperTest {
  private AddressMapper addressMapper;

  @BeforeEach
  void setUp() {
    this.addressMapper = AddressMapper.INSTANCE;
  }

  @Test
  @DisplayName("Проверяет, что правильно маппит")
  void mapAddressDtoToAddress() {
    var addressDto = AddressDto.builder()
            .addressId(1L)
            .region("Region")
            .city("city")
            .street("street")
            .house("house")
            .apartment("apartment")
            .postalCode(123456L)
            .build();

    var address = this.addressMapper.mapAddressDtoToAddress(addressDto);

    assertThat(address).isNotNull().satisfies(
            result ->
            {
              assertThat(result.getAddressId()).isEqualTo(addressDto.addressId());
              assertThat(result.getPostalCode()).isEqualTo(addressDto.postalCode());
              assertThat(result.getRegion()).isEqualTo(addressDto.region());
              assertThat(result.getCity()).isEqualTo(addressDto.city());
              assertThat(result.getStreet()).isEqualTo(addressDto.street());
              assertThat(result.getHouse()).isEqualTo(addressDto.house());
              assertThat(result.getApartment()).isEqualTo(addressDto.apartment());
              assertThat(result.getOwner()).isNull();
              assertThat(result.getMeterReadings()).isNull();
            }
    );

  }
  @Test
  @DisplayName("Проверяет, что null не маппится")
  void mapAddressDtoToAddress_null() {
    var address = this.addressMapper.mapAddressDtoToAddress(null);

    assertThat(address).isNull();
  }

  @Test
  @DisplayName("Проверяет, что правильно маппит Address в AddressDto")
  void mapAddressToAddressDto() {
    var address = Address.builder()
            .addressId(1L)
            .owner(mock(User.class))
            .postalCode(123456L)
            .region("region")
            .city("city")
            .street("street")
            .house("house")
            .apartment("apartment")
            .meterReadings(List.of(mock(MeterReading.class)))
            .build();

    var addressDto = this.addressMapper.mapAddressToAddressDto(address);

    assertThat(addressDto).isNotNull()
            .satisfies(result ->
            {
              assertThat(result.addressId()).isEqualTo(address.getAddressId());
              assertThat(result.region()).isEqualTo(address.getRegion());
              assertThat(result.city()).isEqualTo(address.getCity());
              assertThat(result.street()).isEqualTo(address.getStreet());
              assertThat(result.house()).isEqualTo(address.getHouse());
              assertThat(result.apartment()).isEqualTo(address.getApartment());
              assertThat(result.postalCode()).isEqualTo(address.getPostalCode());
            });
  }
  @Test
  @DisplayName("Проверяет, что правильно маппит Address в AddressDto")
  void mapAddressToAddressDto_null() {
    var addressDto = this.addressMapper.mapAddressToAddressDto(null);

    assertThat(addressDto).isNull();
  }

  @Test
  @DisplayName("Маппит список Address в AddressDto список")
  void mapAddressesToAddressesDto() {
    var address = Address.builder()
            .addressId(1L)
            .owner(mock(User.class))
            .postalCode(123456L)
            .region("region")
            .city("city")
            .street("street")
            .house("house")
            .apartment("apartment")
            .meterReadings(List.of(mock(MeterReading.class)))
            .build();
    var address2 = Address.builder()
            .addressId(2L)
            .owner(mock(User.class))
            .postalCode(123456L)
            .region("region")
            .city("city")
            .street("street")
            .house("house")
            .apartment("apartment")
            .meterReadings(List.of(mock(MeterReading.class)))
            .build();
    var address3 = Address.builder()
            .addressId(3L)
            .owner(mock(User.class))
            .postalCode(123456L)
            .region("region")
            .city("city")
            .street("street")
            .house("house")
            .apartment("apartment")
            .meterReadings(List.of(mock(MeterReading.class)))
            .build();

    var addressDtos = this.addressMapper.mapAddressesToAddressesDto(List.of(address, address2, address3));

    assertThat(addressDtos.size()).isEqualTo(3);
  }

  @Test
  @DisplayName("Проверяет, что если отправить null, то получишь null")
  void mapAddressesToAddressesDto_null() {

    var addressDtos = this.addressMapper.mapAddressesToAddressesDto(null);

    assertThat(addressDtos).isNull();
  }
}