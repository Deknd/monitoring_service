package com.denknd.aspectj.time;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MeasureExecutionTimeAspectTest {

  private MeasureExecutionTimeAspect measureExecutionTimeAspect;

  @BeforeEach
  void setUp() {
    this.measureExecutionTimeAspect = new MeasureExecutionTimeAspect();
  }

  @Test
  @DisplayName("Проверяется, что метод выполняется без ошибок")
  void logging()  {
    var joinPoint = mock(ProceedingJoinPoint.class);
    when(joinPoint.getSignature()).thenReturn(mock(Signature.class));
    when(joinPoint.getKind()).thenReturn("method-execution");

    assertThatCode(() -> this.measureExecutionTimeAspect.logging(joinPoint)).doesNotThrowAnyException();
  }
}