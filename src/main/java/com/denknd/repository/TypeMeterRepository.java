package com.denknd.repository;

import com.denknd.entity.TypeMeter;

import java.util.List;

/**
 * Интерфейс репозитория для работы с типами показаний счетчиков.
 */
public interface TypeMeterRepository {
  /**
   * Ищет все доступные типы показаний счетчиков.
   *
   * @return Список всех доступных типов показаний счетчиков.
   */
  List<TypeMeter> findTypeMeter();

  /**
   * Сохраняет новые типы показаний счетчиков.
   *
   * @param typeMeter Полностью заполненный объект без идентификатора.
   * @return Полностью заполненный объект с присвоенным идентификатором.
   */
  TypeMeter save(TypeMeter typeMeter);

}
