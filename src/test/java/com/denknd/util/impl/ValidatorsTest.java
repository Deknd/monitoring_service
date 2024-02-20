package com.denknd.util.impl;

import com.denknd.entity.TypeMeter;
import com.denknd.exception.ConstraintViolationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ValidatorsTest {

  private AutoCloseable autoCloseable;
  @Mock
  ValidatorFactory validatorFactory;
  private Validators validators;
  @Mock
  private Validator validator;

  @BeforeEach
  void setUp() {
    this.autoCloseable = MockitoAnnotations.openMocks(this);
    when(this.validatorFactory.getValidator()).thenReturn(this.validator);
    this.validators = new Validators(this.validatorFactory);
  }

  @AfterEach
  void tearDown() throws Exception {
    this.autoCloseable.close();
  }

  @Test
  @DisplayName("Проверяет, что данный объектв валиден")
  void validate() {
    var typeMeter = TypeMeter.builder()
            .typeMeterId(1L)
            .typeCode("code")
            .typeDescription("description")
            .metric("m3")
            .build();

    assertThatCode(() -> this.validators.validate(typeMeter)).doesNotThrowAnyException();
    ;

    verify(this.validator, times(1)).validate(eq(typeMeter));
  }

  @Test
  @DisplayName("Проверяет, что данный объектв валиден")
  void validate_notValid() {
    var typeMeter = TypeMeter.builder()
            .typeMeterId(1L)
            .typeCode("code")
            .typeDescription("description")
            .metric("m3")
            .build();
    when(this.validator.validate(eq(typeMeter))).thenReturn(Set.of(mock(ConstraintViolation.class)));

    assertThatThrownBy(() -> this.validators.validate(typeMeter)).isInstanceOf(ConstraintViolationException.class);

    verify(this.validator, times(1)).validate(eq(typeMeter));
  }
}