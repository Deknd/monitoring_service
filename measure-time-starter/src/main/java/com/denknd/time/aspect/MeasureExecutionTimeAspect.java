package com.denknd.time.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Класс для аспекта, которы считает время выполнения данного метода
 */
@Aspect
@Slf4j
public class MeasureExecutionTimeAspect {


  /**
   * Метод, который считает время выполнения выделенного метода
   *
   * @param proceedingJoinPoint точка входа
   * @return возвращает процесс выполнения
   * @throws Throwable ошибки, которые могут возникнуть при выполнении
   */
  @Around("@annotation(com.denknd.time.api.MeasureExecutionTime)")
  public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    var startTime = System.currentTimeMillis();
    var result = proceedingJoinPoint.proceed();
    var endTime = System.currentTimeMillis();
    var executionTime = endTime - startTime;
    log.info("Метод: " + proceedingJoinPoint.getSignature().toShortString() + " Время работы метода: " + executionTime + " milliseconds");
    return result;
  }
}
