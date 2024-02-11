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
   * @param consoleCommandMap   карта команд консоли
   * @param commandAndParam     команда и параметры
   * @param activeUser          активный пользователь
   */
//  void addAction(Map<String, ConsoleCommand> consoleCommandMap, String commandAndParam, UserSecurity activeUser);
}
