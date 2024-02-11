package com.denknd.entity;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * Класс для хранения информации о счетчике.
 */
@Builder
@Data
public class Meter {
  /**
   * Идентификатор сущности.
   */
  private Long meterCountId;
  /**
   * Адрес к которому относиться данный счетчик.
   */
  private Long addressId;
  /**
   * Тип показаний к которому он относится.
   */
  private Long typeMeterId;
  /**
   * серийный номер.
   */
  private String serialNumber;
  /**
   * Дата регистрации счетчика.
   */
  private OffsetDateTime registrationDate;
  /**
   * Дата последней проверки.
   */
  private OffsetDateTime lastCheckDate;
  /**
   * Модель счетчика.
   */
  private String meterModel;
}
