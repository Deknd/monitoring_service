package com.denknd.repository.impl;

import com.denknd.config.ContainerConfig;
import com.denknd.entity.Meter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {ContainerConfig.class})
@ActiveProfiles("test")
class PostgresMeterCountRepositoryTest {

  @Autowired
  private PostgresMeterCountRepository meterCountRepository;

  @Test
  @DisplayName("Проверяет, что сохраняется в БД и выдается идентификатор")
  void save() throws SQLException {
    var meter = Meter.builder()
            .addressId(2L)
            .typeMeterId(3L)
            .registrationDate(OffsetDateTime.now().minus(2, ChronoUnit.MONTHS))
            .build();

    var save = this.meterCountRepository.save(meter);

    assertThat(save.getMeterCountId()).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, что добавляются данные без ошибки")
  void update() {
    var meter = Meter.builder()
            .typeMeterId(1L)
            .addressId(1L)
            .serialNumber("serialNumber")
            .lastCheckDate(OffsetDateTime.now())
            .meterModel("meterModel")
            .build();

    assertThatCode(() -> this.meterCountRepository.update(meter)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Проверяет, что без основных данных(addressId, typeMeterId) выкидывает ошибку")
  void update_noAddressId() {
    var meter = Meter.builder()
            .typeMeterId(1L)
            .serialNumber("serialNumber")
            .lastCheckDate(OffsetDateTime.now())
            .meterModel("meterModel")
            .build();

    assertThatThrownBy(() -> this.meterCountRepository.update(meter)).isInstanceOf(SQLException.class);
  }
}