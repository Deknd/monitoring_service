package com.denknd.port;

import com.denknd.entity.MeterReading;

import java.util.List;

public interface MeterReadingRepository {
    MeterReading save(MeterReading meterReading);
    List<MeterReading> findMeterReadingByAddressId(Long addressId);
}
