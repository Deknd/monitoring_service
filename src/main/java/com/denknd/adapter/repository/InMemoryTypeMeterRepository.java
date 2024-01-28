package com.denknd.adapter.repository;

import com.denknd.entity.TypeMeter;
import com.denknd.port.TypeMeterRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Для хранения типов показаний в памяти
 */
public class InMemoryTypeMeterRepository implements TypeMeterRepository {
    /**
     * Хранит связь айди -> объект TypeMeter
     */
    private final Map<Long, TypeMeter> typeMeterMap = new HashMap<>();
    /**
     * Для генерации айди
     */
    private final Random random = new Random();

    /**
     * Сохраняет в память стандартные типы
     */
    public InMemoryTypeMeterRepository(){
        var cold = TypeMeter.builder()
                .typeMeterId(0L)
                .typeCode("cold")
                .typeDescription("Холодная вода")
                .metric("м3")
                .build();
        var warm = TypeMeter.builder()
                .typeMeterId(1L)
                .typeCode("warm")
                .typeDescription("Горячая вода")
                .metric("м3")
                .build();
        var heat = TypeMeter.builder()
                .typeMeterId(2L)
                .typeCode("heat")
                .typeDescription("Отопление")
                .metric("Гкал")
                .build();
        this.typeMeterMap.put(cold.getTypeMeterId(), cold);
        this.typeMeterMap.put(warm.getTypeMeterId(), warm);
        this.typeMeterMap.put(heat.getTypeMeterId(), heat);
    }

    /**
     * Ищет все доступные типы
     * @return список доступных типов показаний
     */
    @Override
    public List<TypeMeter> findTypeMeter() {
        return this.typeMeterMap.keySet().stream().map(this.typeMeterMap::get).map(this::buildTypeMeter).toList();
    }

    /**
     * Сохраняет новые типы показаний в память
     * @param typeMeter заполненный объект TypeMeter, без айди
     * @return возвращает копию заполненного объекта
     */
    @Override
    public TypeMeter save(TypeMeter typeMeter) {
        long typeMeterId;
        if(typeMeter.getTypeMeterId() == null){
            do {
                typeMeterId = Math.abs(this.random.nextLong());
            } while (this.typeMeterMap.containsKey(typeMeterId));
        } else {
            return null;
        }
        typeMeter.setTypeMeterId(typeMeterId);
        this.typeMeterMap.put(typeMeterId, typeMeter);
        return buildTypeMeter(typeMeter);
    }

    /**
     * Копирует входящий объект
     * @param typeMeter заполненный объект TypeMeter
     * @return копия входящего объекта
     */
    private TypeMeter buildTypeMeter(TypeMeter typeMeter) {
        return TypeMeter.builder()
                .typeMeterId(typeMeter.getTypeMeterId())
                .typeCode(typeMeter.getTypeCode())
                .typeDescription(typeMeter.getTypeDescription())
                .metric(typeMeter.getMetric())
                .build();
    }
}
