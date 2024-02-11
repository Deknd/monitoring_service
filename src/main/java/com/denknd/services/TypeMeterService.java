package com.denknd.services;

import com.denknd.entity.TypeMeter;
import com.denknd.exception.TypeMeterAdditionException;

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
   * @throws TypeMeterAdditionException при не соблюдения ограничений базы данных
   */
  TypeMeter addNewTypeMeter(TypeMeter newType) throws TypeMeterAdditionException;

  /**
   * Возвращает объект с типом показаний по коду этого типа.
   *
   * @param code Код типа показаний.
   * @return Полностью заполненный объект.
   */
  TypeMeter getTypeMeterByCode(String code);

}
