package com.denknd.time.api;

import com.denknd.time.config.MeasureTimeConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для включения данного модуля
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(MeasureTimeConfig.class)
public @interface EnableMeasureTime {
}
