package com.denknd.config;

import com.denknd.util.DataBaseConnection;
import com.denknd.util.DbConfig;
import com.denknd.util.impl.DataBaseConnectionImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class PostgresContainer {
  private final String username = "test_container";
  private final String password = "password_test";
  private final String db = "test_db";
  private final PostgreSQLContainer<?> postgreSQLContainer;
  private String url = null;


  public PostgresContainer() {
    this.postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0")
            .withUsername(this.username)
            .withPassword(this.password)
            .withDatabaseName(this.db);
  }

  public void start() {
    this.postgreSQLContainer.start();
    this.url = this.postgreSQLContainer.getJdbcUrl();
  }

  public void stop() {
    this.postgreSQLContainer.stop();
  }

  public DbConfig getDbConfig() {
    var dbConfig = mock(DbConfig.class);
    when(dbConfig.username()).thenReturn(username);
    when(dbConfig.password()).thenReturn(password);
    when(dbConfig.url()).thenReturn(url);
    return dbConfig;
  }

  public DataBaseConnection getDataBaseConnection() {
    return new DataBaseConnectionImpl(this.getDbConfig());
  }

  @Test
  @DisplayName("Проверка запуска контейнера")
  void test() {
    new PostgresContainer();
    start();
    stop();
  }
}
