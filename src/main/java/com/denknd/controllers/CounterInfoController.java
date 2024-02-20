package com.denknd.controllers;

import com.denknd.aspectj.audit.AuditRecording;
import com.denknd.dto.CounterInfoDto;
import com.denknd.dto.MeterDto;
import com.denknd.mappers.MeterCountMapper;
import com.denknd.services.MeterCountService;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

/**
 * Контроллер по управлению информацией о счетчиках
 */
@RequiredArgsConstructor
public class CounterInfoController {
  /**
   * Сервис для работы с информацией по счетчикам
   */
  private final MeterCountService meterCountService;
  /**
   * Маппер для преобразования объекта {@link com.denknd.entity.Meter}
   */
  private final MeterCountMapper meterCountMapper;

  /**
   * Метод для добавления информации о счетчике
   *
   * @param counterInfoDto информация для обновления информации о счетчиках
   * @return возвращает информацию о счетчике, в случае успешного обновления
   * @throws SQLException ошибка при возникновении
   */
  @AuditRecording("Добавляет дополнительную информацию о счетчиках")
  public MeterDto addInfoForMeter(CounterInfoDto counterInfoDto) throws SQLException {
    var meter = this.meterCountMapper.mapCounterInfoDtoToMeter(counterInfoDto);
    var result = this.meterCountService.addInfoForMeterCount(meter);
    return this.meterCountMapper.mapMeterToMeterDto(result);
  }
}
