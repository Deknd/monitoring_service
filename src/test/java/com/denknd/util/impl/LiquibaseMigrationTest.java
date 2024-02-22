package com.denknd.util.impl;

import com.denknd.config.PostgresContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LiquibaseMigrationTest {
  @Test
  @DisplayName("Проверка правильности настройки миграций и работы матода миграции")
  void migration() {
    var postgresContainer = new PostgresContainer();
    postgresContainer.start();
    var liquibaseConfig = mock(LiquibaseConfig.class);
    when(liquibaseConfig.changelog()).thenReturn("db/changelog/changelog-master.xml");
    when(liquibaseConfig.defaultSchema()).thenReturn("public");
    when(liquibaseConfig.technicalSchema()).thenReturn("public");
    var liquibaseMigration = new LiquibaseMigration(postgresContainer.getDataBaseConnection(), liquibaseConfig);

    assertThatCode(()->liquibaseMigration.migration()).doesNotThrowAnyException();

    postgresContainer.stop();
  }
  @Test
  @DisplayName("Проверяет, что если путь на changelog указан не верно, выдаст ошибку и остановит программу")
  void migration_failed() {
    var postgresContainer = new PostgresContainer();
    postgresContainer.start();
    var liquibaseConfig = mock(LiquibaseConfigImpl.class);
    when(liquibaseConfig.changelog()).thenReturn("db/changelog/cha.xml");
    when(liquibaseConfig.defaultSchema()).thenReturn("public");
    when(liquibaseConfig.technicalSchema()).thenReturn("public");
    var liquibaseMigration = new LiquibaseMigration(postgresContainer.getDataBaseConnection(), liquibaseConfig);
    assertThatThrownBy(()->liquibaseMigration.migration()).isInstanceOf(RuntimeException.class);
    postgresContainer.stop();
  }
}