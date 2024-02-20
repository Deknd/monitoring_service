package com.denknd.mappers;

import com.denknd.dto.CounterInfoDto;
import com.denknd.dto.MeterDto;
import com.denknd.entity.Meter;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * Маппер для данных о счетчике.
 * Этот интерфейс предоставляет методы для преобразования объектов CounterInfoDto в Meter и Meter в MeterDto.
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface MeterCountMapper {
  MeterCountMapper INSTANCE = Mappers.getMapper(MeterCountMapper.class);

  /**
   * Маппер для маппинга CounterInfoDto в Meter.
   *
   * @param counterInfoDto информация о пользователе, полученная от пользователя
   * @return полный объект счетчика
   */
  Meter mapCounterInfoDtoToMeter(CounterInfoDto counterInfoDto);

  /**
   * Маппер для маппинга Meter в MeterDto.
   *
   * @param meter объект с информацией о счетчике
   * @return данные о счетчике для пользователя
   */
  MeterDto mapMeterToMeterDto(Meter meter);

}
