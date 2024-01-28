package com.denknd.services.impl;

import com.denknd.entity.TypeMeter;
import com.denknd.port.TypeMeterRepository;
import com.denknd.services.TypeMeterService;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Сервис для работы с типами показаний
 */
@RequiredArgsConstructor
public class TypeMeterServiceImpl implements TypeMeterService {
    /**
     * Репозиторий для хранения и получения типов показаний
     */
    private final TypeMeterRepository typeMeterRepository;

    /**
     * Выдает доступные типы показаний
     * @return список доступных типов показаний
     */
    @Override
    public List<TypeMeter> getTypeMeter() {
        return this.typeMeterRepository.findTypeMeter();
    }

    /**
     * добавление новых типов показаний
     * @param newType полностью заполненный объект без айди
     * @return полностью заполненный объект с айди
     */
    @Override
    public TypeMeter addNewTypeMeter(TypeMeter newType) {
        return this.typeMeterRepository.save(newType);

    }
}
