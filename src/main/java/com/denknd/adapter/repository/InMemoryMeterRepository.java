package com.denknd.adapter.repository;

import com.denknd.entity.MeterReading;
import com.denknd.port.MeterReadingRepository;

import java.util.*;

/**
 * Класс для хранения показаний в памяти
 */
public class InMemoryMeterRepository implements MeterReadingRepository {
    /**
     * для генерации id
     */
    private final Random random = new Random();
    /**
     * для хранения показаний(ключ - meterId, значения - {@link MeterReading}
     */
    private final Map<Long, MeterReading> meterReadingMap = new HashMap<>();
    /**
     * хранит связь адресов с их показаниями(ключ - addressId, значение - список meterId)
     */
    private final Map<Long, List<Long>> addressIdMeterIdListMap = new HashMap<>();

    /**
     * Служит для сохранения показаний в память
     * @param meterReading полностью заполненный объект показаний(без meterId)
     * @return возвращает объект с генерированным meterId
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
        if(this.addressIdMeterIdListMap.containsKey(addressId)){
            var longs = new ArrayList<>(this.addressIdMeterIdListMap.get(addressId));
            longs.add(meterReadingId);
            this.addressIdMeterIdListMap.put(addressId, List.copyOf(longs));
        } else {
            this.addressIdMeterIdListMap.put(addressId, List.of(meterReadingId));
        }
        return meterReading;
    }

    @Override
    public List<MeterReading> findMeterReadingByAddressId(Long addressId) {
        if(this.addressIdMeterIdListMap.containsKey(addressId)){
            return this.addressIdMeterIdListMap.get(addressId).stream().map(this.meterReadingMap::get).toList();
        }
        return List.of();
    }

}
