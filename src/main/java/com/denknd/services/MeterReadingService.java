package com.denknd.services;

import com.denknd.entity.MeterReading;
import com.denknd.entity.TypeMeter;
import com.denknd.exception.MeterReadingConflictError;

import java.time.YearMonth;
import java.util.List;
import java.util.Set;

/**
 * Интерфейс сервиса для работы с показаниями.
 */
public interface MeterReadingService {
  /**
   * Сохраняет новые показания.
   *
   * @param meterReading Собранный объект показаний без идентификатора.
   * @return Собранный объект показаний с идентификатором.
   * @throws MeterReadingConflictError Ошибка при сохранении, если показания уже внесены или меньше предыдущих.
   */
  MeterReading addMeterValue(MeterReading meterReading) throws MeterReadingConflictError;

  /**
   * Получает актуальные данные по всем переданным типам.
   * Если указана дата, то возвращает все показания по этим типам за эту дату.
   *
   * @param addressIds Идентификаторы адресов, по которым нужны показания (notNull).
   * @param typeCode    Типы показаний, по которым нужны показания.
   *                    Если null, то возвращает по всем типам.
   * @param date        Месяц и год, в котором нужны показания.
   *                    Если null, то возвращает последние показания по выбранным типам.
   * @return Список доступных показаний.
   */
  List<MeterReading> getActualMeterByAddress(
          Set<Long> addressIds,
          Set<TypeMeter> typeCode,
          YearMonth date);

  /**
   * История показаний по переданному адресу.
   *
   * @param addressIds Идентификаторы адресов, по которым нужна история показаний (notNull).
   * @param typeCode   Типы показаний, по которым нужны показания.
   *                   Если null, то возвращает по всем типам.
   * @param startDate  Дата от которой нужны показания.
   *                   Если null, то возвращает без ограничения.
   * @param endDate    Дата, до которой нужны показания.
   *                   Если null, возвращает по текущую дату.
   * @return История всех выбранных показаний, по указанному адресу, со всеми ограничениями, если они указаны.
   */
  List<MeterReading> getHistoryMeterByAddress(
          Set<Long> addressIds,
          Set<String> typeCode,
          YearMonth startDate,
          YearMonth endDate);
}
