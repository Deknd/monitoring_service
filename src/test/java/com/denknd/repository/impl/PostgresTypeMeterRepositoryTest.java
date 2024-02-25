package com.denknd.repository.impl;

import com.denknd.config.ContainerConfig;
import com.denknd.entity.TypeMeter;
import com.denknd.mappers.TypeMeterMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {ContainerConfig.class})
@ActiveProfiles("test")
class PostgresTypeMeterRepositoryTest{
  @Autowired
  private PostgresTypeMeterRepository postgresTypeMeterRepository;
  
  @Test
  @DisplayName("Проверяет, что по дефолту содержаться данные в репозитории")
  void findTypeMeter() {
    var typeMeter = this.postgresTypeMeterRepository.findTypeMeter();

    assertThat(typeMeter).isNotEmpty();
  }

  @Test
  @DisplayName("Проверет, что объекту назначается айди и возвращается полностью собранный объект")
  void save() throws SQLException {
    var typeMeter = TypeMeter.builder()
            .typeDescription("description")
            .typeCode("test")
            .metric("ball").build();

    var save = this.postgresTypeMeterRepository.save(typeMeter);

    assertThat(save).hasNoNullFieldsOrProperties();
  }

  @Test
  @DisplayName("Проверяет, что если у объекта уже есть айди, он не сохраниться")
  void save_notNew() {
    var typeMeter = TypeMeter.builder()
            .typeMeterId(123L)
            .typeDescription("description")
            .typeCode("test")
            .metric("ball").build();

    assertThatThrownBy(() -> this.postgresTypeMeterRepository.save(typeMeter)).isInstanceOf(SQLException.class);

  }

}