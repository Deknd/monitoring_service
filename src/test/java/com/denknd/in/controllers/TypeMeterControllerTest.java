package com.denknd.in.controllers;

import com.denknd.dto.TypeMeterDto;
import com.denknd.exception.TypeMeterAdditionException;
import com.denknd.in.controllers.ExceptionHandlerController;
import com.denknd.in.controllers.TypeMeterController;
import com.denknd.mappers.TypeMeterMapper;
import com.denknd.services.TypeMeterService;
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

import java.nio.file.AccessDeniedException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TypeMeterControllerTest {
  @Mock
  private TypeMeterService typeMeterService;
  @Mock
  private TypeMeterMapper typeMeterMapper;
  private AutoCloseable closeable;
  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    var typeMeterController = new TypeMeterController(this.typeMeterService, this.typeMeterMapper);
    this.mockMvc = MockMvcBuilders
            .standaloneSetup(typeMeterController)
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
  @DisplayName("Проверяет, что метод обращается в нужный сервис")
  void addNewType() throws Exception {
    var typeMeterDto = TypeMeterDto.builder()
            .typeCode("typeCode")
            .typeDescription("typeDescription")
            .metric("metric")
            .build();
    var json = this.objectMapper.writeValueAsString(typeMeterDto);

    this.mockMvc.perform(post("/meter-types")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    verify(this.typeMeterService, times(1)).addNewTypeMeter(any());
  }

  @Test
  @DisplayName("Проверяет, что метод обращается в нужный сервис и обрабатывается ошибка TypeMeterAdditionException")
  void addNewType_TypeMeterAdditionException() throws Exception {
    var typeMeterDto = TypeMeterDto.builder()
            .typeCode("typeCode")
            .typeDescription("typeDescription")
            .metric("metric")
            .build();
    var json = this.objectMapper.writeValueAsString(typeMeterDto);
    when(this.typeMeterService.addNewTypeMeter(any())).thenThrow(new TypeMeterAdditionException("error"));

    this.mockMvc.perform(post("/meter-types")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

    verify(this.typeMeterService, times(1)).addNewTypeMeter(any());
  }

  @Test
  @DisplayName("Проверяет, что метод обращается в нужный сервис и обрабатывается ошибка AccessDeniedException")
  void addNewType_AccessDeniedException() throws Exception {
    var typeMeterDto = TypeMeterDto.builder()
            .typeCode("typeCode")
            .typeDescription("typeDescription")
            .metric("metric")
            .build();
    var json = this.objectMapper.writeValueAsString(typeMeterDto);
    when(this.typeMeterService.addNewTypeMeter(any())).thenThrow(new AccessDeniedException("error"));

    this.mockMvc.perform(post("/meter-types")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isForbidden())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

    verify(this.typeMeterService, times(1)).addNewTypeMeter(any());
  }

  @Test
  @DisplayName("Проверяет, что метод обращается в нужный сервис")
  void getTypeMeterCodes() throws Exception {

    this.mockMvc.perform(get("/meter-types"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    verify(this.typeMeterService, times(1)).getTypeMeter();
  }
}