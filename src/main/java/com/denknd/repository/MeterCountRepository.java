package com.denknd.repository;

import com.denknd.entity.Meter;

import java.sql.SQLException;

/**
 * Интерфейс для хранения информации о счетчиках в БД.
 */
public interface MeterCountRepository {
  /**
   * Создает счетчик в системе
   *
   * @param meter Объект счетчика, который создается автоматически при подаче первых показаний
   * @return сохраненный объект с идентификатором
   * @throws SQLException ошибка при сохранении в БД
   */
  Meter save(Meter meter) throws SQLException;

  /**
   * Дополняет данные о счетчике
   *
   * @param meter объект счетчика с заполнеными данными
   * @return обновленный объект счетчика
   * @throws SQLException ошибка при сохранении в БД
   */
  Meter update(Meter meter) throws SQLException;
}
