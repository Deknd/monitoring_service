package com.denknd.services;

import com.denknd.entity.Meter;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;


/**
 * Интерфейс сервиса для управления информацией о счетчике.
 */
public interface MeterCountService {
  /**
   * Метод для сохранения счетчика в базе данных.
   *
   * @param meter Заполненный объект счетчика.
   * @return Сохраненный объект счетчика с присвоенным идентификатором.
   * @throws SQLException Ошибка при сохранении в базе данных.
   */
  Meter saveMeterCount(Meter meter) throws SQLException;

  /**
   * Добавляет информацию к существующим счетчикам.
   *
   * @param meter Заполненный объект счетчика с дополнительной информацией.
   * @return Заполненный объект счетчика.
   * @throws SQLException          Ошибка сохранения в базе данных.
   * @throws AccessDeniedException Исключение, выбрасываемое в случае отсутствия доступа для добавления информации о счетчике.
   */
  Meter addInfoForMeterCount(Meter meter) throws SQLException, AccessDeniedException;

}
