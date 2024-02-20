package com.denknd.util.impl;

import com.denknd.util.DataBaseConnection;
import com.denknd.util.LiquibaseConfig;
import com.denknd.util.MigrationBd;
import liquibase.changelog.ChangeLogParameters;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DatabaseChangelogCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionArgumentsCommandStep;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CommandExecutionException;
import liquibase.exception.DatabaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * Реализация интерфейса для запуска скриптов миграции базы данных с использованием Liquibase.
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class LiquibaseMigration implements MigrationBd {

  /**
   * Соединение с базой данных.
   */
  private final DataBaseConnection dataBaseConnection;

  /**
   * Конфигурация Liquibase.
   */
  private final LiquibaseConfig liquibaseConfig;

  /**
   * Запускает скрипты миграции базы данных.
   */
  @Override
  public void migration() {
    try (
            var connection = dataBaseConnection.createConnection();
            var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection))
    ) {
      database.setDefaultSchemaName(liquibaseConfig.defaultSchema());
      database.setLiquibaseSchemaName(liquibaseConfig.technicalSchema());
      var changeLogParameters = new ChangeLogParameters(database);
      changeLogParameters.set("defaultSchema", liquibaseConfig.defaultSchema());
      new CommandScope(UpdateCommandStep.COMMAND_NAME)
              .addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, changeLogParameters)
              .addArgumentValue(DbUrlConnectionArgumentsCommandStep.DEFAULT_SCHEMA_NAME_ARG, liquibaseConfig.defaultSchema())
              .addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, liquibaseConfig.changelog())
              .addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, database)
              .setOutput(new LoggerOutputStream())
              .execute();
    } catch (SQLException | DatabaseException | CommandExecutionException e) {
      log.error("Ошибка при выполнении миграций базы данных. Работа приложения приостановлена. База данных находится в невалидном состоянии.", e);
      throw new RuntimeException("Ошибка при выполнении миграций базы данных.", e);
    }
  }

  /**
   * Пользовательский вывод логов в консоль с использованием SLF4J.
   */
  private static class LoggerOutputStream extends OutputStream {
    private final StringBuilder buffer = new StringBuilder();

    @Override
    public void write(int b) {
      if (b == '\n') {
        log.info(buffer.toString());
        buffer.setLength(0);
      } else {
        buffer.append((char) b);
      }
    }
  }
}
