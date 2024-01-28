package com.denknd.services;

import com.denknd.entity.TypeMeter;

import java.util.List;

public interface TypeMeterService {

    List<TypeMeter> getTypeMeter();
    TypeMeter addNewTypeMeter(TypeMeter newType);
}
