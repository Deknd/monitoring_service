package com.denknd.services.impl;

import com.denknd.entity.MeterReading;
import com.denknd.port.MeterReadingRepository;
import com.denknd.services.MeterReadingService;
import lombok.RequiredArgsConstructor;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис для работы с показаниями
 */
@RequiredArgsConstructor
public class MeterReadingServiceImpl implements MeterReadingService {
    /**
     * Репозиторий для работы с показаниями
     */
    private final MeterReadingRepository meterReadingRepository;

    /**
     * фильтрует показания на актуальные и возвращает их
     * @param addressId айди адреса, по которому нужно получить показания
     * @param type тип показаний, который нужно получить
     * @return возвращает показание актуальное для данного типа по принятому адресу
     */
    @Override
    public MeterReading getActualMeter(Long addressId, String type) {
        return this.meterReadingRepository.findMeterReadingByAddressId(addressId).stream()
                .filter(meterReading -> meterReading.getTypeMeter().getTypeCode().equals(type))
                .max(Comparator.comparing(MeterReading::getSubmissionMonth))
                .orElse(null);

    }

    /**
     * Отправляет в репозиторий полученные показания
     * @param meterReading полностью заполненный объект с показаниями без айди
     * @return полностью заполненный объект с показаниями с айди
     */
    @Override
    public MeterReading addMeterValue(MeterReading meterReading) {
        return this.meterReadingRepository.save(meterReading);
    }

    /**
     * Получает все актуальные показания по адресу, с дополнительными условиями для фильтрации
     * @param addressId идентификатор адреса по которму нужны показания
     * @param typeCode типы которые нужны пользователю
     * @param date если не нул, то все показания будут получены по этой дате
     * @return список всех актульных показаний
     */
    @Override
    public List<MeterReading> getActualMeterByAddress(Long addressId, Set<String> typeCode, YearMonth date) {
        var meterReadingByAddressId = this.meterReadingRepository.findMeterReadingByAddressId(addressId);
        if (date == null) {
            meterReadingByAddressId = meterReadingByAddressId.stream()
                    .collect(Collectors.groupingBy(meterReading -> meterReading.getTypeMeter().getTypeCode(),
                            Collectors.maxBy(Comparator.comparing(MeterReading::getSubmissionMonth))))
                    .values()
                    .stream()
                    .flatMap(Optional::stream)
                    .collect(Collectors.toList());
        } else {
            meterReadingByAddressId = meterReadingByAddressId.stream()
                    .filter(meterReading -> meterReading.getSubmissionMonth().equals(date))
                    .toList();
        }


        if (!typeCode.isEmpty()) {
            meterReadingByAddressId = meterReadingByAddressId.stream()
                    .filter(meterReading -> typeCode.contains(meterReading.getTypeMeter().getTypeCode()))
                    .toList();
        }

        return meterReadingByAddressId;
    }

    /**
     * Выдает список всех показаний, в указанных рамках(или без них)
     * @param addressId адрес по которому нужны показания
     * @param typeCode типы показаний
     * @param startDate дата с которой следует собрать показания
     * @param endDate дата по которую нужны показания
     * @return возвращает список показаний по указаным фильтрам
     */
    @Override
    public List<MeterReading> getHistoryMeterByAddress(Long addressId, Set<String> typeCode, YearMonth startDate, YearMonth endDate) {
        var meterReadingByAddressId = this.meterReadingRepository.findMeterReadingByAddressId(addressId);
        if (typeCode!= null && !typeCode.isEmpty()) {
            meterReadingByAddressId = meterReadingByAddressId.stream()
                    .filter(meterReading -> typeCode.contains(meterReading.getTypeMeter().getTypeCode()))
                    .toList();
        }
        if (startDate != null) {
            meterReadingByAddressId = meterReadingByAddressId.stream()
                    .filter(meterReading -> !meterReading.getSubmissionMonth().isBefore(startDate))
                    .toList();
        }
        if (endDate != null) {
            meterReadingByAddressId = meterReadingByAddressId.stream()
                    .filter(meterReading -> !meterReading.getSubmissionMonth().isAfter(endDate))
                    .toList();
        }

        return meterReadingByAddressId;
    }
}
