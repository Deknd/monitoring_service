package com.denknd.repository.impl;

import com.denknd.entity.MeterReading;
import com.denknd.repository.MeterReadingRepository;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * Реализация интерфейса для хранения показаний в памяти.
 */
public class InMemoryMeterReadingRepository implements MeterReadingRepository {
  /**
   * Генератор id.
   */
  private final Random random = new Random();
  /**
   * Хранение показаний (ключ - meterId, значение - {@link MeterReading}).
   */
  private final Map<Long, MeterReading> meterReadingMap = new HashMap<>();
  /**
   * Связь адресов с их показаниями (ключ - addressId, значение - список meterId).
   */
  private final Map<Long, List<Long>> addressIdMeterIdListMap = new HashMap<>();

  /**
   * Сохраняет показания в памяти.
   *
   * @param meterReading Полностью заполненный объект показаний (без meterId).
   * @return Возвращает объект с сгенерированным meterId.
   */
  @Override
  public MeterReading save(MeterReading meterReading) {
    long meterReadingId;

    if (meterReading.getMeterId() == null) {

      do {
        meterReadingId = Math.abs(this.random.nextLong());

      } while (this.meterReadingMap.containsKey(meterReadingId));
    } else {
      return null;
    }
    meterReading.setMeterId(meterReadingId);
    this.meterReadingMap.put(meterReadingId, meterReading);
    var addressId = meterReading.getAddress().getAddressId();
    if (this.addressIdMeterIdListMap.containsKey(addressId)) {
      var longs = new ArrayList<>(this.addressIdMeterIdListMap.get(addressId));
      longs.add(meterReadingId);
      this.addressIdMeterIdListMap.put(addressId, List.copyOf(longs));
    } else {
      this.addressIdMeterIdListMap.put(addressId, List.of(meterReadingId));
    }
    return meterReading;
  }

  /**
   * Получает список показаний по идентификатору адреса.
   *
   * @param addressId Идентификатор адреса.
   * @return Список показаний по этому адресу.
   */
  @Override
  public List<MeterReading> findMeterReadingByAddressId(Long addressId) {
    return addressIdMeterIdListMap
            .getOrDefault(addressId, Collections.emptyList())
            .stream()
            .map(meterReadingMap::get)
            .toList();
  }

  /**
   * Получает актуальные показания по адресу и типу показаний.
   *
   * @param addressId   Идентификатор адреса, по которому нужно получить показания.
   * @param typeMeterId Идентификатор типа показаний.
   * @return Optional с актуальным типом или пустой, если показаний не найдено.
   */
  @Override
  public Optional<MeterReading> findActualMeterReading(Long addressId, Long typeMeterId) {
    return addressIdMeterIdListMap
            .getOrDefault(addressId, Collections.emptyList())
            .stream()
            .map(meterReadingMap::get)
            .filter(mr -> mr.getTypeMeter().getTypeMeterId().equals(typeMeterId))
            .max(Comparator.comparing(MeterReading::getSubmissionMonth));
  }

  /**
   * Получает актуальные показания по адресу, типу показаний и указанной дате.
   *
   * @param addressId   Идентификатор адреса, по которому нужно получить показания.
   * @param typeMeterId Идентификатор типа показаний.
   * @param date        Дата, по которой нужны показания.
   * @return Optional с актуальным типом или пустой, если показаний не найдено.
   */
  @Override
  public Optional<MeterReading> findMeterReadingForDate(
          Long addressId,
          Long typeMeterId,
          YearMonth date) {
    return addressIdMeterIdListMap
            .getOrDefault(addressId, Collections.emptyList())
            .stream()
            .map(meterReadingMap::get)
            .filter(mr -> mr.getTypeMeter().getTypeMeterId().equals(typeMeterId))
            .filter(mr -> mr.getSubmissionMonth().equals(date))
            .findFirst();
  }

}
