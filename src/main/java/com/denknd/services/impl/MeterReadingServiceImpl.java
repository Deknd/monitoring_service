package com.denknd.services.impl;

import com.denknd.entity.Address;
import com.denknd.entity.Meter;
import com.denknd.entity.MeterReading;
import com.denknd.entity.Parameters;
import com.denknd.entity.Roles;
import com.denknd.entity.TypeMeter;
import com.denknd.exception.MeterReadingConflictError;
import com.denknd.repository.MeterReadingRepository;
import com.denknd.security.service.SecurityService;
import com.denknd.services.AddressService;
import com.denknd.services.MeterCountService;
import com.denknd.services.MeterReadingService;
import com.denknd.services.TypeMeterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис для работы с показаниями.
 */
@Service
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
   * Сервис для управления адресами.
   */
  private final AddressService addressService;
  /**
   * Сервис по работе с безопасностью
   */
  private final SecurityService securityService;

  /**
   * Добавляет показания счетчика.
   *
   * @param meterReading Показания счетчика для добавления
   * @return Добавленные показания счетчика с присвоенным идентификатором
   * @throws MeterReadingConflictError Выбрасывается, если возникает конфликт при добавлении показаний счетчика
   * @throws AccessDeniedException     Выбрасывается, если доступ запрещен для данной операции
   */
  @Override
  public MeterReading addMeterValue(MeterReading meterReading) throws MeterReadingConflictError, AccessDeniedException {
    var userSecurity = this.securityService.getUserSecurity();
    if (userSecurity.role().equals(Roles.USER)) {
      var addressesByActiveUser = this.addressService.getAddresses(Optional.of(userSecurity.userId()));
      var addressOwner = addressesByActiveUser.stream()
              .anyMatch(address -> {
                if (address.getAddressId().equals(meterReading.getAddress().getAddressId())) {
                  meterReading.setAddress(address);
                  return true;
                }
                return false;
              });
      if (!addressOwner) {
        throw new MeterReadingConflictError("Адрес не принадлежит вам");
      }
      var typeMeter = this.typeMeterService.getTypeMeter()
              .stream()
              .filter(type ->
                      type.getTypeMeterId().equals(meterReading.getTypeMeter().getTypeMeterId()))
              .findFirst()
              .orElse(null);
      if (typeMeter == null) {
        throw new MeterReadingConflictError("Не известный тип показаний");
      } else {
        meterReading.setTypeMeter(typeMeter);
      }

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
      if (actualMeter == null || actualMeter.getMeter() == null) {
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
    throw new AccessDeniedException("Доступ запрещен, можно подавать показания, только с ролью USER");
  }

  /**
   * Получает все актуальные показания счетчика по адресу с указанными параметрами.
   *
   * @param parameters Параметры для фильтрации показаний
   * @return Список актуальных показаний счетчика по адресу с примененными фильтрами
   */
  @Override
  public List<MeterReading> getActualMeterByAddress(Parameters parameters) {
    if (!this.securityService.isAuthentication()){
      return Collections.emptyList();
    }
    var typeCode = this.typeMeterService.getTypeMeter()
            .stream()
            .filter(
                    typeMeter -> {
                      Set<Long> typeMeterIds = parameters.getTypeMeterIds();
                      if (typeMeterIds == null) {
                        return false;
                      }
                      return typeMeterIds.stream()
                              .anyMatch(typeMeterId -> typeMeterId.equals(typeMeter.getTypeMeterId()));
                    }
            )
            .collect(Collectors.toSet());
    List<MeterReading> result;
    var actualType = Set.copyOf(this.typeMeterService.getTypeMeter());
    var addressIds = this.getAddressIdByRole(parameters);
    if (addressIds == null) {
      return Collections.emptyList();
    }
    if (parameters.getDate() == null) {
      result = getMeterReadings(typeCode, actualType, addressIds);
    } else {
      result = getMeterReadingsByDate(parameters, typeCode, actualType, addressIds);
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
   * Получает список показаний для указанных типов и адресов.
   * Если типы показаний не указаны, будут возвращены все доступные показания для указанных адресов.
   *
   * @param typeCode   Множество типов показаний, по которым нужно получить показания.
   * @param actualType Множество всех доступных типов показаний.
   * @param addressIds Множество идентификаторов адресов, для которых нужно получить показания.
   * @return Список показаний, соответствующих указанным параметрам.
   */
  private List<MeterReading> getMeterReadings(Set<TypeMeter> typeCode, Set<TypeMeter> actualType, Set<Long> addressIds) {
    List<MeterReading> result;
    if (typeCode.isEmpty()) {
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
    return result;
  }

  /**
   * Получает список показаний для указанных типов, адресов и даты.
   * Если типы показаний не указаны, будут возвращены все доступные показания для указанных адресов.
   * Если дата не указана, будут возвращены показания для указанных адресов без учета даты.
   *
   * @param parameters Параметры для получения показаний.
   * @param typeCode   Множество типов показаний, по которым нужно получить показания.
   * @param actualType Множество всех доступных типов показаний.
   * @param addressIds Множество идентификаторов адресов, для которых нужно получить показания.
   * @return Список показаний, соответствующих указанным параметрам.
   */
  private List<MeterReading> getMeterReadingsByDate(Parameters parameters, Set<TypeMeter> typeCode, Set<TypeMeter> actualType, Set<Long> addressIds) {
    List<MeterReading> result;
    if (typeCode.isEmpty()) {

      result = addressIds.stream()
              .flatMap(addressId ->
                      this.getMeterReadingsWithDate(addressId, actualType, parameters.getDate())
                              .stream())
              .toList();
    } else {
      result = addressIds.stream()
              .flatMap(addressId ->
                      this.getMeterReadingsWithDate(addressId, typeCode, parameters.getDate())
                              .stream())
              .toList();
    }
    return result;
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
   * @param parameters Параметры для фильтрации показаний.
   * @return Список показаний, соответствующих указанным фильтрам.
   */
  @Override
  public List<MeterReading> getHistoryMeterByAddress(Parameters parameters) {
    if (!this.securityService.isAuthentication()){
      return Collections.emptyList();
    }
    var addressIds = this.getAddressIdByRole(parameters);
    if (addressIds == null || addressIds.isEmpty()) {
      return Collections.emptyList();
    }
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
    if (parameters.getTypeMeterIds() != null && !parameters.getTypeMeterIds().isEmpty()) {
      meterReadingsAllAddress = meterReadingsAllAddress.stream()
              .filter(meterReading -> parameters.getTypeMeterIds().contains(meterReading.getTypeMeter().getTypeMeterId()))
              .toList();
    }

    if (parameters.getStartDate() != null) {
      meterReadingsAllAddress = meterReadingsAllAddress.stream()
              .filter(meterReading -> !meterReading.getSubmissionMonth().isBefore(parameters.getStartDate()))
              .toList();
    }

    if (parameters.getEndDate() != null) {
      meterReadingsAllAddress = meterReadingsAllAddress.stream()
              .filter(meterReading -> !meterReading.getSubmissionMonth().isAfter(parameters.getEndDate()))
              .toList();
    }

    return meterReadingsAllAddress;
  }

  /**
   * Получает идентификаторы адресов в соответствии с ролью пользователя.
   *
   * @param buildParameters Параметры для определения роли пользователя и фильтрации адресов.
   * @return Набор идентификаторов адресов, доступных пользователю в соответствии с его ролью.
   */
  private Set<Long> getAddressIdByRole(Parameters buildParameters) {
    var userSecurity = this.securityService.getUserSecurity();
    if (userSecurity.role().equals(Roles.USER)) {
      return this.getAddressId(userSecurity.userId(), buildParameters.getAddressId());
    }
    if (userSecurity.role().equals(Roles.ADMIN)) {
      return this.getAddressId(
              buildParameters.getUserId(),
              buildParameters.getAddressId());
    }
    return Collections.emptySet();
  }

  /**
   * Получить доступные данному пользователю адреса.
   * Если addressId равен null, то вернет все доступные пользователю адреса,
   * иначе проверит, что данный адрес принадлежит пользователю
   *
   * @param userId    идентификатор пользователя, которому нужны адреса
   * @param addressId идентификатор адреса, проверяется на принадлежность к пользователю(может быть null)
   * @return список идентификаторов адресов доступный пользователю
   */
  private Set<Long> getAddressId(Long userId, Long addressId) {
    var addressesByUser =
            this.addressService.getAddresses(Optional.of(userId));

    if (addressId != null) {
      addressesByUser = addressesByUser
              .stream()
              .filter(address -> address.getAddressId().equals(addressId))
              .toList();
    }
    if (addressesByUser.isEmpty()) {
      return null;
    }
    return addressesByUser.stream()
            .map(Address::getAddressId)
            .collect(Collectors.toSet());
  }
}
