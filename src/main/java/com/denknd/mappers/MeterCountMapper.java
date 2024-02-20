package com.denknd.mappers;

import com.denknd.dto.CounterInfoDto;
import com.denknd.dto.MeterDto;
import com.denknd.entity.Meter;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * Маппер для данных о счетчике
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MeterCountMapper {
  MeterCountMapper INSTANCE = Mappers.getMapper(MeterCountMapper.class);

  /**
   * Маппер для маппинга {@link CounterInfoDto} в {@link Meter}
   * @param counterInfoDto информация о пользователе, полученная от пользователя
   * @return возвращает полный объект счетчика
   */
  Meter mapCounterInfoDtoToMeter(CounterInfoDto counterInfoDto);

  /**
   * Маппер для маппинга {@link Meter} в {@link MeterDto}
   * @param meter объект с информацией о счетчике
   * @return возвращает данные о счетчике для пользователя
   */
  MeterDto mapMeterToMeterDto(Meter meter);

}
