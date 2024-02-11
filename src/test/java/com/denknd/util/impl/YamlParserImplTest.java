package com.denknd.util.impl;

import com.denknd.util.YamlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class YamlParserImplTest {

  private YamlParser yamlParser;
  @BeforeEach
  void setUp() {
    this.yamlParser = new YamlParserImpl();
  }

  @Test
  @DisplayName("Проверяет, что парсится yml файл со всеми основными настройками")
  void parseYaml() throws FileNotFoundException {
    this.yamlParser.setPathToApplicationYml("src/test/resources/application-test.yml");
    var dbConfig = this.yamlParser.dbConfig();
    assertThat(dbConfig.username()).isEqualTo("testUserDb");
    assertThat(dbConfig.password()).isEqualTo("testPasswordDb");
    assertThat(dbConfig.url()).isEqualTo("jdbc:test://test:5432/test");
    assertThat(dbConfig.defaultSchema()).isEqualTo("default_schema");
    var liquibaseConfig = this.yamlParser.liquibaseConfig();
    assertThat(liquibaseConfig.changelog()).isEqualTo("src/test/resources/db/changelog/changelog-test.xml");
    assertThat(liquibaseConfig.defaultSchema()).isEqualTo("default_schema");
    assertThat(liquibaseConfig.technicalSchema()).isEqualTo("technical_schema");
  }
}