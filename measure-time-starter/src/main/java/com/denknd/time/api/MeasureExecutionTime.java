package com.denknd.time.api;

import com.denknd.time.config.MeasureTimeConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для подсчета время выполнения метода
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Import(MeasureTimeConfig.class)
public @interface MeasureExecutionTime {
}
