package com.denknd.adapter.repository;

import com.denknd.entity.TypeMeter;
import com.denknd.port.TypeMeterRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class InMemoryTypeMeterRepository implements TypeMeterRepository {
    private final Map<Long, TypeMeter> typeMeterMap = new HashMap<>();

    private final Random random = new Random();
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


    @Override
    public List<TypeMeter> findTypeMeter() {
        return this.typeMeterMap.keySet().stream().map(this.typeMeterMap::get).toList();
    }

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
        return TypeMeter.builder()
                .typeMeterId(typeMeterId)
                .typeCode(typeMeter.getTypeCode())
                .typeDescription(typeMeter.getTypeDescription())
                .metric(typeMeter.getMetric())
                .build();
    }
}
