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
   * серийный номер.
   */
  private String serialNumber;
  /**
   * Дата установки.
   */
  private OffsetDateTime installationDate;
  /**
   * Дата последней проверки.
   */
  private OffsetDateTime lastCheckDate;
  /**
   * Модель счетчика.
   */
  private String meterModel;
}
