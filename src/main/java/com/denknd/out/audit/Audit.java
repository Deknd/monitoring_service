package com.denknd.out.audit;

import com.denknd.security.UserSecurity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;


/**
 * Сущность для хранения аудита.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Audit {
  /**
   * Идентификатор.
   */
  private Long auditId;

  /**
   * Описание операции, которую он совершил.
   */
  private String operation;

  /**
   * Дата операции.
   */
  private OffsetDateTime operationTime;

  /**
   * Пользователь, который совершил действие.
   */
  private UserSecurity user;
}
