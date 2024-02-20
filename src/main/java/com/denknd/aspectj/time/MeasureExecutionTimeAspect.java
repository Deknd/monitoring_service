package com.denknd.aspectj.time;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Класс для аспекта, которы считает время выполнения данного метода
 */
@Aspect
@Slf4j
@Component
public class MeasureExecutionTimeAspect {


  /**
   * Метод который считает время выполнения выделеного метода
   *
   * @param proceedingJoinPoint точка входа
   * @return возвращает процесс выполнения
   * @throws Throwable ошибки, которые могут возникнуть при выполнении
   */
  @Around("@annotation(com.denknd.aspectj.time.MeasureExecutionTime)")
  public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
      var startTime = System.currentTimeMillis();
      var result = proceedingJoinPoint.proceed();
      var endTime = System.currentTimeMillis();
      var executionTime = endTime - startTime;
      log.info("Метод: " + proceedingJoinPoint.getSignature().toShortString() + " Время работы метода: " + executionTime + " milliseconds");
      return result;
  }
}
