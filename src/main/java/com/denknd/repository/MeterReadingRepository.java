package com.denknd.repository;

import com.denknd.entity.MeterReading;

import java.sql.SQLException;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для работы с показаниями счетчика.
 */
public interface MeterReadingRepository {
  /**
   * Сохраняет показания счетчика в репозиторий.
   *
   * @param meterReading Полностью заполненный объект показаний без идентификатора.
   * @return Полностью заполненный объект показаний с присвоенным идентификатором.
   */
  MeterReading save(MeterReading meterReading) throws SQLException;

  /**
   * Ищет все доступные показания по указанному адресу.
   *
   * @param addressId Идентификатор адреса.
   * @return Список доступных показаний на указанном адресе.
   */
  List<MeterReading> findMeterReadingByAddressId(Long addressId);

  /**
   * Достает актуальные показания на данный момент по данному адресу с указанным типом.
   *
   * @param addressId   Идентификатор адреса.
   * @param typeMeterId Идентификатор типа показаний.
   * @return Optional с показаниями, актуальными на текущий момент,
   * или пустой Optional, если нет показаний.
   */
  Optional<MeterReading> findActualMeterReading(Long addressId, Long typeMeterId);

  /**
   * Достает показания по указанной дате на данный адрес и тип показаний.
   *
   * @param addressId   Идентификатор адреса.
   * @param typeMeterId Идентификатор типа показаний.
   * @param date        Месяц и год, для которых нужны показания.
   * @return Optional с показаниями на указанную дату и тип,
   * или пустой Optional, если нет показаний.
   */
  Optional<MeterReading> findMeterReadingForDate(Long addressId, Long typeMeterId, YearMonth date);

}
