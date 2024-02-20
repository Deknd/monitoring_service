package com.denknd.util.functions;

import com.denknd.entity.TypeMeter;
import com.denknd.in.controllers.TypeMeterController;
import com.denknd.dto.TypeMeterDto;
import com.denknd.services.TypeMeterService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TypeMeterParametersParserFromRawParametersTest {

  private TypeMeterParametersParserFromRawParameters parserFromRawParameters;
  @Mock
  private TypeMeterService typeMeterService;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.parserFromRawParameters = new TypeMeterParametersParserFromRawParameters(this.typeMeterService);
  }

  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }

  @Test
  @DisplayName("Проверяет вытаскивает из массива строк, нужные параметры")
  void apply() {
    var test1 = 1L;
    var test2 = 2L;
    var test3 = 3L;
    var typeMeters = List.of(
            TypeMeter.builder().typeMeterId(1L).build(),
            TypeMeter.builder().typeMeterId(2L).build(),
            TypeMeter.builder().typeMeterId(3L).build()
    );
    var param = "1,2";
    when(this.typeMeterService.getTypeMeter()).thenReturn(typeMeters);

    var longSet = this.parserFromRawParameters.convert(param);

    assertThat(longSet).contains(test1, test2).doesNotContain(test3);
    verify(this.typeMeterService, times(1)).getTypeMeter();
  }

  @Test
  @DisplayName("Проверяет что при вводе, не последовательности цифр, возвращается пустой сет")
  void apply_() {
    var test1 = 1L;
    var test2 = 2L;
    var test3 = 3L;
    var typeMeters = List.of(
            TypeMeter.builder().typeMeterId(1L).build(),
            TypeMeter.builder().typeMeterId(2L).build(),
            TypeMeter.builder().typeMeterId(3L).build());
    when(this.typeMeterService.getTypeMeter()).thenReturn(typeMeters);

    var longSet = this.parserFromRawParameters.convert("asdfsdaf");

    assertThat(longSet).doesNotContain(test3, test1, test2);
    verify(this.typeMeterService, times(1)).getTypeMeter();
  }
}