package com.denknd.services.impl;

import com.denknd.entity.Meter;
import com.denknd.repository.MeterCountRepository;
import com.denknd.services.MeterCountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MeterCountServiceImplTest {
  private AutoCloseable closeable;
  @Mock
  private MeterCountRepository meterCountRepository;
  private MeterCountService meterCountService;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.meterCountService = new MeterCountServiceImpl(this.meterCountRepository);
  }

  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }

  @Test
  @DisplayName("Проверяет, что метод вызывает нужный сервис и добавляет время регистрации")
  void saveMeterCount() throws SQLException {
    var meter = mock(Meter.class);

    this.meterCountService.saveMeterCount(meter);

    verify(meter, times(1)).setRegistrationDate(any());
    verify(this.meterCountRepository, times(1)).save(eq(meter));
  }

  @Test
  @DisplayName("Проверяет, что метод вызывает нужный сервис")
  void addInfoForMeterCount() throws SQLException {
    var meter = mock(Meter.class);

    this.meterCountService.addInfoForMeterCount(meter);

    verify(this.meterCountRepository, times(1)).update(eq(meter));
  }
}