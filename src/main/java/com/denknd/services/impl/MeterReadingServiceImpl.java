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

@RequiredArgsConstructor
public class MeterReadingServiceImpl implements MeterReadingService {
    private final MeterReadingRepository meterReadingRepository;

    @Override
    public MeterReading getActualMeter(Long addressId, String type) {
        return this.meterReadingRepository.findMeterReadingByAddressId(addressId).stream()
                .filter(meterReading -> meterReading.getTypeMeter().getTypeCode().equals(type))
                .max(Comparator.comparing(MeterReading::getSubmissionMonth))
                .orElse(null);

    }

    @Override
    public MeterReading addMeterValue(MeterReading meterReading) {
        return this.meterReadingRepository.save(meterReading);
    }

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
