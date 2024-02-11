package com.denknd.controllers;

import com.denknd.dto.CounterInfoDto;
import com.denknd.entity.Meter;
import com.denknd.entity.TypeMeter;
import com.denknd.mappers.MeterCountMapper;
import com.denknd.services.MeterCountService;
import com.denknd.services.TypeMeterService;
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
import static org.mockito.Mockito.when;


class CounterInfoControllerTest {
  private CounterInfoController counterInfoController;
  private AutoCloseable closeable;
  @Mock
  private MeterCountService meterCountService;
  @Mock
  private MeterCountMapper meterCountMapper;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.counterInfoController = new CounterInfoController(meterCountService, meterCountMapper);
  }
  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }
  @Test
  @DisplayName("Проверяет, что вызывается нужный сервис")
  void addInfoForMeter() throws SQLException {
    var counterInfoDto = mock(CounterInfoDto.class);
    when(this.meterCountMapper.mapCounterInfoDtoToMeter(eq(counterInfoDto))).thenReturn(mock(Meter.class));

    this.counterInfoController.addInfoForMeter(counterInfoDto);

    verify(this.meterCountService, times(1)).addInfoForMeterCount(any());
  }
}