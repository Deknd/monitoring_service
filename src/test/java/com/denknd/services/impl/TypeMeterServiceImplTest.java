package com.denknd.services.impl;

import com.denknd.entity.TypeMeter;
import com.denknd.repository.TypeMeterRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.Mapping;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TypeMeterServiceImplTest {

  @Mock
  private TypeMeterRepository repository;
  private TypeMeterServiceImpl typeMeterService;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.typeMeterService = new TypeMeterServiceImpl(this.repository);
  }
  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
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


  @Test
  @DisplayName("проверяет, что достает данные из репозитория и выбирает из них нужный тип")
  void getTypeMeterByCode() {
    var typeCode = "test2";
    var typeMeter = TypeMeter.builder().typeCode(typeCode).build();
    var typeMeterList = List.of(
            TypeMeter.builder().typeCode("test").build(),
            typeMeter,
            TypeMeter.builder().typeCode("test3").build()
    );
    when(this.repository.findTypeMeter()).thenReturn(typeMeterList);

    var typeMeterByCode = this.typeMeterService.getTypeMeterByCode(typeCode);

    assertThat(typeMeterByCode).isEqualTo(typeMeter);
  }
  @Test
  @DisplayName("проверяет, что достает данные из репозитория и не находит код с данным типом")
  void getTypeMeterByCode_null() {

    var typeMeterList = List.of(
            TypeMeter.builder().typeCode("test").build(),
            TypeMeter.builder().typeCode("test2").build(),
            TypeMeter.builder().typeCode("test3").build()
    );
    when(this.repository.findTypeMeter()).thenReturn(typeMeterList);

    var typeMeterByCode = this.typeMeterService.getTypeMeterByCode("null");

    assertThat(typeMeterByCode).isNull();
  }
}