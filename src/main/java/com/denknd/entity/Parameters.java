package com.denknd.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.YearMonth;
import java.util.Set;

/**
 * Объект для передачи параметров в контроллеры
 */
@Builder
@Getter
public class Parameters {
  /**
   * Идентификатор адреса
   */
  private Long addressId;
  /**
   * Идентификатор пользователя
   */
  private Long userId;
  /**
   * Электронная почта
   */
  private String email;
  /**
   * идентификаторы типов показаний
   */
  private Set<Long> typeMeterIds;
  /**
   * Дата показаний
   */
  private YearMonth startDate;
  /**
   * Дата показаний
   */
  private YearMonth endDate;
  /**
   * Дата показаний
   */
  private YearMonth date;
}
