package com.denknd.mappers;

import com.denknd.dto.CounterInfoDto;
import com.denknd.dto.MeterDto;
import com.denknd.entity.Meter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MeterCountMapperTest {
  private MeterCountMapper meterCountMapper;

  @BeforeEach
  void setUp() {
    this.meterCountMapper = MeterCountMapper.INSTANCE;
  }

  @Test
  @DisplayName("Проверяет, что правильно маппит CounterInfoDto в Meter")
  void mapCounterInfoDtoToMeter() {
    var counterInfoDto = CounterInfoDto.builder()
            .addressId(1L)
            .typeMeterId(1L)
            .serialNumber("serialNumber")
            .meterModel("meterModel")
            .build();

    var meter = this.meterCountMapper.mapCounterInfoDtoToMeter(counterInfoDto);

    assertThat(meter.getMeterCountId()).isNull();
    assertThat(meter.getAddressId()).isEqualTo(counterInfoDto.addressId());
    assertThat(meter.getTypeMeterId()).isEqualTo(counterInfoDto.typeMeterId());
    assertThat(meter.getSerialNumber()).isEqualTo(counterInfoDto.serialNumber());
    assertThat(meter.getRegistrationDate()).isNull();
    assertThat(meter.getLastCheckDate()).isNull();
    assertThat(meter.getMeterModel()).isEqualTo(counterInfoDto.meterModel());

  }

  @Test
  void mapMeterToMeterDto() {
    var meter = Meter.builder()
            .meterCountId(1L)
            .addressId(2L)
            .typeMeterId(3L)
            .serialNumber("serialNumber")
            .registrationDate(OffsetDateTime.now().minus(2, ChronoUnit.MONTHS))
            .lastCheckDate(OffsetDateTime.now())
            .meterModel("meterModel")
            .build();

    var meterDto = this.meterCountMapper.mapMeterToMeterDto(meter);

    assertThat(meterDto).hasNoNullFieldsOrProperties();
  }
}