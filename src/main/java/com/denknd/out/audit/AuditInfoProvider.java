package com.denknd.out.audit;

/**
 * Интерфейс для объектов, которые предоставляют информацию для аудита.
 */
public interface AuditInfoProvider {
  /**
   * Получить команду, связанную с объектом для выполнения.
   *
   * @return Команда для выполнения.
   */
  String getCommand();

  /**
   * Получить краткое описание работы команды, связанной с объектом.
   *
   * @return Краткое описание работы команды.
   */
  String getAuditActionDescription();
}
