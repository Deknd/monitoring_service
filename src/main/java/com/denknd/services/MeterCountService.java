package com.denknd.services;

import com.denknd.entity.Meter;

import java.sql.SQLException;

/**
 * Интерфейс сервиса, по управлению информацией о счетчике
 */
public interface MeterCountService {
  /**
   * Метод для сохранения счетчика в БД
   * @param meter заполненный объект счетчика
   * @return возвращает сохраненный объект с идентификатором
   * @throws SQLException ошибка при сохранении в БД
   */
  Meter saveMeterCount(Meter meter) throws SQLException;

  /**
   * Добавляет информацию к существующим счетчикам
   * @param meter заполненный объект счетчика, с дополнительной информацией
   * @return возвращает заполненный объект
   * @throws SQLException ошибка сохранения в БД
   */
  Meter addInfoForMeterCount(Meter meter) throws SQLException;
}
