package com.denknd.services.impl;

import com.denknd.entity.Meter;
import com.denknd.entity.MeterReading;
import com.denknd.entity.TypeMeter;
import com.denknd.exception.MeterReadingConflictError;
import com.denknd.repository.MeterReadingRepository;
import com.denknd.services.MeterCountService;
import com.denknd.services.MeterReadingService;
import com.denknd.services.TypeMeterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Сервис для работы с показаниями.
 */
@RequiredArgsConstructor
@Slf4j
public class MeterReadingServiceImpl implements MeterReadingService {
  /**
   * Репозиторий для работы с показаниями.
   */
  private final MeterReadingRepository meterReadingRepository;
  /**
   * Сервис для работы с типами показаний.
   */
  private final TypeMeterService typeMeterService;
  /**
   * Сервис для работы с счетчиками
   */
  private final MeterCountService meterCountService;


  /**
   * Отправляет в репозиторий полученные показания.
   *
   * @param meterReading Полностью заполненный объект с показаниями без айди.
   * @return Полностью заполненный объект с показаниями с присвоенным айди.
   * @throws MeterReadingConflictError Выбрасывается, когда попытка подать показания в один и тот же месяц
   *                                   или когда данные вводимые меньше, чем были до этого.
   */

  @Override
  public MeterReading addMeterValue(MeterReading meterReading) throws MeterReadingConflictError {
    var actualMeter = this.meterReadingRepository.findActualMeterReading(meterReading.getAddress().getAddressId(), meterReading.getTypeMeter().getTypeMeterId()).orElse(null);
    var submissionMonth = YearMonth.now();
    if (actualMeter != null && submissionMonth.isBefore(actualMeter.getSubmissionMonth())
            || actualMeter != null && submissionMonth.equals(actualMeter.getSubmissionMonth())) {
      throw new MeterReadingConflictError("Данные за " + submissionMonth + " уже внесены");
    }
    if (actualMeter != null && Double.compare(meterReading.getMeterValue(), actualMeter.getMeterValue()) < 0) {
      throw new MeterReadingConflictError("Не верные показания или новый счетчик. Вызовите мастера для проверки и пломбирования счетчика");
    }
    var timeSendMeter = OffsetDateTime.now();
    meterReading.setSubmissionMonth(submissionMonth);
    meterReading.setTimeSendMeter(timeSendMeter);
    if(actualMeter == null || actualMeter.getMeter() == null){
      var meterCount = Meter.builder()
              .typeMeterId(meterReading.getTypeMeter().getTypeMeterId())
              .addressId(meterReading.getAddress().getAddressId())
              .build();
      try {
        this.meterCountService.saveMeterCount(meterCount);
      } catch (SQLException e) {
        log.info("Ошибка сохранения информации о счетчике");
      }
    }
    try {
      return this.meterReadingRepository.save(meterReading);
    } catch (SQLException e) {
      throw new MeterReadingConflictError("Не верные показания, не соблюдены ограничения БД. " + e.getMessage());
    }
  }

  /**
   * Получает все актуальные показания по адресу, с дополнительными условиями для фильтрации.
   *
   * @param addressIds Идентификаторы адресов, по которым нужны показания.
   * @param typeCode   Типы, которые нужны пользователю.
   * @param date       Если не null, то все показания будут получены по этой дате.
   * @return Список всех актуальных показаний.
   */
  @Override
  public List<MeterReading> getActualMeterByAddress(Set<Long> addressIds, Set<TypeMeter> typeCode, YearMonth date) {
   List<MeterReading> result;
    var actualType = Set.copyOf(this.typeMeterService.getTypeMeter());

    if (date == null) {
      if (typeCode == null || typeCode.isEmpty()) {
        result = addressIds.stream()
                .flatMap(
                        addressId ->
                                this.getMeterReadings(addressId, actualType)
                                        .stream())
                .toList();
      } else {
        result = addressIds.stream()
                .flatMap(addressId -> this.getMeterReadings(addressId, typeCode)
                        .stream())
                .toList();
      }
    } else {
      if (typeCode == null || typeCode.isEmpty()) {

        result = addressIds.stream()
                .flatMap(addressId ->
                        this.getMeterReadingsWithDate(addressId, actualType, date)
                                .stream())
                .toList();
      } else {
        result = addressIds.stream()
                .flatMap(addressId ->
                        this.getMeterReadingsWithDate(addressId, typeCode, date)
                                .stream())
                .toList();
      }
    }
    return result.stream()
            .peek(meterReading -> {
              var typeMeter
                      = actualType.stream()
                      .filter(type -> type.getTypeMeterId().equals(meterReading.getTypeMeter().getTypeMeterId()))
                      .findFirst()
                      .get();
              meterReading.setTypeMeter(typeMeter);
            }).toList();
  }

  /**
   * Получает список показаний по указанной дате для конкретного адреса и типов счетчиков.
   *
   * @param addressId Идентификатор адреса.
   * @param typeCode  Множество типов показаний.
   * @param date      Дата, для которой нужны показания.
   * @return Список показаний, соответствующих указанным параметрам.
   */
  private List<MeterReading> getMeterReadingsWithDate(Long addressId, Set<TypeMeter> typeCode, YearMonth date) {
    return typeCode.stream()
            .map(type -> this.meterReadingRepository.findMeterReadingForDate(addressId, type.getTypeMeterId(), date))
            .map(optional -> optional.orElse(null))
            .filter(Objects::nonNull)
            .toList();
  }

  /**
   * Получает список актуальных показаний для конкретного адреса и типов счетчиков.
   *
   * @param addressId Идентификатор адреса.
   * @param typeCode  Множество типов показаний.
   * @return Список актуальных показаний, соответствующих указанным параметрам.
   */
  private List<MeterReading> getMeterReadings(Long addressId, Set<TypeMeter> typeCode) {
    return typeCode.stream()
            .map(type -> this.meterReadingRepository.findActualMeterReading(addressId, type.getTypeMeterId()))
            .map(optional -> optional.orElse(null))
            .filter(Objects::nonNull)
            .toList();
  }

  /**
   * Выдает список всех показаний в указанных рамках (или без них).
   *
   * @param addressIds Идентификаторы адресов, по которым нужны показания.
   * @param typeCode   Типы показаний.
   * @param startDate  Дата с которой следует собрать показания.
   * @param endDate    Дата по которую нужны показания.
   * @return Список показаний по указанным фильтрам.
   */
  @Override
  public List<MeterReading> getHistoryMeterByAddress(Set<Long> addressIds, Set<Long> typeCode, YearMonth startDate, YearMonth endDate) {
    List<MeterReading> meterReadingsAllAddress = new ArrayList<>();
    var typeMeterList = this.typeMeterService.getTypeMeter();
    for (Long addressId : addressIds) {
      var meterReadingByAddressId
              = this.meterReadingRepository.findMeterReadingByAddressId(addressId)
              .stream()
              .peek(meterReading -> {
                var typeMeter
                        = typeMeterList.stream()
                        .filter(type -> type.getTypeMeterId().equals(meterReading.getTypeMeter().getTypeMeterId()))
                        .findFirst()
                        .get();
                meterReading.setTypeMeter(typeMeter);
              }).toList();
      meterReadingsAllAddress.addAll(meterReadingByAddressId);

    }
    if (typeCode != null && !typeCode.isEmpty()) {
      meterReadingsAllAddress = meterReadingsAllAddress.stream()
              .filter(meterReading -> typeCode.contains(meterReading.getTypeMeter().getTypeMeterId()))
              .toList();
    }
    if (startDate != null) {
      meterReadingsAllAddress = meterReadingsAllAddress.stream()
              .filter(meterReading -> !meterReading.getSubmissionMonth().isBefore(startDate))
              .toList();
    }
    if (endDate != null) {
      meterReadingsAllAddress = meterReadingsAllAddress.stream()
              .filter(meterReading -> !meterReading.getSubmissionMonth().isAfter(endDate))
              .toList();
    }

    return meterReadingsAllAddress;
  }
}
