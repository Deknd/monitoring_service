package com.denknd.mappers;

import com.denknd.dto.MeterReadingRequestDto;
import com.denknd.dto.MeterReadingResponseDto;
import com.denknd.entity.Address;
import com.denknd.entity.MeterReading;
import com.denknd.entity.TypeMeter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Маппер для работы с показаниями счетчиков.
 * Этот интерфейс предоставляет методы для преобразования объектов MeterReadingRequestDto в MeterReading
 * с указанием адреса и типа счетчика, а также для преобразования MeterReading в MeterReadingResponseDto.
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface MeterReadingMapper {
  MeterReadingMapper INSTANCE = Mappers.getMapper(MeterReadingMapper.class);

  /**
   * Преобразование MeterReadingRequestDto в MeterReading с указанием адреса и типа счетчика.
   *
   * @param meterReadingRequestDto показания, полученные от пользователя
   * @param address                адрес, на который производятся показания
   * @param typeMeter              тип счетчика
   * @return показания для сервиса
   */
  MeterReading mapMeterReadingRequestDtoToMeterReading(MeterReadingRequestDto meterReadingRequestDto, Address address, TypeMeter typeMeter);

  /**
   * Преобразование MeterReading в MeterReadingResponseDto.
   *
   * @param meterReading показания, полученные от сервиса
   * @return показания для пользователя
   */
  @Mapping(target = "addressId", source = "meterReading.address.addressId")
  @Mapping(target = "typeDescription", source = "meterReading.typeMeter.typeDescription")
  @Mapping(target = "metric", source = "meterReading.typeMeter.metric")
  @Mapping(target = "code", source = "meterReading.typeMeter.typeCode")
  MeterReadingResponseDto mapMeterReadingToMeterReadingResponseDto(MeterReading meterReading);

  /**
   * Преобразование списка MeterReading в список MeterReadingResponseDto.
   *
   * @param meterReadingList список показаний, полученных от сервиса
   * @return список показаний для пользователя
   */
  List<MeterReadingResponseDto> mapMeterReadingsToMeterReadingResponsesDto(List<MeterReading> meterReadingList);

  /**
   * Преобразует ResultSet из базы данных в MeterReading.
   *
   * @param resultSet сет, полученный от JDBC из базы данных
   * @return созданный по данным из базы данных объект MeterReading
   * @throws SQLException выбрасывается, если нет таких столбцов в БД
   */
  default MeterReading mapResultSetToMeterReading(ResultSet resultSet) throws SQLException {
    return MeterReading.builder()
            .meterId(resultSet.getLong("meter_id"))
            .address(Address.builder().addressId(resultSet.getLong("address_id")).build())
            .typeMeter(TypeMeter.builder().typeMeterId(resultSet.getLong("type_meter_id")).build())
            .meterValue(resultSet.getDouble("meter_value"))
            .submissionMonth(YearMonth.parse(resultSet.getString("submission_month"), DateTimeFormatter.ofPattern("yyyy-MM")))
            .timeSendMeter(OffsetDateTime.ofInstant(resultSet.getTimestamp("time_send_meter").toInstant(), ZoneId.systemDefault()))
            .build();
  }
}
