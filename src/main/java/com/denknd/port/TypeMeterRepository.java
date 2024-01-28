package com.denknd.port;

import com.denknd.entity.TypeMeter;

import java.util.List;

/**
 * Интерфейс для работы с типами данных
 */
public interface TypeMeterRepository {
    /**
     * Ищет все доступные типы данных счетчиков
     * @return список все типов счетчиков
     */
    List<TypeMeter> findTypeMeter();

    /**
     * Сохраняет новые типы данных
     * @param typeMeter полностью заполненный объект без айди
     * @return полностью заполненный объект с айди
     */
    TypeMeter save(TypeMeter typeMeter);
}
