package com.denknd.util.impl;

import com.denknd.util.DataBaseConnection;
import com.denknd.util.DbConfig;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Создает соединение с базой данных.
 */
@RequiredArgsConstructor
public class DataBaseConnectionImpl implements DataBaseConnection {
  /**
   * Конфигурации для соединения
   */
  private final DbConfig dbConfig;

  /**
   * Создает соединение с базой данных
   *
   * @return Соединение с базой данных
   * @throws SQLException при ошибки соединения с базой данных
   */
  @Override
  public Connection createConnection() throws SQLException {
    var connection = DriverManager.getConnection(dbConfig.url(), dbConfig.username(), dbConfig.password());
    connection.setSchema(this.dbConfig.defaultSchema());
    return connection;
  }
}
