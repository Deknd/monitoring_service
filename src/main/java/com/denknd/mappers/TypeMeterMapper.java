package com.denknd.mappers;

import com.denknd.dto.TypeMeterDto;
import com.denknd.entity.TypeMeter;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Маппер для типов показаний
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TypeMeterMapper {

  TypeMeterMapper INSTANCE = Mappers.getMapper(TypeMeterMapper.class);

  /**
   * Преобразование TypeMeterDto в TypeMeter
   * @param typeMeterDto тип показаний от пользователя
   * @return тип показаний для сервиса
   */
  TypeMeter mapTypeMeterDtoToTypeMeter(TypeMeterDto typeMeterDto);

  /**
   * Преобразование TypeMeter в TypeMeterDto
   * @param typeMeter показания от сервиса
   * @return показания для пользователя
   */
  TypeMeterDto typeMeterToTypeMeterDto(TypeMeter typeMeter);

  /**
   * Преобразование списка TypeMeter в список TypeMeterDto
   * @param typeMeterList показания от сервиса
   * @return показания для пользователя
   */
  List<TypeMeterDto> typeMetersToTypeMetersDto(List<TypeMeter> typeMeterList);
}
