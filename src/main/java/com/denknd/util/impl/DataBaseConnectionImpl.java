package com.denknd.util.impl;

import com.denknd.util.DataBaseConnection;
import com.denknd.util.DbConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Реализация интерфейса для установки соединения с базой данных.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataBaseConnectionImpl implements DataBaseConnection {
  /**
   * Конфигурация для установки соединения с базой данных.
   */
  private final DbConfig dbConfig;

  /**
   * Создает соединение с базой данных на основе предоставленной конфигурации.
   *
   * @return Соединение с базой данных
   * @throws SQLException в случае возникновения ошибки при соединении с базой данных
   */
  @Override
  public Connection createConnection() throws SQLException {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      log.error("Не удалось загрузить драйвер postgresql");
      throw new RuntimeException(e);
    }
    var connection = DriverManager.getConnection(dbConfig.url(), dbConfig.username(), dbConfig.password());
    connection.setSchema(this.dbConfig.defaultSchema());
    return connection;
  }
}
