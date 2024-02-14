package com.denknd.util.impl;

import com.denknd.util.DbConfig;
import com.denknd.util.JwtConfig;
import com.denknd.util.LiquibaseConfig;
import com.denknd.util.YamlParser;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Реализация парсера yml файла
 */
public class YamlParserImpl implements YamlParser {
  /**
   * Путь к конфигурациям приложения
   */
  private String pathToApplicationYml = "src/main/resources/application.yml";
  /**
   * Конфигурация ликвибаз
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
   * Метод для парсинга данных из конфигураций приложения
   *
   * @throws FileNotFoundException выкидывается, когда файл не обноружен
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
   * Конфигурация ликвибаз
   *
   * @return Конфигурация ликвибаз
   * @throws FileNotFoundException когда нет доступа к файлу с конфигурациями
   */
  @Override
  public LiquibaseConfig liquibaseConfig() throws FileNotFoundException {
    if (this.liquibaseConfig == null) {
      parseYaml();
    }
    return this.liquibaseConfig;
  }

  /**
   * Конфигурация базы данных
   *
   * @return Конфигурация базы данных
   * @throws FileNotFoundException когда нет доступа к файлу с конфигурациями
   */
  @Override
  public DbConfig dbConfig() throws FileNotFoundException {
    if (this.dbConfig == null) {
      parseYaml();
    }
    return this.dbConfig;
  }

  /**
   * Конфигурация для Jwt токенов
   *
   * @return конфиг для токенов
   */
  @Override
  public JwtConfig jwtConfig() throws FileNotFoundException {
    if (this.jwtConfig == null) {
      parseYaml();
    }
    return this.jwtConfig;
  }

  /**
   * Настройка пути к файлу с конфигурациями
   *
   * @param pathToApplicationYml путь к файлу с конфигурациями
   */
  @Override
  public void setPathToApplicationYml(String pathToApplicationYml) {
    this.pathToApplicationYml = pathToApplicationYml;
  }
}
