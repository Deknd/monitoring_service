package com.denknd.controllers;

import com.denknd.dto.MeterReadingRequestDto;
import com.denknd.dto.MeterReadingResponseDto;
import com.denknd.entity.Address;
import com.denknd.entity.MeterReading;
import com.denknd.entity.TypeMeter;
import com.denknd.exception.MeterReadingConflictError;
import com.denknd.mappers.MeterReadingMapper;
import com.denknd.services.AddressService;
import com.denknd.services.MeterReadingService;
import com.denknd.services.TypeMeterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Контроллер для работы с показаниями данных.
 */
@RequiredArgsConstructor
@Log4j2
public class MeterReadingController {
  /**
   * Сервис для управления показаниями.
   */
  private final MeterReadingService meterReadingService;
  /**
   * Сервис для управления адресами.
   */
  private final AddressService addressService;
  /**
   * Сервис для управления типами показаний.
   */
  private final TypeMeterService typeMeterService;
  /**
   * Маппер показаний.
   */
  private final MeterReadingMapper meterReadingMapper;

  /**
   * Возвращает историю показаний по указанным данным.
   * Если addressId == null, то выведется история по всем адресам записанных на пользователя.
   * Выводится история по указанным типам {@param parameters}, если ничего не передавать,
   * то вернется история по всем доступным типам. Если указан startDate,
   * то выводится список от этой даты, иначе ограничений нет.
   * Если указан endDate, то выводится список до этой даты, иначе по текущею дату.
   *
   * @param addressId  идентификатор адреса, по которому нужна история показаний(может быть null).
   * @param userId     идентификатор пользователя, которому нужна история(не может быть null)
   * @param parameters типы показаний, по котором нужна история(может быть null).
   * @param startDate  дата с которой нужны показания
   * @param endDate    дата по которой нужны показания
   * @return список с показаниями, по всем параметрам
   */
  public List<MeterReadingResponseDto> getHistoryMeterReading(
          Long addressId,
          Long userId,
          Set<Long> parameters,
          YearMonth startDate,
          YearMonth endDate
  ) {

    var addressIdSet = this.getAddressId(userId, addressId);
    if (addressIdSet == null) {
      return List.of();
    }
    var historyMeterByAddress = this.meterReadingService.getHistoryMeterByAddress(addressIdSet, parameters, startDate, endDate);
    return this.meterReadingMapper.mapMeterReadingsToMeterReadingResponsesDto(historyMeterByAddress);
  }

  /**
   * Добавляет новые показания в память.
   *
   * @param meterReadingRequestDto показания полученные для сохранения
   * @return возвращает полученные показания
   * @throws MeterReadingConflictError ошибки при сохранении, если показания уже внесены, если показания меньше предыдущих
   */
  public MeterReadingResponseDto addMeterReadingValue(
          MeterReadingRequestDto meterReadingRequestDto,
          Long userId
  ) throws MeterReadingConflictError {
    var addressesByActiveUser = this.addressService.getAddresses(userId);
    var addressOwner = addressesByActiveUser.stream()
            .anyMatch(address ->
                    address.getAddressId().equals(meterReadingRequestDto.addressId()));
    if (!addressOwner) {
      throw new MeterReadingConflictError("Адрес не принадлежит вам");
    }
    var typeMeter = this.typeMeterService.getTypeMeter()
            .stream()
            .filter(type ->
                    type.getTypeMeterId().equals(meterReadingRequestDto.codeType()))
            .findFirst()
            .orElse(null);
    if (typeMeter == null){
      throw  new MeterReadingConflictError("Не известный тип показаний");
    }
    var address = this.addressService.getAddressByAddressId(meterReadingRequestDto.addressId());
    var meterReading = this.meterReadingMapper
            .mapMeterReadingRequestDtoToMeterReading(meterReadingRequestDto, address, typeMeter);
    var resultMeterReading = this.meterReadingService.addMeterValue(meterReading);
    return this.meterReadingMapper.mapMeterReadingToMeterReadingResponseDto(resultMeterReading);
  }

  /**
   * Показывает показания по полученным данным. Если не указана date,
   * то вернутся актуальные показания на текущую дату.
   * Если адрес не указан, то вернутся показания по всем адресам доступным пользователю.
   * Если не указаны типы показаний, по которым нужны данные, то вернутся данные по всем доступным показаниям.
   *
   * @param addressId идентификатор адреса по которому нужны данные(может быть null)
   * @param userId    идентификатор пользователя которому нужны данные(не null)
   * @param type      типы показаний, по которым нужны данные(может быть null)
   * @param date      дата в которую нужны показания(может быть null)
   * @return возвращает данные пользователю, по указанным параметрам
   */
  public List<MeterReadingResponseDto> getMeterReadings(
          Long addressId,
          Long userId,
          Set<Long> type,
          YearMonth date) {
    var addressIdSet = this.getAddressId(userId, addressId);
    if (addressIdSet == null) {
      return List.of();
    }
    var types = this.typeMeterService.getTypeMeter()
            .stream()
            .filter(
                    typeMeter -> type.stream()
                            .anyMatch(typeCode -> typeCode.equals(typeMeter.getTypeMeterId())))
            .collect(Collectors.toSet());


    var actualMeterByAddress = this.meterReadingService.getActualMeterByAddress(addressIdSet, types, date);
    return this.meterReadingMapper.mapMeterReadingsToMeterReadingResponsesDto(actualMeterByAddress);
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
            this.addressService.getAddresses(userId);

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
