package com.denknd.out.audit;

import com.denknd.security.entity.UserSecurity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;


/**
 * Сущность для хранения аудита.
 * Этот класс представляет аудиторские записи, в которых хранится информация о совершенных операциях,
 * включая их описание, дату выполнения и пользователя, который их совершил.
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
   * Пользователь, который совершил операцию.
   */
  private UserSecurity user;
}
