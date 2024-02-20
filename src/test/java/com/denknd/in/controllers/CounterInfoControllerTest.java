package com.denknd.in.controllers;

import com.denknd.config.AppConfig;
import com.denknd.config.SwaggerConfig;
import com.denknd.config.WebConfig;
import com.denknd.dto.CounterInfoDto;
import com.denknd.in.controllers.CounterInfoController;
import com.denknd.in.controllers.ExceptionHandlerController;
import com.denknd.mappers.MeterCountMapper;
import com.denknd.services.MeterCountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class CounterInfoControllerTest {
  private AutoCloseable closeable;
  @Mock
  private MeterCountService meterCountService;
  @Mock
  private MeterCountMapper meterCountMapper;

  private ObjectMapper objectMapper;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    var counterInfoController = new CounterInfoController(this.meterCountService, this.meterCountMapper);
    var rootContext = new AnnotationConfigWebApplicationContext();
    rootContext.register(AppConfig.class, WebConfig.class, SwaggerConfig.class);

    this.mockMvc = MockMvcBuilders
            .standaloneSetup(counterInfoController)
            .setControllerAdvice(new ExceptionHandlerController())
            .build();
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new ParameterNamesModule());

  }

  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }

  @Test
  @DisplayName("Проверяет, что вызывается нужный сервис")
  void addInfoForMeter() throws Exception {
    var counterInfoDto = CounterInfoDto.builder()
            .addressId(1L)
            .typeMeterId(1L)
            .serialNumber("serialNumber")
            .meterModel("meterModel")
            .build();
    var json = this.objectMapper.writeValueAsString(counterInfoDto);

    var mvcResult = this.mockMvc.perform(post("/counter-info")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    assertThat(mvcResult).isNotNull();
    verify(this.meterCountService, times(1)).addInfoForMeterCount(any());

  }

  @Test
  @DisplayName("Проверяет, что обрабатывается ошибка SQLException")
  void addInfoForMeter_SQLException() throws Exception {
    var counterInfoDto = CounterInfoDto.builder()
            .addressId(1L)
            .typeMeterId(1L)
            .serialNumber("serialNumber")
            .meterModel("meterModel")
            .build();
    var json = this.objectMapper.writeValueAsString(counterInfoDto);
    when(this.meterCountService.addInfoForMeterCount(any())).thenThrow(new SQLException("error"));

    var mvcResult = this.mockMvc.perform(post("/counter-info")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn();

    assertThat(mvcResult).isNotNull();
    verify(this.meterCountService, times(1)).addInfoForMeterCount(any());
  }

  @Test
  @DisplayName("Проверяет, что обрабатывается ошибка AccessDeniedException")
  void addInfoForMeter_AccessDeniedException() throws Exception {
    var counterInfoDto = CounterInfoDto.builder()
            .addressId(1L)
            .typeMeterId(1L)
            .serialNumber("serialNumber")
            .meterModel("meterModel")
            .build();
    var json = this.objectMapper.writeValueAsString(counterInfoDto);
    when(this.meterCountService.addInfoForMeterCount(any())).thenThrow(new AccessDeniedException("error"));

    var mvcResult = this.mockMvc.perform(post("/counter-info")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isForbidden())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn();

    assertThat(mvcResult).isNotNull();
    verify(this.meterCountService, times(1)).addInfoForMeterCount(any());
  }
}