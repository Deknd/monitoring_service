package com.denknd.port;

import com.denknd.entity.TypeMeter;

import java.util.List;

public interface TypeMeterRepository {
    List<TypeMeter> findTypeMeter();
    TypeMeter save(TypeMeter typeMeter);
}
