package com.denknd.controllers;

import com.denknd.dto.MeterReadingRequestDto;
import com.denknd.entity.Address;
import com.denknd.entity.TypeMeter;
import com.denknd.exception.MeterReadingConflictError;
import com.denknd.mappers.MeterReadingMapper;
import com.denknd.services.AddressService;
import com.denknd.services.MeterReadingService;
import com.denknd.services.TypeMeterService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.YearMonth;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MeterReadingControllerTest {
  @Mock
  private MeterReadingService meterReadingService;
  @Mock
  private AddressService addressService;
  @Mock
  private TypeMeterService typeMeterService;
  @Mock
  private MeterReadingMapper meterReadingMapper;
  private MeterReadingController meterReadingController;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);

    this.meterReadingController = new MeterReadingController(
            this.meterReadingService,
            this.addressService,
            this.typeMeterService,
            this.meterReadingMapper);

  }
  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }
  @Test
  @DisplayName("Проверяет, что метод вызывает все нужные сервисы с нужными параметрами")
  void getHistoryMeterReading() {
    var addressId = 2L;
    var userId = 1L;
    var parameters = Set.of(1L, 2L);
    var startDate = YearMonth.now().minusMonths(5);
    var endDate = YearMonth.now().minusMonths(1);
    var address = mock(Address.class);
    when(address.getAddressId()).thenReturn(addressId);
    when(this.addressService.getAddresses(eq(userId))).thenReturn(List.of(address));

    this.meterReadingController.getHistoryMeterReading(addressId, userId, parameters, startDate, endDate);

    verify(this.addressService, times(1)).getAddresses(eq(userId));
    verify(this.meterReadingService, times(1)).getHistoryMeterByAddress(any(), eq(parameters), eq(startDate), eq(endDate));
  }

  @Test
  @DisplayName("Проверяет, что выходит из метода, если нет доступных адресов")
  void getHistoryMeterReading_nutAddress() {
    var addressId = 2L;
    var userId = 1L;
    var parameters = Set.of(1L, 2L);
    var startDate = YearMonth.now().minusMonths(5);
    var endDate = YearMonth.now().minusMonths(1);
    var address = mock(Address.class);
    when(address.getAddressId()).thenReturn(addressId);
    when(this.addressService.getAddresses(eq(userId))).thenReturn(List.of());

    this.meterReadingController.getHistoryMeterReading(addressId, userId, parameters, startDate, endDate);

    verify(this.addressService, times(1)).getAddresses(eq(userId));
    verify(this.meterReadingService, times(0)).getHistoryMeterByAddress(any(), eq(parameters), eq(startDate), eq(endDate));
  }

  @Test
  @DisplayName("Проверяет, что метод вызывает все сервисы и отправляет показания на обработку")
  void addMeterReadingValue() throws MeterReadingConflictError {
    var meterReadingRequestDto = MeterReadingRequestDto.builder()
            .codeType(1L)
            .addressId(1L)
            .build();
    var userId = 1L;
    var address = mock(Address.class);
    when(address.getAddressId()).thenReturn(meterReadingRequestDto.addressId());
    when(this.addressService.getAddresses(eq(userId))).thenReturn(List.of(address));
    when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(TypeMeter.builder().typeMeterId(1L).build()));

    this.meterReadingController.addMeterReadingValue(meterReadingRequestDto, userId);

    verify(this.typeMeterService, times(1)).getTypeMeter();
    verify(this.addressService, times(1)).getAddressByAddressId(eq(meterReadingRequestDto.addressId()));
    verify(this.meterReadingService, times(1)).addMeterValue(any());
  }

  @Test
  @DisplayName("Проверяет, что когда у пользователя нет адресов, выкидывает ошибку")
  void addMeterReadingValue_notOwnerAddress() throws MeterReadingConflictError {
    var meterReadingRequestDto = MeterReadingRequestDto.builder()
            .codeType(1L)
            .addressId(5L)
            .build();
    var userId = 1L;
    when(this.addressService.getAddresses(eq(userId))).thenReturn(List.of());

    assertThatThrownBy(() -> this.meterReadingController.addMeterReadingValue(meterReadingRequestDto, userId)).isInstanceOf(MeterReadingConflictError.class);


    verify(this.typeMeterService, times(0)).getTypeMeter();
    verify(this.addressService, times(0)).getAddressByAddressId(any());
    verify(this.meterReadingService, times(0)).addMeterValue(any());
  }

  @Test
  @DisplayName("Проверяет, что метод вызывает все нужные сервисы")
  void getMeterReadings() {
    var addressId = 1L;
    var userId = 2L;
    var types = Set.of(1L, 2L);
    var date = YearMonth.now();
    var address = mock(Address.class);
    when(address.getAddressId()).thenReturn(addressId);
    when(this.addressService.getAddresses(eq(userId))).thenReturn(List.of(address));
    var typeMeter = mock(TypeMeter.class);
    when(typeMeter.getTypeCode()).thenReturn("type");
    when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(typeMeter));

    this.meterReadingController.getMeterReadings(addressId, userId, types, date);

    verify(this.addressService, times(1)).getAddresses(eq(userId));
    verify(this.typeMeterService, times(1)).getTypeMeter();
    verify(this.meterReadingService, times(1)).getActualMeterByAddress(any(), any(), eq(date));
  }

  @Test
  @DisplayName("Проверяет, что если нет у пользователя адресов, то метод возвращает пустой лист")
  void getMeterReadings_notAddress() {
    var addressId = 1L;
    var userId = 2L;
    var types = Set.of(1L, 2L);
    var date = YearMonth.now();
    when(this.addressService.getAddresses(eq(userId))).thenReturn(List.of());

    this.meterReadingController.getMeterReadings(addressId, userId, types, date);

    verify(this.addressService, times(1)).getAddresses(eq(userId));
    verify(this.typeMeterService, times(0)).getTypeMeter();
    verify(this.meterReadingService, times(0)).getActualMeterByAddress(any(), any(), eq(date));
  }
}