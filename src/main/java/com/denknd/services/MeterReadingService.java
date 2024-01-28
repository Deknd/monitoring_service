package com.denknd.services;

import com.denknd.entity.MeterReading;

import java.time.YearMonth;
import java.util.List;
import java.util.Set;

public interface MeterReadingService {

    MeterReading getActualMeter(Long addressId, String type);
    MeterReading addMeterValue(MeterReading meterReading);
    List<MeterReading> getActualMeterByAddress(Long addressId, Set<String> typeCode, YearMonth date);
    List<MeterReading> getHistoryMeterByAddress(Long addressId, Set<String> typeCode, YearMonth startDate, YearMonth endDate);
}
