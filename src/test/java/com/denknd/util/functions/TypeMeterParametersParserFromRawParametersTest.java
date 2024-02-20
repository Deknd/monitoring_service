package com.denknd.util.functions;

import com.denknd.controllers.TypeMeterController;
import com.denknd.dto.TypeMeterDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TypeMeterParametersParserFromRawParametersTest {

  private TypeMeterParametersParserFromRawParameters parserFromRawParameters;
  @Mock
  private TypeMeterController typeMeterController;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.parserFromRawParameters = new TypeMeterParametersParserFromRawParameters(this.typeMeterController);
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
    var typeMeters = Set.of(
            TypeMeterDto.builder().typeMeterId(1L).build(),
            TypeMeterDto.builder().typeMeterId(2L).build(),
            TypeMeterDto.builder().typeMeterId(3L).build()
    );
    var param = "1,2";
    when(this.typeMeterController.getTypeMeterCodes()).thenReturn(typeMeters);

    var longSet = this.parserFromRawParameters.apply(param);

    assertThat(longSet).contains(test1, test2).doesNotContain(test3);
    verify(this.typeMeterController, times(1)).getTypeMeterCodes();
  }
  @Test
  @DisplayName("Проверяет что при отсутствии параметров, возвращается пустой сет")
  void apply_null() {
    var test1 = 1L;
    var test2 = 2L;
    var test3 = 3L;
    var typeMeters = Set.of(
            TypeMeterDto.builder().typeMeterId(1L).build(),
            TypeMeterDto.builder().typeMeterId(2L).build(),
            TypeMeterDto.builder().typeMeterId(3L).build()
    );
    var param = "1,2";
    when(this.typeMeterController.getTypeMeterCodes()).thenReturn(typeMeters);

    var longSet = this.parserFromRawParameters.apply(null);

    assertThat(longSet).doesNotContain(test3, test1, test2);
    verify(this.typeMeterController, times(0)).getTypeMeterCodes();
  }
  @Test
  @DisplayName("Проверяет что при вводе, не последовательности цифр, возвращается пустой сет")
  void apply_() {
    var test1 = 1L;
    var test2 = 2L;
    var test3 = 3L;
    var typeMeters = Set.of(
            TypeMeterDto.builder().typeMeterId(1L).build(),
            TypeMeterDto.builder().typeMeterId(2L).build(),
            TypeMeterDto.builder().typeMeterId(3L).build()
    );
    var param = "1,2";
    when(this.typeMeterController.getTypeMeterCodes()).thenReturn(typeMeters);

    var longSet = this.parserFromRawParameters.apply("asdfsdaf");

    assertThat(longSet).doesNotContain(test3, test1, test2);
    verify(this.typeMeterController, times(1)).getTypeMeterCodes();
  }
}