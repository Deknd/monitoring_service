package com.denknd.in.controllers;

import com.denknd.config.TestConfig;
import com.denknd.dto.TypeMeterDto;
import com.denknd.exception.AccessDeniedException;
import com.denknd.exception.TypeMeterAdditionException;
import com.denknd.mappers.TypeMeterMapper;
import com.denknd.services.TypeMeterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {TypeMeterController.class})
@AutoConfigureMockMvc
@SpringJUnitConfig(TestConfig.class)
class TypeMeterControllerTest {
  @Autowired
  private TypeMeterService typeMeterService;
  @MockBean
  private TypeMeterMapper typeMeterMapper;
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;


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
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

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
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));

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
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));

    verify(this.typeMeterService, times(1)).addNewTypeMeter(any());
  }

  @Test
  @DisplayName("Проверяет, что метод обращается в нужный сервис")
  void getTypeMeterCodes() throws Exception {

    this.mockMvc.perform(get("/meter-types"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    verify(this.typeMeterService, times(1)).getTypeMeter();
  }
}