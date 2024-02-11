package com.denknd.mappers;

import com.denknd.dto.MeterReadingRequestDto;
import com.denknd.dto.MeterReadingResponseDto;
import com.denknd.entity.Address;
import com.denknd.entity.Meter;
import com.denknd.entity.MeterReading;
import com.denknd.entity.TypeMeter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MeterReadingMapperTest {
  private MeterReadingMapper meterReadingMapper;

  @BeforeEach
  void setUp() {
    this.meterReadingMapper = MeterReadingMapper.INSTANCE;
  }

  @Test
  @DisplayName("Проверяет, что правильно маппит MeterReadingRequestDto в MeterReading")
  void mapMeterReadingRequestDtoToMeterReading() {
    var meterReadingRequestDto = MeterReadingRequestDto.builder()
            .addressId(1L)
            .codeType(2L)
            .meterValue(12314.124)
            .build();
    var address = mock(Address.class);
    var typeMeter = mock(TypeMeter.class);

    var meterReading = this.meterReadingMapper.mapMeterReadingRequestDtoToMeterReading(
            meterReadingRequestDto, address, typeMeter);

    assertThat(meterReading).isNotNull();
    assertThat(meterReading.getMeterValue()).isEqualTo(meterReadingRequestDto.meterValue());
    assertThat(meterReading.getAddress()).isEqualTo(address);
    assertThat(meterReading.getTypeMeter()).isEqualTo(typeMeter);
    assertThat(meterReading.getMeterId()).isNull();
    assertThat(meterReading.getSubmissionMonth()).isNull();
    assertThat(meterReading.getMeter()).isNull();
    assertThat(meterReading.getTimeSendMeter()).isNull();

  }

  @Test
  @DisplayName("Проверяет, что правильно маппит MeterReadingRequestDto в MeterReading")
  void mapMeterReadingRequestDtoToMeterReading_null() {
    var meterReading = this.meterReadingMapper.mapMeterReadingRequestDtoToMeterReading(
            null, null, null);

    assertThat(meterReading).isNull();
  }

  @Test
  @DisplayName("Проверяет, что правильно маппит MeterReading в MeterReadingResponseDto")
  void mapMeterReadingToMeterReadingResponseDto() {
    var meterReading = MeterReading.builder()
            .meterId(1L)
            .address(Address.builder().addressId(5L).build())
            .typeMeter(TypeMeter.builder().typeDescription("description").build())
            .meterValue(123124.324)
            .submissionMonth(YearMonth.now())
            .meter(mock(Meter.class))
            .timeSendMeter(OffsetDateTime.now())
            .build();

    var meterReadingResponseDto = this.meterReadingMapper.mapMeterReadingToMeterReadingResponseDto(meterReading);

    assertThat(meterReadingResponseDto).isNotNull()
            .satisfies(result ->
            {
              assertThat(result.meterId()).isEqualTo(meterReading.getMeterId());
              assertThat(result.addressId()).isEqualTo(meterReading.getAddress().getAddressId());
              assertThat(result.typeDescription()).isEqualTo(meterReading.getTypeMeter().getTypeDescription());
              assertThat(result.meterValue()).isEqualTo(meterReading.getMeterValue());
              assertThat(result.metric()).isEqualTo(meterReading.getTypeMeter().getMetric());
              assertThat(result.code()).isEqualTo(meterReading.getTypeMeter().getTypeCode());
              assertThat(result.submissionMonth()).isEqualTo(meterReading.getSubmissionMonth());
              assertThat(result.timeSendMeter()).isEqualTo(meterReading.getTimeSendMeter());
            });
  }

  @Test
  @DisplayName("Проверяет, что если передать null вернет null")
  void mapMeterReadingToMeterReadingResponseDto_() {
    var meterReadingResponseDto = this.meterReadingMapper.mapMeterReadingToMeterReadingResponseDto(null);

    assertThat(meterReadingResponseDto).isNull();
  }

  @Test
  @DisplayName("Проверяет, что правильно маппит лист MeterReading в лист MeterReadingResponseDto")
  void mapMeterReadingsToMeterReadingResponsesDto() {
    var meterReading = MeterReading.builder()
            .meterId(1L)
            .address(Address.builder().addressId(5L).build())
            .typeMeter(TypeMeter.builder().typeDescription("description").build())
            .meterValue(123124.324)
            .submissionMonth(YearMonth.now())
            .meter(mock(Meter.class))
            .timeSendMeter(OffsetDateTime.now())
            .build();
    var meterReading2 = MeterReading.builder()
            .meterId(2L)
            .address(Address.builder().addressId(5L).build())
            .typeMeter(TypeMeter.builder().typeDescription("description").build())
            .meterValue(1231242.324)
            .submissionMonth(YearMonth.now())
            .meter(mock(Meter.class))
            .timeSendMeter(OffsetDateTime.now())
            .build();
    var meterReading3 = MeterReading.builder()
            .meterId(3L)
            .address(Address.builder().addressId(5L).build())
            .typeMeter(TypeMeter.builder().typeDescription("description").build())
            .meterValue(12312433.324)
            .submissionMonth(YearMonth.now())
            .meter(mock(Meter.class))
            .timeSendMeter(OffsetDateTime.now())
            .build();

    var meterReadingResponseDtos = this.meterReadingMapper.mapMeterReadingsToMeterReadingResponsesDto(
            List.of(
                    meterReading, meterReading2, meterReading3
            ));

    assertThat(meterReadingResponseDtos.size()).isEqualTo(3);
  }
  @Test
  @DisplayName("Проверяет, что если передать null, то вернется null")
  void mapMeterReadingsToMeterReadingResponsesDto_null() {
    var meterReadingResponseDtos = this.meterReadingMapper.mapMeterReadingsToMeterReadingResponsesDto(null);

    assertThat(meterReadingResponseDtos).isNull();
  }

  @Test
  @DisplayName("Проверяет, что маппится данные из ResultSet в MeterReading")
  void mapResultSetToMeterReading () throws SQLException {
    var resultSet = mock(ResultSet.class);
    when(resultSet.getLong(eq("meter_id"))).thenReturn(1L);
    when(resultSet.getLong(eq("address_id"))).thenReturn(1L);
    when(resultSet.getLong(eq("type_meter_id"))).thenReturn(1L);
    when(resultSet.getDouble("meter_value")).thenReturn(123.34);
    when(resultSet.getString("submission_month")).thenReturn("1999-11");
    var offsetDateTime = OffsetDateTime.parse("2024-02-07T15:30:45.123456+03:00");
    var timestamp = Timestamp.from(offsetDateTime.toInstant());
    when(resultSet.getTimestamp(eq("time_send_meter"))).thenReturn(timestamp);

    var meterReading = this.meterReadingMapper.mapResultSetToMeterReading(resultSet);

    assertThat(meterReading.getMeterId()).isEqualTo(1L);
    assertThat(meterReading.getAddress().getAddressId()).isEqualTo(1L);
    assertThat(meterReading.getTypeMeter().getTypeMeterId()).isEqualTo(1L);
    assertThat(meterReading.getMeterValue()).isEqualTo(123.34);
    assertThat(meterReading.getSubmissionMonth()).isNotNull();
    assertThat(meterReading.getTimeSendMeter()).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, что при отсутствие столбца выпадает ошибка")
  void mapResultSetToMeterReading_SQLException () throws SQLException {
    var resultSet = mock(ResultSet.class);
    when(resultSet.getLong(eq("meter_id"))).thenReturn(1L);
    when(resultSet.getLong(eq("address_id"))).thenReturn(1L);
    when(resultSet.getLong(eq("type_meter_id"))).thenThrow(SQLException.class);
    when(resultSet.getDouble("meter_value")).thenReturn(123.34);
    when(resultSet.getString("submission_month")).thenReturn("1999-11");
    var offsetDateTime = OffsetDateTime.parse("2024-02-07T15:30:45.123456+03:00");
    var timestamp = Timestamp.from(offsetDateTime.toInstant());
    when(resultSet.getTimestamp(eq("time_send_meter"))).thenReturn(timestamp);

   assertThatThrownBy(()->this.meterReadingMapper.mapResultSetToMeterReading(resultSet)).isInstanceOf(SQLException.class);
  }
}