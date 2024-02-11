package com.denknd.util;

/**
 * Интерфейс для запуска миграций базы данных
 */
public interface MigrationBd {
  /**
   * Запуск миграций бд
   */
  void migration();
}
