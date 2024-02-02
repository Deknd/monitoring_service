package com.denknd.controllers;

import com.denknd.dto.TypeMeterDto;
import com.denknd.mappers.TypeMeterMapper;
import com.denknd.services.TypeMeterService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class TypeMeterControllerTest {
  @Mock
  private TypeMeterService typeMeterService;
  @Mock
  private TypeMeterMapper typeMeterMapper;
  private TypeMeterController typeMeterController;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.typeMeterController = new TypeMeterController(this.typeMeterService, this.typeMeterMapper);
  }
  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }
  @Test
  @DisplayName("Проверяет, что метод обращается в нужный сервис")
  void addNewType() {
    var typeMeterDto = mock(TypeMeterDto.class);

    this.typeMeterController.addNewType(typeMeterDto);

    verify(this.typeMeterService, times(1)).addNewTypeMeter(any());
  }

  @Test
  @DisplayName("Проверяет, что метод обращается в нужный сервис")
  void getTypeMeterCodes() {
    this.typeMeterController.getTypeMeterCodes();

    verify(this.typeMeterService, times(1)).getTypeMeter();
  }
}