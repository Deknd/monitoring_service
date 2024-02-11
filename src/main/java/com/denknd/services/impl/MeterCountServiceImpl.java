package com.denknd.services.impl;

import com.denknd.entity.Meter;
import com.denknd.repository.MeterCountRepository;
import com.denknd.services.MeterCountService;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.time.OffsetDateTime;

/**
 * Сервис для работы с информацие о счетчике
 */
@RequiredArgsConstructor
public class MeterCountServiceImpl implements MeterCountService {
  /**
   * Репозиторий для хранения информации о счетчике
   */
  private final MeterCountRepository meterCountRepository;

  /**
   * Сохраняет информацию о счетчике
   * @param meter информация о счетчике
   * @return сохраненый объект с информацией о счетчике с идентификатором
   * @throws SQLException ошибка при сохранении информации
   */
  @Override
  public Meter saveMeterCount(Meter meter) throws SQLException {
    meter.setRegistrationDate(OffsetDateTime.now());
    return meterCountRepository.save(meter);
  }

  /**
   * Добавляет дополнительную информацию о счетчике
   * @param meter объект с дополнительной информацией
   * @return возвращает обновленный объект
   * @throws SQLException ошибка при сохранении информации в бд
   */
  @Override
  public Meter addInfoForMeterCount(Meter meter) throws SQLException {
    return this.meterCountRepository.update(meter);
  }
}
