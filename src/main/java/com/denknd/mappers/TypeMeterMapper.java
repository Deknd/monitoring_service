package com.denknd.mappers;

import com.denknd.dto.TypeMeterDto;
import com.denknd.entity.TypeMeter;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Маппер для преобразования типов показаний.
 * Этот интерфейс предоставляет методы для преобразования объектов TypeMeterDto в TypeMeter и обратно,
 * а также из ResultSet в объекты TypeMeter при работе с базой данных.
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TypeMeterMapper {

  TypeMeterMapper INSTANCE = Mappers.getMapper(TypeMeterMapper.class);

  /**
   * Преобразование TypeMeterDto в TypeMeter.
   *
   * @param typeMeterDto тип показаний от пользователя
   * @return тип показаний для сервиса
   */
  TypeMeter mapTypeMeterDtoToTypeMeter(TypeMeterDto typeMeterDto);

  /**
   * Преобразование TypeMeter в TypeMeterDto.
   *
   * @param typeMeter показания от сервиса
   * @return показания для пользователя
   */
  TypeMeterDto typeMeterToTypeMeterDto(TypeMeter typeMeter);

  /**
   * Преобразование списка TypeMeter в список TypeMeterDto.
   *
   * @param typeMeterList список показаний от сервиса
   * @return список показаний для пользователя
   */
  List<TypeMeterDto> typeMetersToTypeMetersDto(List<TypeMeter> typeMeterList);

  /**
   * Создает объект TypeMeter.
   *
   * @param resultSet данные из базы данных
   * @return заполненный объект
   * @throws SQLException если данные из БД некорректные
   */
  default TypeMeter mapResultSetToTypeMeter(ResultSet resultSet) throws SQLException {
    return TypeMeter.builder()
            .typeMeterId(resultSet.getLong("type_meter_id"))
            .typeCode(resultSet.getString("type_code"))
            .typeDescription(resultSet.getString("type_description"))
            .metric(resultSet.getString("metric"))
            .build();
  }
}
