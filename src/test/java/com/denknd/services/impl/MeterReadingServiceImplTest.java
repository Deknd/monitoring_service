package com.denknd.services.impl;

import com.denknd.entity.Address;
import com.denknd.entity.MeterReading;
import com.denknd.entity.Parameters;
import com.denknd.entity.Roles;
import com.denknd.entity.TypeMeter;
import com.denknd.exception.MeterReadingConflictError;
import com.denknd.repository.MeterReadingRepository;
import com.denknd.security.entity.UserSecurity;
import com.denknd.security.service.SecurityService;
import com.denknd.services.AddressService;
import com.denknd.services.MeterCountService;
import com.denknd.services.TypeMeterService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MeterReadingServiceImplTest {
  @Mock
  private MeterReadingRepository readingRepository;
  private MeterReadingServiceImpl meterReadingService;
  @Mock
  private TypeMeterService typeMeterService;
  @Mock
  private MeterCountService meterCountService;
  @Mock
  private AddressService addressService;
  @Mock
  private SecurityService securityService;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.meterReadingService = new MeterReadingServiceImpl(
            this.readingRepository,
            this.typeMeterService,
            this.meterCountService,
            this.addressService,
            this.securityService);
  }

  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }

  @Test
  @DisplayName("Проверяет, что вызывается репозиторий с ожидаемым аргументом")
  void addMeterValue() throws MeterReadingConflictError, SQLException, AccessDeniedException {
    var address = Address.builder().addressId(12L).build();
    var typeMeter = TypeMeter.builder().typeMeterId(5L).build();
    var meterReading = MeterReading.builder()
            .meterValue(123123.43)
            .typeMeter(typeMeter)
            .address(address)
            .build();
    var userSecurity = UserSecurity.builder().role(Roles.USER).userId(5L).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);
    when(this.addressService.getAddresses(any())).thenReturn(List.of(address));
    when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(typeMeter));

    this.meterReadingService.addMeterValue(meterReading);

    var meterReadingCaptor = ArgumentCaptor.forClass(MeterReading.class);
    verify(this.readingRepository, times(1)).save(meterReadingCaptor.capture());
    var argument = meterReadingCaptor.getValue();
    assertThat(argument.getSubmissionMonth()).isEqualTo(YearMonth.now());
    assertThat(argument.getTimeSendMeter()).isNotNull().isBefore(OffsetDateTime.now());
    assertThat(argument.getMeter()).isNull();
  }

  @Test
  @DisplayName("Проверяет, что при попытке сохранинть показания на не существующий тип, выпадает ошибка")
  void addMeterValue_notType() throws SQLException {
    var address = Address.builder().addressId(12L).build();
    var typeMeter = TypeMeter.builder().typeMeterId(5L).build();
    var meterReading = MeterReading.builder()
            .meterValue(123123.43)
            .typeMeter(typeMeter)
            .address(address)
            .build();
    var userSecurity = UserSecurity.builder().role(Roles.USER).userId(5L).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);
    when(this.addressService.getAddresses(any())).thenReturn(List.of(address));
    when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(mock(TypeMeter.class)));

    assertThatThrownBy(() -> this.meterReadingService.addMeterValue(meterReading)).isInstanceOf(MeterReadingConflictError.class);

    verify(this.readingRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Проверяет, что при попытке сохранинть показания на не существующий адрес, выпадает ошибка")
  void addMeterValue_notAddress() throws SQLException {
    var address = Address.builder().addressId(12L).build();
    var typeMeter = TypeMeter.builder().typeMeterId(5L).build();
    var meterReading = MeterReading.builder()
            .meterValue(123123.43)
            .typeMeter(typeMeter)
            .address(address)
            .build();
    var userSecurity = UserSecurity.builder().role(Roles.USER).userId(5L).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);
    when(this.addressService.getAddresses(any())).thenReturn(List.of(mock(Address.class)));

    assertThatThrownBy(() -> this.meterReadingService.addMeterValue(meterReading)).isInstanceOf(MeterReadingConflictError.class);

    verify(this.readingRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Проверяет, что вызывается репозиторий с ожидаемым аргументом, при сохранении информации о счетчике выпадает ошибка")
  void addMeterValue_saveMeter_SQLException() throws MeterReadingConflictError, SQLException, AccessDeniedException {
    var address = Address.builder().addressId(12L).build();
    var typeMeter = TypeMeter.builder().typeMeterId(5L).build();
    var meterReading = MeterReading.builder()
            .meterValue(123123.43)
            .typeMeter(typeMeter)
            .address(address)
            .build();
    var userSecurity = UserSecurity.builder().role(Roles.USER).userId(5L).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);
    when(this.addressService.getAddresses(any())).thenReturn(List.of(address));
    when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(typeMeter));
    when(this.meterCountService.saveMeterCount(any())).thenThrow(SQLException.class);

    this.meterReadingService.addMeterValue(meterReading);

    var meterReadingCaptor = ArgumentCaptor.forClass(MeterReading.class);
    verify(this.readingRepository, times(1)).save(meterReadingCaptor.capture());
    var argument = meterReadingCaptor.getValue();
    assertThat(argument.getSubmissionMonth()).isEqualTo(YearMonth.now());
    assertThat(argument.getTimeSendMeter()).isNotNull().isBefore(OffsetDateTime.now());
    assertThat(argument.getMeter()).isNull();
  }

  @Test
  @DisplayName("Проверяет, что если при сохранении в репозитории выпадет ошибку, она обработается")
  void addMeterValue_SQLException() throws SQLException {
    var address = Address.builder().addressId(12L).build();
    var typeMeter = TypeMeter.builder().typeMeterId(5L).build();
    var meterReading = MeterReading.builder()
            .meterValue(123123.43)
            .typeMeter(typeMeter)
            .address(address)
            .build();
    var userSecurity = UserSecurity.builder().role(Roles.USER).userId(5L).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);
    when(this.addressService.getAddresses(any())).thenReturn(List.of(address));
    when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(typeMeter));
    when(this.readingRepository.save(any())).thenThrow(SQLException.class);

    assertThatThrownBy(() -> this.meterReadingService.addMeterValue(meterReading)).isInstanceOf(MeterReadingConflictError.class);

    var meterReadingCaptor = ArgumentCaptor.forClass(MeterReading.class);
    verify(this.readingRepository, times(1)).save(meterReadingCaptor.capture());
    var argument = meterReadingCaptor.getValue();
    assertThat(argument.getSubmissionMonth()).isEqualTo(YearMonth.now());
    assertThat(argument.getTimeSendMeter()).isNotNull().isBefore(OffsetDateTime.now());
    assertThat(argument.getMeter()).isNull();
  }

  @Test
  @DisplayName("Проверяет, что при подаче повторных показаний выпадает ошибка")
  void addMeterValue_repeatedReadings() throws SQLException {
    var address = Address.builder().addressId(12L).build();
    var typeMeter = TypeMeter.builder().typeMeterId(5L).build();
    var meterReading = MeterReading.builder()
            .meterValue(123123.43)
            .typeMeter(typeMeter)
            .address(address)
            .build();
    var meterReadingActual = MeterReading.builder()
            .submissionMonth(YearMonth.now())
            .build();
    when(this.readingRepository.findActualMeterReading(any(), any())).thenReturn(Optional.of(meterReadingActual));
    var userSecurity = UserSecurity.builder().role(Roles.USER).userId(5L).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);
    when(this.addressService.getAddresses(any())).thenReturn(List.of(address));
    when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(typeMeter));

    assertThatThrownBy(() -> this.meterReadingService.addMeterValue(meterReading)).isInstanceOf(MeterReadingConflictError.class);

    verify(this.readingRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Проверяет, что при подаче повторных показаний выпадает ошибка")
  void addMeterValue_noValidMeter() throws SQLException {
    var address = Address.builder().addressId(12L).build();
    var typeMeter = TypeMeter.builder().typeMeterId(5L).build();
    var meterReading = MeterReading.builder()
            .meterValue(123123.43)
            .typeMeter(typeMeter)
            .address(address)
            .build();
    var meterReadingActual = MeterReading.builder()
            .submissionMonth(YearMonth.now().minusMonths(1))
            .meterValue(21321412D)
            .build();
    when(this.readingRepository.findActualMeterReading(any(), any())).thenReturn(Optional.of(meterReadingActual));
    var userSecurity = UserSecurity.builder().role(Roles.USER).userId(5L).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);
    when(this.addressService.getAddresses(any())).thenReturn(List.of(address));
    when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(typeMeter));

    assertThatThrownBy(() -> this.meterReadingService.addMeterValue(meterReading)).isInstanceOf(MeterReadingConflictError.class);

    verify(this.readingRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Проверяет, что при попытке подать данные с роли админа, выпадает ошибка")
  void addMeterValue_admin() throws SQLException {
    var address = Address.builder().addressId(12L).build();
    var typeMeter = TypeMeter.builder().typeMeterId(5L).build();
    var meterReading = MeterReading.builder()
            .meterValue(123123.43)
            .typeMeter(typeMeter)
            .address(address)
            .build();
    var userSecurity = UserSecurity.builder().role(Roles.ADMIN).userId(5L).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);

    assertThatThrownBy(() -> this.meterReadingService.addMeterValue(meterReading)).isInstanceOf(AccessDeniedException.class);

    verify(this.readingRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Проверяет что при вызове метода с ролью АДМИН сервис обращается в репозиторий и возвращает показания")
  void getActualMeterByAddress_dateAndType() {
    var stringType = "test";
    var typeCode = "type2";
    var type = TypeMeter.builder().typeMeterId(2L).typeCode(stringType).build();
    var type2 = TypeMeter.builder().typeMeterId(3L).typeCode(typeCode).build();
    var date = YearMonth.now().minusMonths(1);
    var parameters = Parameters.builder()
            .typeMeterIds(Set.of(2L, 3L))
            .userId(1L)
            .addressId(2L)
            .date(date)
            .build();
    var userSecurity = UserSecurity.builder().role(Roles.ADMIN).userId(5L).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(type, type2));
    when(this.readingRepository.findMeterReadingForDate(any(), any(), any()))
            .thenReturn(Optional.of(MeterReading.builder().typeMeter(type).build()))
            .thenReturn(Optional.of(MeterReading.builder().typeMeter(type).build()));
    when(this.addressService.getAddresses(any())).thenReturn(List.of(Address.builder().addressId(2L).build()));

    this.meterReadingService.getActualMeterByAddress(parameters);

    verify(this.readingRepository, times(2)).findMeterReadingForDate(eq(2L), anyLong(), eq(date));
  }

  @Test
  @DisplayName("Проверяет что при вызове метода с ролью АДМИН, без передачи даты, сервис обращается в репозиторий и возвращает актуальные показания показания")
  void getActualMeterByAddress_Type() {
    var stringType = "test";
    var typeCode = "type2";
    var type = TypeMeter.builder().typeMeterId(2L).typeCode(stringType).build();
    var type2 = TypeMeter.builder().typeMeterId(3L).typeCode(typeCode).build();
    var date = YearMonth.now().minusMonths(1);
    var parameters = Parameters.builder()
            .typeMeterIds(Set.of(2L, 3L))
            .userId(1L)
            .addressId(2L)
            .build();
    var userSecurity = UserSecurity.builder().role(Roles.ADMIN).userId(5L).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(type, type2));
    when(this.readingRepository.findMeterReadingForDate(any(), any(), any()))
            .thenReturn(Optional.of(MeterReading.builder().typeMeter(type).build()))
            .thenReturn(Optional.of(MeterReading.builder().typeMeter(type).build()));
    when(this.addressService.getAddresses(any())).thenReturn(List.of(Address.builder().addressId(2L).build()));

    this.meterReadingService.getActualMeterByAddress(parameters);

    verify(this.readingRepository, times(2)).findActualMeterReading(eq(2L), anyLong());
  }

  @Test
  @DisplayName("Проверяет что при вызове метода с ролью АДМИН, без передачи типов, сервис обращается в репозиторий и возвращает показания для всех типов")
  void getActualMeterByAddress_date() {
    var stringType = "test";
    var typeCode = "type2";
    var type = TypeMeter.builder().typeMeterId(2L).typeCode(stringType).build();
    var type2 = TypeMeter.builder().typeMeterId(3L).typeCode(typeCode).build();
    var date = YearMonth.now().minusMonths(1);
    var parameters = Parameters.builder()
            .userId(1L)
            .addressId(2L)
            .date(date)
            .build();
    var userSecurity = UserSecurity.builder().role(Roles.ADMIN).userId(5L).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(type, type2));
    when(this.readingRepository.findMeterReadingForDate(any(), any(), any()))
            .thenReturn(Optional.of(MeterReading.builder().typeMeter(type).build()))
            .thenReturn(Optional.of(MeterReading.builder().typeMeter(type).build()));
    when(this.addressService.getAddresses(any())).thenReturn(List.of(Address.builder().addressId(2L).build()));

    this.meterReadingService.getActualMeterByAddress(parameters);

    verify(this.readingRepository, times(2)).findMeterReadingForDate(eq(2L), anyLong(), eq(date));
  }

  @Test
  @DisplayName("Проверяет что при вызове метода с ролью АДМИН, без передачи типов и даты, сервис обращается в репозиторий и возвращает актуальные показания для всех типов")
  void getActualMeterByAddress() {
    var stringType = "test";
    var typeCode = "type2";
    var type = TypeMeter.builder().typeMeterId(2L).typeCode(stringType).build();
    var type2 = TypeMeter.builder().typeMeterId(3L).typeCode(typeCode).build();
    var parameters = Parameters.builder()
            .userId(1L)
            .addressId(2L)
            .build();
    var userSecurity = UserSecurity.builder().role(Roles.ADMIN).userId(5L).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(type, type2));
    when(this.readingRepository.findMeterReadingForDate(any(), any(), any()))
            .thenReturn(Optional.of(MeterReading.builder().typeMeter(type).build()))
            .thenReturn(Optional.of(MeterReading.builder().typeMeter(type).build()));
    when(this.addressService.getAddresses(any())).thenReturn(List.of(Address.builder().addressId(2L).build()));

    this.meterReadingService.getActualMeterByAddress(parameters);

    verify(this.readingRepository, times(2)).findActualMeterReading(eq(2L), anyLong());
  }

  @Test
  @DisplayName("Проверяет что при вызове метода с ролью USER, без передачи типов и даты, сервис обращается в репозиторий и возвращает актуальные показания для всех типов")
  void getActualMeterByAddress_user() {
    var stringType = "test";
    var typeCode = "type2";
    var type = TypeMeter.builder().typeMeterId(2L).typeCode(stringType).build();
    var type2 = TypeMeter.builder().typeMeterId(3L).typeCode(typeCode).build();
    var parameters = Parameters.builder()
            .userId(1L)
            .addressId(2L)
            .build();
    var userSecurity = UserSecurity.builder().role(Roles.USER).userId(5L).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(type, type2));
    when(this.readingRepository.findMeterReadingForDate(any(), any(), any()))
            .thenReturn(Optional.of(MeterReading.builder().typeMeter(type).build()))
            .thenReturn(Optional.of(MeterReading.builder().typeMeter(type).build()));
    when(this.addressService.getAddresses(any())).thenReturn(List.of(Address.builder().addressId(2L).build()));

    this.meterReadingService.getActualMeterByAddress(parameters);

    verify(this.readingRepository, times(2)).findActualMeterReading(eq(2L), anyLong());
  }

  @Test
  @DisplayName("Проверяет что при вызове метода с ролью USER, без передачи типов и даты, сервис обращается в репозиторий и возвращает актуальные показания для всех типов")
  void getActualMeterByAddress_notAddress() {
    var stringType = "test";
    var typeCode = "type2";
    var type = TypeMeter.builder().typeMeterId(2L).typeCode(stringType).build();
    var type2 = TypeMeter.builder().typeMeterId(3L).typeCode(typeCode).build();
    var parameters = Parameters.builder()
            .userId(1L)
            .addressId(2L)
            .build();
    var userSecurity = UserSecurity.builder().role(Roles.USER).userId(5L).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(type, type2));
    when(this.readingRepository.findMeterReadingForDate(any(), any(), any()))
            .thenReturn(Optional.of(MeterReading.builder().typeMeter(type).build()))
            .thenReturn(Optional.of(MeterReading.builder().typeMeter(type).build()));
    when(this.addressService.getAddresses(any())).thenReturn(List.of());

    this.meterReadingService.getActualMeterByAddress(parameters);

    verify(this.readingRepository, times(0)).findActualMeterReading(any(), any());
  }

  @Test
  @DisplayName("Проверяет что при вызове метода с ролью USER, без передачи типов и даты, сервис обращается в репозиторий и возвращает актуальные показания для всех типов")
  void getActualMeterByAddress_notAuth() {
    var actualMeterByAddress = this.meterReadingService.getActualMeterByAddress(mock(Parameters.class));

    assertThat(actualMeterByAddress).isEmpty();
    verify(this.readingRepository, times(0)).findActualMeterReading(any(), any());
  }


  @Test
  @DisplayName("Проверяет работу фильтров")
  void getHistoryMeterByAddress() {
    var stringType = "test";
    var type1 = TypeMeter.builder().typeMeterId(1L).typeCode(stringType).build();
    var typeCode = "type2";
    var type2 = TypeMeter.builder().typeMeterId(2L).typeCode(typeCode).build();
    var type3 = TypeMeter.builder().typeMeterId(3L).typeCode("type3").build();

    var meter1 = MeterReading.builder()
            .submissionMonth(YearMonth.now().minusMonths(2))
            .typeMeter(type1).timeSendMeter(OffsetDateTime.now()).build();
    var meter2 = MeterReading.builder()
            .submissionMonth(YearMonth.now().minusMonths(5))
            .typeMeter(type1).timeSendMeter(OffsetDateTime.now()).build();
    var meter3 = MeterReading.builder()
            .submissionMonth(YearMonth.now())
            .typeMeter(type2).timeSendMeter(OffsetDateTime.now()).build();
    var meter4 = MeterReading.builder()
            .submissionMonth(YearMonth.now())
            .typeMeter(type3).timeSendMeter(OffsetDateTime.now()).build();
    var meterReadings = List.of(meter1, meter2, meter3, meter4);
    var parameters = Parameters.builder()
            .addressId(5L)
            .typeMeterIds(Set.of(1L, 2L))
            .startDate(YearMonth.now().minusMonths(4))
            .endDate(YearMonth.now().minusMonths(1))
            .build();
    var userSecurity = UserSecurity.builder().userId(1L).role(Roles.USER).build();
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);
    when(this.addressService.getAddresses(any())).thenReturn(List.of(Address.builder().addressId(5L).build()));

    when(this.readingRepository.findMeterReadingByAddressId(any())).thenReturn(meterReadings);
    when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(type1, type2, type3));

    var historyMeterByAddress
            = this.meterReadingService.getHistoryMeterByAddress(parameters);

    assertThat(historyMeterByAddress).contains(meter1).doesNotContain(meter2, meter3, meter4);
    verify(this.readingRepository, times(1)).findMeterReadingByAddressId(eq(5L));
  }
}