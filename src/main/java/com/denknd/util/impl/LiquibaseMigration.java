package com.denknd.util.impl;

import com.denknd.util.DataBaseConnection;
import com.denknd.util.LiquibaseConfig;
import com.denknd.util.MigrationBd;
import liquibase.changelog.ChangeLogParameters;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DatabaseChangelogCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionArgumentsCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CommandExecutionException;
import liquibase.exception.DatabaseException;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Реализация Класса отвечающего за запуск скриптов миграций
 */
@RequiredArgsConstructor
public class LiquibaseMigration implements MigrationBd {
  /**
   * Соединение с базой данных
   */
  private final DataBaseConnection dataBaseConnection;
  /**
   * Конфигурация ликвибаз
   */
  private final LiquibaseConfig liquibaseConfig;

  /**
   * Запуск скриптов миграции
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
              .execute();

    } catch (SQLException | DatabaseException | CommandExecutionException e) {
      e.printStackTrace();
      System.out.println("Ошибка миграций БД. Работа приложения остановлена. База данных находится не в валидном состоянии.");
      throw new RuntimeException();
    }
  }


}
