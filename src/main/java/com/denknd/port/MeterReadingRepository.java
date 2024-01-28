package com.denknd.port;

import com.denknd.entity.MeterReading;

import java.util.List;

/**
 * Интерфейс для работы с показаниями счетчика
 */
public interface MeterReadingRepository {
    /**
     * Сохраняет показания счетчика
     * @param meterReading полностью заполненный объект без айди
     * @return возвращает полностью заполненный объект с айди
     */
    MeterReading save(MeterReading meterReading);

    /**
     * Ищет все доступные показания по указанному адресу
     * @param addressId идентификатор адреса
     * @return список доступных показаний на это адрес
     */
    List<MeterReading> findMeterReadingByAddressId(Long addressId);
}
