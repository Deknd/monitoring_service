package com.denknd.util.impl;

import com.denknd.util.DbConfig;
import com.denknd.util.JwtConfig;
import com.denknd.util.LiquibaseConfig;
import com.denknd.util.YamlParser;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Реализация интерфейса парсера YAML файлов.
 */
@Component
public class YamlParserImpl implements YamlParser {
  /**
   * Путь к файлу с конфигурацией приложения YAML.
   */
  private String pathToApplicationYml = "src/main/resources/application.yml";
  /**
   * Конфигурация Liquibase.
   */
  private LiquibaseConfigImpl liquibaseConfig;
  /**
   * Конфигурация базы данных
   */
  private DbConfigImpl dbConfig;
  /**
   * Конфиг для создания токенов
   */
  private JwtConfig jwtConfig;


  /**
   * Метод для парсинга данных из файла конфигурации приложения YAML.
   *
   * @throws FileNotFoundException если файл не найден
   */
  private void parseYaml() throws FileNotFoundException {
    try {
      var yaml = new Yaml();
      var fileInputStream = new FileInputStream(this.pathToApplicationYml);

      var yamlConfig = yaml.loadAs(fileInputStream, YamlConfigImpl.class);
      this.liquibaseConfig = yamlConfig.getLiquibase();
      this.dbConfig = yamlConfig.getDb();
      this.dbConfig.setDefault_schema(yamlConfig.getLiquibase().defaultSchema());
      this.jwtConfig = yamlConfig.getJwt();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.out.println("Не установлены основные настройки приложения");
      throw e;
    }
  }

  /**
   * Получение конфигурации Liquibase из файла YAML.
   *
   * @return Конфигурация Liquibase
   * @throws FileNotFoundException если файл не найден
   */
  @Override
  public LiquibaseConfig liquibaseConfig() throws FileNotFoundException {
    if (this.liquibaseConfig == null) {
      parseYaml();
    }
    return this.liquibaseConfig;
  }

  /**
   * Получение конфигурации базы данных из файла YAML.
   *
   * @return Конфигурация базы данных
   * @throws FileNotFoundException если файл не найден
   */
  @Override
  public DbConfig dbConfig() throws FileNotFoundException {
    if (this.dbConfig == null) {
      parseYaml();
    }
    return this.dbConfig;
  }

  /**
   * Получение конфигурации JWT токенов из файла YAML.
   *
   * @return Конфигурация JWT токенов
   * @throws FileNotFoundException если файл не найден
   */
  @Override
  public JwtConfig jwtConfig() throws FileNotFoundException {
    if (this.jwtConfig == null) {
      parseYaml();
    }
    return this.jwtConfig;
  }

  /**
   * Установка пути к файлу с конфигурацией приложения YAML.
   *
   * @param pathToApplicationYml путь к файлу YAML
   */
  @Override
  public void setPathToApplicationYml(String pathToApplicationYml) {
    this.pathToApplicationYml = pathToApplicationYml;
  }
}
