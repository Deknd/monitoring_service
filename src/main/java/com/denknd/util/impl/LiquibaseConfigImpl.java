package com.denknd.util.impl;

import com.denknd.util.LiquibaseConfig;
import lombok.Setter;

/**
 * Представляет реализацию конфигураций для миграции базы данных
 */
@Setter
public class LiquibaseConfigImpl implements LiquibaseConfig {
  /**
   * Техническая схема
   */
  private String technical_schema;
  /**
   * дефолтная схема
   */
  private String default_schema;
  /**
   * Путь к скриптам миграции
   */
  private String changelog;

  /**
   * Возвращает название технической схемы
   * @return техническая схема
   */
  @Override
  public String technicalSchema() {
    return this.technical_schema;
  }

  /**
   * Возвращает название дефолтной схемы
   * @return дефолтная схема
   */
  @Override
  public String defaultSchema() {
    return this.default_schema;
  }

  /**
   * Возвращает Путь к скриптам миграции
   * @return Путь к скриптам миграции
   */
  @Override
  public String changelog() {
    return this.changelog;
  }
}
