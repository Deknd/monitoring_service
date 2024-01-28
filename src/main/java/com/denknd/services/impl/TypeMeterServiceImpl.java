package com.denknd.services.impl;

import com.denknd.entity.TypeMeter;
import com.denknd.port.TypeMeterRepository;
import com.denknd.services.TypeMeterService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class TypeMeterServiceImpl implements TypeMeterService {

    private final TypeMeterRepository typeMeterRepository;
    @Override
    public List<TypeMeter> getTypeMeter() {
        return this.typeMeterRepository.findTypeMeter();
    }

    @Override
    public TypeMeter addNewTypeMeter(TypeMeter newType) {
        return this.typeMeterRepository.save(newType);

    }
}
