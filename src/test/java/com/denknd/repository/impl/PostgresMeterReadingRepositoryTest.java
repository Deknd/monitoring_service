package com.denknd.repository.impl;

import com.denknd.entity.Address;
import com.denknd.entity.MeterReading;
import com.denknd.entity.TypeMeter;
import com.denknd.repository.TestContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.YearMonth;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PostgresMeterReadingRepositoryTest extends TestContainer {

  private PostgresMeterReadingRepository meterRepository;
  @BeforeEach
  void setUp() {
    this.meterRepository = new PostgresMeterReadingRepository(postgresContainer.getDataBaseConnection());
  }

  @Test
  @DisplayName("Проверяет что объект сохраняется и генерирует id")
  void save() throws SQLException {
    var address = mock(Address.class);
    when(address.getAddressId()).thenReturn(1L);
    var meterReading = MeterReading.builder()
            .address(address)
            .typeMeter(TypeMeter.builder().typeMeterId(1L).build())
            .meterValue(1313.43)
            .submissionMonth(YearMonth.now())
            .timeSendMeter(OffsetDateTime.now())
            .build();

    var save = this.meterRepository.save(meterReading);
    assertThat(save.getMeterId()).isNotNull();
  }
  @Test
  @DisplayName("Проверяет что при попытки обновить данные в показаниях, возвращается null")
  void save_attemptUpdate() throws SQLException {
    var address = mock(Address.class);
    when(address.getAddressId()).thenReturn(1L);
    var meterReading = MeterReading.builder()
            .meterId(123L)
            .address(address)
            .typeMeter(TypeMeter.builder().typeMeterId(1L).build())
            .meterValue(1313.43)
            .submissionMonth(YearMonth.now())
            .timeSendMeter(OffsetDateTime.now())
            .build();

    assertThatThrownBy(()-> this.meterRepository.save(meterReading));
  }
  @Test
  @DisplayName("Проверяет что при попытки добавить данные к одному и тому же данному")
  void save_addsDataSingleAddress() throws SQLException {
    var address = mock(Address.class);
    when(address.getAddressId()).thenReturn(2L);
    var meterReading = MeterReading.builder()
            .address(address)
            .typeMeter(TypeMeter.builder().typeMeterId(2L).build())
            .meterValue(1313243.43)
            .submissionMonth(YearMonth.now())
            .timeSendMeter(OffsetDateTime.now())
            .build();

    var save = this.meterRepository.save(meterReading);

    assertThat(save.getMeterId()).isNotNull();
  }
  @Test
  @DisplayName("Достает из бд по адресу список показаний")
  void findMeterReadingByAddressId() {
    var addressId = 2L;

    var meterReadingByAddressId = this.meterRepository.findMeterReadingByAddressId(addressId);

    assertThat(meterReadingByAddressId.size()>=3).isTrue();

  }
  @Test
  @DisplayName("Достает из бд по адресу список показаний")
  void findMeterReadingByAddressId_notAddress() {
    var addressId = 23L;

    var meterReadingByAddressId = this.meterRepository.findMeterReadingByAddressId(addressId);

    assertThat(meterReadingByAddressId.isEmpty()).isTrue();

  }

  @Test
  @DisplayName("Ищет актуальные показания счетчиков")
  void findActualMeterReading(){
    var addressId = 2L;
    var typeId = 2L;

    var actualMeterReading = this.meterRepository.findActualMeterReading(addressId, typeId);

    assertThat(actualMeterReading).isPresent();
    var meterReading = actualMeterReading.get();
    assertThat(meterReading.getSubmissionMonth()).isEqualTo(YearMonth.now());
  }
  @Test
  @DisplayName("возвращает пустой опшинал, если нет данных по этому типу данных")
  void findActualMeterReading_NotType(){
    var addressId = 2L;
    var typeId = 3L;

    var actualMeterReading = this.meterRepository.findActualMeterReading(addressId, typeId);

    assertThat(actualMeterReading).isEmpty();
  }

  @Test
  @DisplayName("возвращает пустой опшинал, если нет данных по этому адресу")
  void findActualMeterReading_notAddress(){
    var addressId = 5L;
    var typeId = 2L;

    var actualMeterReading = this.meterRepository.findActualMeterReading(addressId, typeId);

    assertThat(actualMeterReading).isEmpty();
  }
  @Test
  @DisplayName("Ищет показания по указанной дате")
  void findMeterReadingForDate(){
    var addressId = 2L;
    var typeId = 2L;
    var date = YearMonth.now().minusMonths(1);

    var actualMeterReading = this.meterRepository.findMeterReadingForDate(addressId, typeId, date);

    assertThat(actualMeterReading).isPresent();
    var meterReading = actualMeterReading.get();
    assertThat(meterReading.getSubmissionMonth()).isEqualTo(date);
    System.out.println(meterReading);

  }
  @Test
  @DisplayName("Ищет показания по указанной дате и возвращает пустой опшинал, если показаний по этой дате нет")
  void findMeterReadingForDate_notMeterForDate(){
    var addressId = 2L;
    var typeId = 2L;
    var date = YearMonth.now().minusMonths(3);

    var actualMeterReading = this.meterRepository.findMeterReadingForDate(addressId, typeId, date);

    assertThat(actualMeterReading).isEmpty();

  }
  @Test
  @DisplayName("Проверяет, что возвращает пустой опшинал, когда нет такого адреса")
  void findMeterReadingForDate_notAddress(){
    var addressId = 5L;
    var typeId = 2L;
    var date = YearMonth.now().minusMonths(3);

    var actualMeterReading = this.meterRepository.findMeterReadingForDate(addressId, typeId, date);

    assertThat(actualMeterReading).isEmpty();

  }
}