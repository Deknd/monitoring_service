package com.denknd.util;

/**
 * Интерфейс конфигураций базы данных
 */
public interface DbConfig {
  /**
   * Возвращает логин для БД
   *
   * @return логин от бд
   */
  String username();

  /**
   * Возвращает пароль от БД
   *
   * @return пароль от БД
   */
  String password();

  /**
   * Возвращает урл от БД
   *
   * @return урл БД
   */
  String url();

  /**
   * Возвращает дефолтную схему БД
   *
   * @return дефолтная схема
   */
  String defaultSchema();
}
