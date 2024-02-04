package com.denknd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Класс для хранения показаний.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeterReading {
  /**
   * Идентификатор сущности.
   */
  private Long meterId;
  /**
   * Адрес, к которому относится данные показания.
   */
  private Address address;
  /**
   * Тип показаний.
   */
  private TypeMeter typeMeter;
  /**
   * Показания.
   */
  private Double meterValue;
  /**
   * Период в котором подали данные показания.
   */
  private YearMonth submissionMonth;
  /**
   * Информация о счетчике.
   */
  private Meter meter;
  /**
   * Дата подачи показаний.
   */
  private OffsetDateTime timeSendMeter;


}
