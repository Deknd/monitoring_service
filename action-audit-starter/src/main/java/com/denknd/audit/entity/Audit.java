package com.denknd.audit.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;


/**
 * Сущность для хранения аудита.
 * Этот класс представляет аудиторские записи, в которых хранится информация о совершенных операциях,
 * включая их описание, дату выполнения и идентификатор пользователя.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Audit {
  /**
   * Идентификатор аудита.
   */
  private Long auditId;

  /**
   * Описание операции, которую совершил пользователь.
   */
  private String operation;

  /**
   * Дата и время выполнения операции.
   */
  private OffsetDateTime operationTime;

  /**
   * Идентификатор пользователя
   */
  private Long userId;
}
