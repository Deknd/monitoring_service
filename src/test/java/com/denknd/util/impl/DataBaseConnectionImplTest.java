//package com.denknd.util.impl;
//
//import com.denknd.config.PostgresContainer;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.sql.SQLException;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class DataBaseConnectionImplTest {
//
//  private DataBaseConnection dataBaseConnection;
//  private PostgresContainer postgresContainer;
//
//  @BeforeEach
//  void setUp() {
//    this.postgresContainer = new PostgresContainer();
//    this.postgresContainer.start();
//    this.dataBaseConnection = new DataBaseConnectionImpl(this.postgresContainer.getDbConfig());
//  }
//
//  @AfterEach
//  void tearDown() {
//    this.postgresContainer.stop();
//  }
//
//  @Test
//  @DisplayName("Проверка, что устанавливается соединение с базой данных")
//  void createConnection() throws SQLException {
//    var connection = dataBaseConnection.createConnection();
//    assertThat(connection).isNotNull();
//    connection.close();
//
//  }
//}