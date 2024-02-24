//package com.denknd.repository.impl;
//
//import com.denknd.entity.TypeMeter;
//import com.denknd.mappers.TypeMeterMapper;
//import com.denknd.repository.TestContainer;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.sql.SQLException;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//class PostgresTypeMeterRepositoryTest extends TestContainer {
//  private PostgresTypeMeterRepository postgresTypeMeterRepository;
//  private AutoCloseable closeable;
//
//  @Mock
//  private TypeMeterMapper typeMeterMapper;
//
//  @BeforeEach
//  void setUp() {
//    this.closeable = MockitoAnnotations.openMocks(this);
//
//    this.postgresTypeMeterRepository = new PostgresTypeMeterRepository(postgresContainer.getDataBaseConnection(), this.typeMeterMapper);
//  }
//
//  @AfterEach
//  void tearDown() throws Exception {
//    this.closeable.close();
//  }
//
//  @Test
//  @DisplayName("Проверяет, что по дефолту содержаться данные в репозитории")
//  void findTypeMeter() {
//    var typeMeter = this.postgresTypeMeterRepository.findTypeMeter();
//
//    assertThat(typeMeter).isNotEmpty();
//  }
//
//  @Test
//  @DisplayName("Проверет, что объекту назначается айди и возвращается полностью собранный объект")
//  void save() throws SQLException {
//    var typeMeter = TypeMeter.builder()
//            .typeDescription("description")
//            .typeCode("test")
//            .metric("ball").build();
//
//    var save = this.postgresTypeMeterRepository.save(typeMeter);
//
//    assertThat(save).hasNoNullFieldsOrProperties();
//  }
//
//  @Test
//  @DisplayName("Проверяет, что если у объекта уже есть айди, он не сохраниться")
//  void save_notNew() {
//    var typeMeter = TypeMeter.builder()
//            .typeMeterId(123L)
//            .typeDescription("description")
//            .typeCode("test")
//            .metric("ball").build();
//
//    assertThatThrownBy(() -> this.postgresTypeMeterRepository.save(typeMeter)).isInstanceOf(SQLException.class);
//
//  }
//
//  @Test
//  @DisplayName("Проверяет, что если у объекта слишком длинный typeCode выдается ошибка")
//  void save_longTypeCode() {
//    var typeMeter = TypeMeter.builder()
//            .typeDescription("description")
//            .typeCode(generateRandomLogin(11))
//            .metric("m3").build();
//
//    assertThatThrownBy(() -> this.postgresTypeMeterRepository.save(typeMeter)).isInstanceOf(SQLException.class);
//
//  }
//
//  @Test
//  @DisplayName("Проверяет, что если у объекта слишком длинный metric выдается ошибка")
//  void save_longMetric() {
//    var typeMeter = TypeMeter.builder()
//            .typeDescription("description")
//            .typeCode("typeCode")
//            .metric(generateRandomLogin(11)).build();
//
//    assertThatThrownBy(() -> this.postgresTypeMeterRepository.save(typeMeter)).isInstanceOf(SQLException.class);
//
//  }
//}