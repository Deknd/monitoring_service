package com.denknd.util.impl;

import lombok.Getter;
import lombok.Setter;

/**
 * Класс использующийся для парсинга yml файла
 */
@Getter
@Setter
public class YamlConfigImpl {
  /**
   * Конфигурация базы данных
   */
  private DbConfigImpl db;
  /**
   * Конфигурация ликвибаз
   */
  private LiquibaseConfigImpl liquibase;
  /**
   * Конфигурация для шифрования и дешифрования токенов
   */
  private JwtConfigImpl jwt;

}
