package com.denknd.aspectj.time;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Класс для аспекта, которы считает время выполнения данного метода
 */
@Aspect
@Slf4j
public class MeasureExecutionTimeAspect {
  /**
   * Точка входа для выполнения аспекта
   */
  @Pointcut("@annotation(com.denknd.aspectj.time.MeasureExecutionTime)")
  public void annotatedByMeasureExecutionTime() {
  }

  /**
   * Метод который считает время выполнения выделеного метода
   * @param proceedingJoinPoint точка входа
   * @return возвращает процесс выполнения
   * @throws Throwable ошибки, которые могут возникнуть при выполнении
   */
  @Around("annotatedByMeasureExecutionTime()")
  public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    var startTime = System.currentTimeMillis();
    var result = proceedingJoinPoint.proceed();
    var endTime = System.currentTimeMillis();
    var executionTime = endTime - startTime;
    log.info("Метод: " + proceedingJoinPoint.getSignature().toShortString() + " Время работы метода: " + executionTime + " milliseconds");
    return result;
  }
}
