package com.denknd.audit.services;

import com.denknd.audit.entity.Audit;

/**
 * Интерфейс для работы с аудитом.
 * Этот интерфейс предоставляет методы для записи действий пользователей в журнал аудита.
 */
public interface AuditService {
  /**
   * Записывает действие пользователя в журнал аудита.
   *
   * @param audit объект аудита с информацией о совершенном действии
   */
  void addAction(Audit audit);
}
