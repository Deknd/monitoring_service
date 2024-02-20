package com.denknd.util;

import java.io.FileNotFoundException;

/**
 * Интерфейс для парсера yml файлов
 */
public interface YamlParser {
  /**
   * Возвращает конфигурации для ликвибаз
   *
   * @return конфигурации для ликвибаз
   * @throws FileNotFoundException когда конфигурации не найдены
   */
  LiquibaseConfig liquibaseConfig() throws FileNotFoundException;

  /**
   * Конфигурация базы данных
   *
   * @return Конфигурация базы данных
   * @throws FileNotFoundException когда нет доступа к файлу с конфигурациями
   */
  DbConfig dbConfig() throws FileNotFoundException;
  /**
   * Возвращает конфигурацию для JWT-токенов.
   *
   * @return Конфигурация для JWT-токенов.
   * @throws FileNotFoundException если файл с конфигурациями не найден
   */
  JwtConfig jwtConfig() throws FileNotFoundException;

  /**
   * Настройка пути к файлу с конфигурациями
   *
   * @param pathToApplicationYml путь к файлу с конфигурациями
   */
  void setPathToApplicationYml(String pathToApplicationYml);
}
