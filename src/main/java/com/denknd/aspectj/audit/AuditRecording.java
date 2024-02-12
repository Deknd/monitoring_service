package com.denknd.aspectj.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для методов, которые нужны для аудита
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuditRecording {
  /**
   * Краткое описание действия, описывающее работу данного метода
   * @return описания, для аудита
   */
  String value();
}
