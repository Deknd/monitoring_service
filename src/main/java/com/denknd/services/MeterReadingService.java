package com.denknd.services;

import com.denknd.entity.MeterReading;
import com.denknd.entity.Parameters;
import com.denknd.exception.MeterReadingConflictError;

import java.nio.file.AccessDeniedException;
import java.util.List;

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
   * @throws AccessDeniedException     Исключение, выбрасываемое в случае отсутствия доступа для добавления показаний.
   */
  MeterReading addMeterValue(MeterReading meterReading) throws MeterReadingConflictError, AccessDeniedException;

  /**
   * Получает актуальные данные по всем переданным типам.
   * Если указана дата, то возвращает все показания по этим типам за эту дату.
   *
   * @param parameters Параметры для получения данных.
   * @return Список доступных показаний.
   */
  List<MeterReading> getActualMeterByAddress(Parameters parameters);

  /**
   * История показаний по переданному адресу.
   *
   * @param parameters Параметры для построения запроса истории показаний.
   * @return История всех выбранных показаний, по указанному адресу, со всеми ограничениями, если они указаны.
   */
  List<MeterReading> getHistoryMeterByAddress(Parameters parameters);
}