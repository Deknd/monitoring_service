package com.denknd.services;

import com.denknd.entity.TypeMeter;
import com.denknd.exception.TypeMeterAdditionException;

import java.nio.file.AccessDeniedException;
import java.util.List;

/**
 * Интерфейс для работы с типами показаний.
 */
public interface TypeMeterService {
  /**
   * Возвращает все доступные типы.
   *
   * @return Список доступных типов показаний.
   */
  List<TypeMeter> getTypeMeter();

  /**
   * Добавляет новый тип показаний.
   *
   * @param newType Новый заполненный объект с типом показаний, без айди.
   * @return Заполненный объект с айди.
   * @throws TypeMeterAdditionException Исключение, выбрасываемое при несоблюдении ограничений базы данных.
   * @throws AccessDeniedException      Исключение, выбрасываемое при отсутствии доступа для добавления нового типа показаний.
   */
  TypeMeter addNewTypeMeter(TypeMeter newType) throws TypeMeterAdditionException, AccessDeniedException;

  /**
   * Возвращает объект с типом показаний по коду этого типа.
   *
   * @param code Код типа показаний.
   * @return Полностью заполненный объект.
   */
  TypeMeter getTypeMeterByCode(String code);

}
