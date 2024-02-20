package com.denknd.in.controllers;

import com.denknd.config.AppConfig;
import com.denknd.config.SwaggerConfig;
import com.denknd.config.WebConfig;
import com.denknd.dto.MeterReadingRequestDto;
import com.denknd.entity.Parameters;
import com.denknd.exception.MeterReadingConflictError;
import com.denknd.in.controllers.ExceptionHandlerController;
import com.denknd.in.controllers.MeterReadingController;
import com.denknd.mappers.MeterReadingMapper;
import com.denknd.services.MeterReadingService;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.AccessDeniedException;
import java.time.YearMonth;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MeterReadingControllerTest {
  @Mock
  private MeterReadingService meterReadingService;
  @Mock
  private MeterReadingMapper meterReadingMapper;
  private AutoCloseable closeable;
  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);

    var meterReadingController = new MeterReadingController(
            this.meterReadingService,
            this.meterReadingMapper);

    this.mockMvc = MockMvcBuilders.standaloneSetup(meterReadingController)
            .setControllerAdvice(new ExceptionHandlerController())
            .build();
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
  }

  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }

  @Test
  @DisplayName("Проверяет, что метод вызывает все нужные сервисы с нужными параметрами")
  void getHistoryMeterReading() throws Exception {
    var addressId = 2L;
    var userId = 1L;
    var parameters = Set.of(1L, 2L);
    var startDate = YearMonth.now().minusMonths(5);
    var endDate = YearMonth.now().minusMonths(1);

    this.mockMvc.perform(get("/meter-readings/history")
                    .param("addrId", String.valueOf(addressId))
                    .param("userId", String.valueOf(userId))
                    .param("typeIds", parameters.stream().map(String::valueOf).collect(Collectors.joining(",")))
                    .param("start_date", startDate.toString())
                    .param("end_date", endDate.toString()))
            .andExpect(status().isOk());

    var argumentCapture = ArgumentCaptor.forClass(Parameters.class);
    verify(this.meterReadingService, times(1)).getHistoryMeterByAddress(argumentCapture.capture());
    var argumentCaptureValue = argumentCapture.getValue();
    assertThat(argumentCaptureValue.getAddressId()).isEqualTo(addressId);
    assertThat(argumentCaptureValue.getUserId()).isEqualTo(userId);
    assertThat(argumentCaptureValue.getTypeMeterIds()).isEqualTo(parameters);
    assertThat(argumentCaptureValue.getStartDate()).isEqualTo(startDate);
    assertThat(argumentCaptureValue.getEndDate()).isEqualTo(endDate);
  }

  @Test
  @DisplayName("Проверяет, что метод вызывает все сервисы и отправляет показания на обработку")
  void addMeterReadingValue() throws Exception {
    var meterReadingRequestDto = MeterReadingRequestDto.builder()
            .typeMeterId(1L)
            .addressId(1L)
            .meterValue(123313.324)
            .build();
    var json = this.objectMapper.writeValueAsString(meterReadingRequestDto);

    this.mockMvc.perform(post("/meter-readings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    verify(this.meterReadingService, times(1)).addMeterValue(any());
  }

  @Test
  @DisplayName("Проверяет, что метод вызывает все сервисы и обрабатывается ошибка MeterReadingConflictError" )
  void addMeterReadingValue_MeterReadingConflictError() throws Exception {
    var meterReadingRequestDto = MeterReadingRequestDto.builder()
            .typeMeterId(1L)
            .addressId(1L)
            .meterValue(123313.324)
            .build();
    var json = this.objectMapper.writeValueAsString(meterReadingRequestDto);
    when(this.meterReadingService.addMeterValue(any())).thenThrow(new MeterReadingConflictError("error"));

    this.mockMvc.perform(post("/meter-readings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn();

    verify(this.meterReadingService, times(1)).addMeterValue(any());
  }

  @Test
  @DisplayName("Проверяет, что метод вызывает все сервисы и обрабатывается ошибка AccessDeniedException")
  void addMeterReadingValue_AccessDeniedException() throws Exception {
    var meterReadingRequestDto = MeterReadingRequestDto.builder()
            .typeMeterId(1L)
            .addressId(1L)
            .meterValue(123313.324)
            .build();
    var json = this.objectMapper.writeValueAsString(meterReadingRequestDto);
    when(this.meterReadingService.addMeterValue(any())).thenThrow(new AccessDeniedException("error"));

    this.mockMvc.perform(post("/meter-readings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isForbidden())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn();

    verify(this.meterReadingService, times(1)).addMeterValue(any());
  }


  @Test
  @DisplayName("Проверяет, что метод вызывает все нужные сервисы")
  void getMeterReadings() throws Exception {
    var addressId = 1L;
    var userId = 2L;
    var types = Set.of(1L, 2L);
    var date = YearMonth.now();

    var mvcResult = this.mockMvc.perform(get("/meter-readings")
                    .param("addrId", String.valueOf(addressId))
                    .param("userId", String.valueOf(userId))
                    .param("typeIds", types.stream().map(String::valueOf).collect(Collectors.joining(",")))
                    .param("date", date.toString()))
            .andExpect(status().isOk())
            .andReturn();

    assertThat(mvcResult).isNotNull();
    var argumentCapture = ArgumentCaptor.forClass(Parameters.class);
    verify(this.meterReadingService, times(1)).getActualMeterByAddress(argumentCapture.capture());
    var parameters = argumentCapture.getValue();
    assertThat(parameters.getAddressId()).isEqualTo(addressId);
    assertThat(parameters.getUserId()).isEqualTo(userId);
    assertThat(parameters.getTypeMeterIds()).isEqualTo(types);
    assertThat(parameters.getDate()).isEqualTo(date);
  }

}