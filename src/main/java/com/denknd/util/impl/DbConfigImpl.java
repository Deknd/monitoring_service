package com.denknd.util.impl;

import com.denknd.util.DbConfig;
import lombok.Setter;

/**
 * Представляет класс Конфигурации для базы данных
 */
@Setter
public class DbConfigImpl implements DbConfig {
  /**
   * Логин для соединения с базой данных
   */
  private String username;
  /**
   * Пароль для соединения с базой данных
   */
  private String password;
  /**
   * Урл базы данных
   */
  private String url;
  /**
   * Схема дефолтная
   */
  private String default_schema;

  /**
   * Возвращает логин для БД
   * @return логин от бд
   */
  @Override
  public String username() {
    return this.username;
  }

  /**
   * Возвращает пароль от БД
   * @return пароль от БД
   */
  @Override
  public String password() {
    return this.password;
  }

  /**
   * Возвращает урл от БД
   * @return урл БД
   */
  @Override
  public String url() {
    return this.url;
  }

  /**
   * Возвращает дефолтную схему БД
   * @return дефолтная схема
   */
  @Override
  public String defaultSchema() {
    return this.default_schema;
  }
}
