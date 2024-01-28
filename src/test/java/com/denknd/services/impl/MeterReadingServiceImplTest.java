package com.denknd.services.impl;

import com.denknd.entity.MeterReading;
import com.denknd.entity.TypeMeter;
import com.denknd.port.MeterReadingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MeterReadingServiceImplTest {

    private MeterReadingRepository readingRepository;
    private MeterReadingServiceImpl meterReadingService;

    @BeforeEach
    void setUp() {
        this.readingRepository = mock(MeterReadingRepository.class);
        this.meterReadingService = new MeterReadingServiceImpl(this.readingRepository);
    }

    @Test
    @DisplayName("Проверяет, что достает актуальное показание счетчиков")
    void getActualMeter() {
        var stringType = "test";
        var type1 = TypeMeter.builder().typeCode(stringType).build();
        var type2 = TypeMeter.builder().typeCode("type2").build();
        var type3 = TypeMeter.builder().typeCode("type3").build();

        var meter1 = MeterReading.builder()
                .submissionMonth(YearMonth.now())
                .typeMeter(type1).build();
        var meter2 = MeterReading.builder()
                .submissionMonth(YearMonth.now().minusMonths(1))
                .typeMeter(type1).build();
        var meter3 = MeterReading.builder()
                .submissionMonth(YearMonth.now())
                .typeMeter(type2).build();
        var meter4 = MeterReading.builder()
                .submissionMonth(YearMonth.now())
                .typeMeter(type3).build();
        var  meterReadings = List.of(meter1, meter2, meter3, meter4);
        when(this.readingRepository.findMeterReadingByAddressId(any())).thenReturn(meterReadings);

        var actualMeter = this.meterReadingService.getActualMeter(1L, stringType);

        assertThat(actualMeter).isEqualTo(meter1);
    }
    @Test
    @DisplayName("Проверяет, что если нет показаний по данному типу, вернет нулл")
    void getActualMeter_noType() {
        var stringType = "test";
        var type1 = TypeMeter.builder().typeCode(stringType).build();
        var type2 = TypeMeter.builder().typeCode("type2").build();
        var type3 = TypeMeter.builder().typeCode("type3").build();

        var meter1 = MeterReading.builder()
                .submissionMonth(YearMonth.now())
                .typeMeter(type1).build();
        var meter2 = MeterReading.builder()
                .submissionMonth(YearMonth.now().minusMonths(1))
                .typeMeter(type1).build();
        var meter3 = MeterReading.builder()
                .submissionMonth(YearMonth.now())
                .typeMeter(type2).build();
        var meter4 = MeterReading.builder()
                .submissionMonth(YearMonth.now())
                .typeMeter(type3).build();
        var  meterReadings = List.of(meter1, meter2, meter3, meter4);
        when(this.readingRepository.findMeterReadingByAddressId(any())).thenReturn(meterReadings);

        var actualMeter = this.meterReadingService.getActualMeter(1L, "fsdfs");

        assertThat(actualMeter).isNull();
    }

    @Test
    @DisplayName("Проверяет, что вызывается репозиторий с ожидаемым аргументом")
    void addMeterValue() {
        var meterReading = mock(MeterReading.class);

        this.meterReadingService.addMeterValue(meterReading);

        verify(this.readingRepository, times(1)).save(eq(meterReading));
    }

    @Test
    @DisplayName("Проверяет фильтры с передачей даты данного метода и вызов репозитория")
    void getActualMeterByAddress() {
        var stringType = "test";
        var type1 = TypeMeter.builder().typeCode(stringType).build();
        var typeCode = "type2";
        var type2 = TypeMeter.builder().typeCode(typeCode).build();
        var type3 = TypeMeter.builder().typeCode("type3").build();

        var meter1 = MeterReading.builder()
                .submissionMonth(YearMonth.now())
                .typeMeter(type1).timeSendMeter(OffsetDateTime.now()).build();
        var meter2 = MeterReading.builder()
                .submissionMonth(YearMonth.now().minusMonths(1))
                .typeMeter(type1).timeSendMeter(OffsetDateTime.now()).build();
        var meter3 = MeterReading.builder()
                .submissionMonth(YearMonth.now())
                .typeMeter(type2).timeSendMeter(OffsetDateTime.now()).build();
        var meter4 = MeterReading.builder()
                .submissionMonth(YearMonth.now())
                .typeMeter(type3).timeSendMeter(OffsetDateTime.now()).build();
        var  meterReadings = List.of(meter1, meter2, meter3, meter4);
        when(this.readingRepository.findMeterReadingByAddressId(any())).thenReturn(meterReadings);

        var actualMeterByAddress = this.meterReadingService.getActualMeterByAddress(1L, Set.of(stringType, typeCode), YearMonth.now().minusMonths(1));

        verify(this.readingRepository, times(1)).findMeterReadingByAddressId(any());
        assertThat(actualMeterByAddress).contains(meter2).doesNotContain(meter1, meter3, meter4);


    }
    @Test
    @DisplayName("Проверяет все фильтры без передачи даты данного метода и вызов репозитория")
    void getActualMeterByAddress_actual() {
        var stringType = "test";
        var type1 = TypeMeter.builder().typeCode(stringType).build();
        var typeCode = "type2";
        var type2 = TypeMeter.builder().typeCode(typeCode).build();
        var type3 = TypeMeter.builder().typeCode("type3").build();

        var meter1 = MeterReading.builder()
                .submissionMonth(YearMonth.now())
                .typeMeter(type1).timeSendMeter(OffsetDateTime.now()).build();
        var meter2 = MeterReading.builder()
                .submissionMonth(YearMonth.now().minusMonths(1))
                .typeMeter(type1).timeSendMeter(OffsetDateTime.now()).build();
        var meter3 = MeterReading.builder()
                .submissionMonth(YearMonth.now())
                .typeMeter(type2).timeSendMeter(OffsetDateTime.now()).build();
        var meter4 = MeterReading.builder()
                .submissionMonth(YearMonth.now())
                .typeMeter(type3).timeSendMeter(OffsetDateTime.now()).build();
        var  meterReadings = List.of(meter1, meter2, meter3, meter4);
        when(this.readingRepository.findMeterReadingByAddressId(any())).thenReturn(meterReadings);

        var actualMeterByAddress = this.meterReadingService.getActualMeterByAddress(1L, Set.of(stringType, typeCode), null);

        verify(this.readingRepository, times(1)).findMeterReadingByAddressId(any());
        assertThat(actualMeterByAddress).contains(meter1, meter3).doesNotContain(meter2, meter4);


    }

    @Test
    @DisplayName("Проверяет работу фильтров")
    void getHistoryMeterByAddress() {
        var stringType = "test";
        var type1 = TypeMeter.builder().typeCode(stringType).build();
        var typeCode = "type2";
        var type2 = TypeMeter.builder().typeCode(typeCode).build();
        var type3 = TypeMeter.builder().typeCode("type3").build();

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
        var  meterReadings = List.of(meter1, meter2, meter3, meter4);
        when(this.readingRepository.findMeterReadingByAddressId(any())).thenReturn(meterReadings);

        var historyMeterByAddress = this.meterReadingService.getHistoryMeterByAddress(1L, Set.of(stringType, typeCode), YearMonth.now().minusMonths(4), YearMonth.now().minusMonths(1));

        assertThat(historyMeterByAddress).contains(meter1).doesNotContain(meter2, meter3, meter4);
    }
}