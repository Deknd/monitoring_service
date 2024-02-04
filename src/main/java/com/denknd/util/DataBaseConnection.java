package com.denknd.util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Интерфейс предоставляющий доступ к соединению с базой данных
 */
public interface DataBaseConnection {
  /**
   * Возвращает соединение с базой данных
   * @return соединение с базой данных
   * @throws SQLException ошибка соединения
   */
  Connection createConnection() throws SQLException;
}
