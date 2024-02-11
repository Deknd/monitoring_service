package com.denknd.mappers;

import com.denknd.dto.AddressDto;
import com.denknd.entity.Address;
import com.denknd.entity.MeterReading;
import com.denknd.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

  @Test
  @DisplayName("Проверяет, что маппится ResultSet в Address")
  void mapResultSetToAddress() throws SQLException {
    var resultSet = mock(ResultSet.class);
    when(resultSet.getLong(eq("address_id"))).thenReturn(1L);
    when(resultSet.getLong(eq("user_id"))).thenReturn(2L);
    when(resultSet.getLong(eq("user_id"))).thenReturn(3L);
    when(resultSet.getString(eq("region"))).thenReturn("region set");
    when(resultSet.getString(eq("city"))).thenReturn("city set");
    when(resultSet.getString(eq("street"))).thenReturn("street set");
    when(resultSet.getString(eq("house"))).thenReturn("house set");
    when(resultSet.getString(eq("apartment"))).thenReturn("apartment set");

    var address = this.addressMapper.mapResultSetToAddress(resultSet);

    assertThat(address.getAddressId()).isNotNull();
    assertThat(address.getOwner()).isNotNull();
    assertThat(address.getPostalCode()).isNotNull();
    assertThat(address.getRegion()).isNotNull();
    assertThat(address.getCity()).isNotNull();
    assertThat(address.getHouse()).isNotNull();
    assertThat(address.getApartment()).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, что выпадает ошибка, когда не находится нужная колонка")
  void mapResultSetToAddress_() throws SQLException {
    var resultSet = mock(ResultSet.class);
    when(resultSet.getLong(eq("address_id"))).thenReturn(1L);
    when(resultSet.getLong(eq("user_id"))).thenReturn(2L);
    when(resultSet.getLong(eq("user_id"))).thenReturn(3L);
    when(resultSet.getString(eq("region"))).thenThrow(SQLException.class);
    when(resultSet.getString(eq("city"))).thenReturn("city set");
    when(resultSet.getString(eq("street"))).thenReturn("street set");
    when(resultSet.getString(eq("house"))).thenReturn("house set");
    when(resultSet.getString(eq("apartment"))).thenReturn("apartment set");

    assertThatThrownBy(() -> this.addressMapper.mapResultSetToAddress(resultSet));
  }
}