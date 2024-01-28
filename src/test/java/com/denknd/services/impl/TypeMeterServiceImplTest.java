package com.denknd.services.impl;

import com.denknd.entity.TypeMeter;
import com.denknd.port.TypeMeterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TypeMeterServiceImplTest {

    private TypeMeterRepository repository;
    private TypeMeterServiceImpl typeMeterService;
    @BeforeEach
    void setUp() {
        this.repository = mock(TypeMeterRepository.class);
        this.typeMeterService = new TypeMeterServiceImpl(this.repository);
    }

    @Test
    @DisplayName("Проверяет, что сервис обращается в репозиторий")
    void getTypeMeter() {
        this.typeMeterService.getTypeMeter();

        verify(this.repository, times(1)).findTypeMeter();
    }

    @Test
    @DisplayName("Проверяет, что сервис обращается в репозиторий")
    void addNewTypeMeter() {
        this.typeMeterService.addNewTypeMeter(mock(TypeMeter.class));

        verify(this.repository, times(1)).save(any(TypeMeter.class));
    }
}