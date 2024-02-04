package com.denknd.services.impl;

import com.denknd.entity.Address;
import com.denknd.entity.MeterReading;
import com.denknd.entity.TypeMeter;
import com.denknd.exception.MeterReadingConflictError;
import com.denknd.repository.MeterReadingRepository;
import com.denknd.services.TypeMeterService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MeterReadingServiceImplTest {
    @Mock
    private MeterReadingRepository readingRepository;
    private MeterReadingServiceImpl meterReadingService;
    @Mock
    private TypeMeterService typeMeterService;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        this.closeable = MockitoAnnotations.openMocks(this);
        this.meterReadingService = new MeterReadingServiceImpl(this.readingRepository, this.typeMeterService);
    }
    @AfterEach
    void tearDown() throws Exception {
        this.closeable.close();
    }

    @Test
    @DisplayName("Проверяет, что вызывается репозиторий с ожидаемым аргументом")
    void addMeterValue() throws MeterReadingConflictError, SQLException {
        var address = Address.builder().addressId(12L).build();
        var typeMeter = TypeMeter.builder().build();
        var meterReading = MeterReading.builder()
                .meterValue(123123.43)
                .typeMeter(typeMeter)
                .address(address)
                .build();

        this.meterReadingService.addMeterValue(meterReading);

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
        var typeMeter = TypeMeter.builder().build();
        var meterReading = MeterReading.builder()
                .meterValue(123123.43)
                .typeMeter(typeMeter)
                .address(address)
                .build();
        var meterReadingActual = MeterReading.builder()
                .submissionMonth(YearMonth.now())
                .build();
        when(this.readingRepository.findActualMeterReading(any(), any())).thenReturn(Optional.of(meterReadingActual));

        assertThatThrownBy(()->this.meterReadingService.addMeterValue(meterReading)).isInstanceOf(MeterReadingConflictError.class);

        verify(this.readingRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Проверяет, что при подаче повторных показаний выпадает ошибка")
    void addMeterValue_noValidMeter() throws SQLException {
        var address = Address.builder().addressId(12L).build();
        var typeMeter = TypeMeter.builder().build();
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

        assertThatThrownBy(()->this.meterReadingService.addMeterValue(meterReading)).isInstanceOf(MeterReadingConflictError.class);

        verify(this.readingRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Проверяет что сервис обращается в репозиторий с ожидаемыми запросами, с параметрами типов и даты")
    void getActualMeterByAddress_dateAndType() {
        var stringType = "test";
        var typeCode = "type2";
        var type = TypeMeter.builder().typeMeterId(2L).typeCode(stringType).build();
        var type2 = TypeMeter.builder().typeMeterId(3L).typeCode(typeCode).build();
        var date = YearMonth.now().minusMonths(1);
        when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(type, type2));

        this.meterReadingService.getActualMeterByAddress(Set.of(1L), Set.of(type, type2), date);

        verify(this.readingRepository, times(2)).findMeterReadingForDate(eq(1L), anyLong(), eq(date));

    }

    @Test
    @DisplayName("Проверяет что вызывается репозиторий, со всеми актуальными значениями, без передачи определенного типа показаний")
    void getActualMeterByAddress_dateAndNotType() {
        var stringType = "test";
        var typeCode = "type2";
        var type = TypeMeter.builder().typeMeterId(2L).typeCode(stringType).build();
        var type2 = TypeMeter.builder().typeMeterId(3L).typeCode(typeCode).build();
        var date = YearMonth.now().minusMonths(1);
        when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(type, type2));

       this.meterReadingService.getActualMeterByAddress(Set.of(1L), null, date);

        verify(this.readingRepository, times(2)).findMeterReadingForDate(eq(1L), anyLong(), eq(date));

    }
    @Test
    @DisplayName("Проверяет что сервис обращается в репозиторий с ожидаемыми запросами, с типами , но без даты")
    void getActualMeterByAddress_TypeAndNotDate() {
        var stringType = "test";
        var typeCode = "type2";
        var type = TypeMeter.builder().typeMeterId(2L).typeCode(stringType).build();
        var type2 = TypeMeter.builder().typeMeterId(3L).typeCode(typeCode).build();
        when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(type, type2));

        this.meterReadingService.getActualMeterByAddress(Set.of(1L), Set.of(type, type2), null);

        verify(this.readingRepository, times(2)).findActualMeterReading(eq(1L), anyLong());

    }

    @Test
    @DisplayName("Проверяет что вызывается репозиторий, со всеми актуальными значениями, без передачи даты и определенного типа показаний")
    void getActualMeterByAddress_notDateAndNotType() {
        var stringType = "test";
        var typeCode = "type2";
        var type = TypeMeter.builder().typeMeterId(2L).typeCode(stringType).build();
        var type2 = TypeMeter.builder().typeMeterId(3L).typeCode(typeCode).build();
        when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(type, type2));

        this.meterReadingService.getActualMeterByAddress(Set.of(1L), null, null);

        verify(this.readingRepository, times(2)).findActualMeterReading(eq(1L), anyLong());

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
        when(this.readingRepository.findMeterReadingByAddressId(any())).thenReturn(meterReadings);
        when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(type1, type2, type3));

        var historyMeterByAddress = this.meterReadingService.getHistoryMeterByAddress(Set.of(1L), Set.of(stringType, typeCode), YearMonth.now().minusMonths(4), YearMonth.now().minusMonths(1));

        assertThat(historyMeterByAddress).contains(meter1).doesNotContain(meter2, meter3, meter4);
    }
}