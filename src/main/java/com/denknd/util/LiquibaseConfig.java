package com.denknd.util;

/**
 * Интерфейс конфигураций ликвибаз
 */
public interface LiquibaseConfig {
  /**
   * Возвращает название технической схемы
   *
   * @return техническая схема
   */
  String technicalSchema();

  /**
   * Возвращает название дефолтной схемы
   *
   * @return дефолтная схема
   */
  String defaultSchema();

  /**
   * Возвращает Путь к скриптам миграции
   *
   * @return Путь к скриптам миграции
   */
  String changelog();
}
