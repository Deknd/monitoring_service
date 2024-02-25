package com.denknd.in.controllers;

import com.denknd.config.TestConfig;
import com.denknd.dto.CounterInfoDto;
import com.denknd.exception.AccessDeniedException;
import com.denknd.mappers.MeterCountMapper;
import com.denknd.services.MeterCountService;
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


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {CounterInfoController.class})
@AutoConfigureMockMvc
@SpringJUnitConfig(TestConfig.class)
class CounterInfoControllerTest {
  @MockBean
  private MeterCountService meterCountService;
  @MockBean
  private MeterCountMapper meterCountMapper;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("Проверяет, что вызывается нужный сервис")
  void addInfoForMeter() throws Exception {
    var counterInfoDto = CounterInfoDto.builder()
            .addressId(1L)
            .typeMeterId(1L)
            .serialNumber("serialNumbfbcner")
            .meterModel("meterMocvnbvdel")
            .build();
    var json = this.objectMapper.writeValueAsString(counterInfoDto);
    System.out.println(json);
    var mvcResult = this.mockMvc.perform(put("/counter-info")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
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

    var mvcResult = this.mockMvc.perform(put("/counter-info")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isForbidden())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn();

    assertThat(mvcResult).isNotNull();
    verify(this.meterCountService, times(1)).addInfoForMeterCount(any());
  }
}