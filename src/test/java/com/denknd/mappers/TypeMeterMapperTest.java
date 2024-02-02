package com.denknd.mappers;

import com.denknd.dto.TypeMeterDto;
import com.denknd.entity.TypeMeter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TypeMeterMapperTest {

  private TypeMeterMapper typeMeterMapper;
  @BeforeEach
  void setUp() {
    this.typeMeterMapper = TypeMeterMapper.INSTANCE;
  }

  @Test
  @DisplayName("Проверяет, что правильно маппит TypeMeterDto в TypeMeter")
  void mapTypeMeterDtoToTypeMeter() {
    var typeMeterDto = TypeMeterDto.builder()
            .typeCode("code")
            .typeDescription("description")
            .metric("metric")
            .build();

    var typeMeter = this.typeMeterMapper.mapTypeMeterDtoToTypeMeter(typeMeterDto);

    assertThat(typeMeter).isNotNull();
    assertThat(typeMeter.getTypeMeterId()).isNull();
    assertThat(typeMeter.getTypeCode()).isEqualTo(typeMeterDto.typeCode());
    assertThat(typeMeter.getTypeDescription()).isEqualTo(typeMeterDto.typeDescription());
    assertThat(typeMeter.getMetric()).isEqualTo(typeMeterDto.metric());
  }
  @Test
  @DisplayName("Проверяет, что если отправить null, то вернется null")
  void mapTypeMeterDtoToTypeMeter_null() {
    var typeMeter = this.typeMeterMapper.mapTypeMeterDtoToTypeMeter(null);

    assertThat(typeMeter).isNull();
  }

  @Test
  @DisplayName("Проверяет, что правильно маппит TypeMeter в TypeMeterDto")
  void typeMeterToTypeMeterDto() {
    var typeMeter = TypeMeter.builder()
            .typeMeterId(1L)
            .typeCode("typeCode")
            .typeDescription("typeDescription")
            .metric("metric")
            .build();

    var typeMeterDto = this.typeMeterMapper.typeMeterToTypeMeterDto(typeMeter);

    assertThat(typeMeterDto).isNotNull();
    assertThat(typeMeterDto.typeCode()).isEqualTo(typeMeter.getTypeCode());
    assertThat(typeMeterDto.typeDescription()).isEqualTo(typeMeter.getTypeDescription());
    assertThat(typeMeterDto.metric()).isEqualTo(typeMeter.getMetric());
  }

  @Test
  @DisplayName("Проверяет, что если отправить null, то вернется null")
  void typeMeterToTypeMeterDto_null() {
    var typeMeterDto = this.typeMeterMapper.typeMeterToTypeMeterDto(null);

    assertThat(typeMeterDto).isNull();
  }

  @Test
  @DisplayName("Проверяет, что правильно маппит лист TypeMeter в лист TypeMeterDto")
  void typeMetersToTypeMetersDto() {
    var typeMeter = TypeMeter.builder()
            .typeMeterId(1L)
            .typeCode("typeCode")
            .typeDescription("typeDescription")
            .metric("metric")
            .build();
    var typeMeter2 = TypeMeter.builder()
            .typeMeterId(2L)
            .typeCode("typeCode2")
            .typeDescription("typeDescription2")
            .metric("metric2")
            .build();
    var typeMeter3 = TypeMeter.builder()
            .typeMeterId(3L)
            .typeCode("typeCode3")
            .typeDescription("typeDescription3")
            .metric("metric3")
            .build();

    var typeMeterDtos = this.typeMeterMapper.typeMetersToTypeMetersDto(
            List.of(typeMeter, typeMeter2, typeMeter3));

    assertThat(typeMeterDtos.size()).isEqualTo(3);
  }

  @Test
  @DisplayName("Проверяет, что если отправить null, то вернется null")
  void typeMetersToTypeMetersDto_null() {
    var typeMeterDtos = this.typeMeterMapper.typeMetersToTypeMetersDto(null);

    assertThat(typeMeterDtos).isNull();
  }
}