package com.denknd.out.audit;

import com.denknd.security.entity.UserSecurity;

import java.util.Map;

/**
 * Интерфейс для работы с аудитом
 */
public interface AuditService {
  /**
   * Записывает действие пользователя в журнал аудита.
   *
   * @param audit   аудит с действиями
   */
  void addAction(Audit audit);
}
