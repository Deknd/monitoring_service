package com.denknd.in.controllers;

import com.denknd.dto.AddressDto;
import com.denknd.exception.AddressDatabaseException;
import com.denknd.in.controllers.AddressController;
import com.denknd.in.controllers.ExceptionHandlerController;
import com.denknd.mappers.AddressMapper;
import com.denknd.security.service.SecurityService;
import com.denknd.services.AddressService;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AddressControllerTest {

  @Mock
  private AddressService addressService;
  @Mock
  private AddressMapper addressMapper;
  private AutoCloseable closeable;
  private ObjectMapper objectMapper;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
    var addressController = new AddressController(this.addressService, this.addressMapper);
    this.mockMvc = MockMvcBuilders.standaloneSetup(addressController).setControllerAdvice(new ExceptionHandlerController()).build();

  }

  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }

  @Test
  @DisplayName("Проверяет, что вызываются нужные сервисы, с нужными аргументами")
  void addAddress() throws Exception {
    var addressDto = AddressDto.builder()
            .region("region")
            .city("city")
            .street("street")
            .house("house")
            .apartment("2ap")
            .postalCode(123456L)
            .build();
    when(this.addressMapper.mapAddressToAddressDto(any())).thenReturn(mock(AddressDto.class));
    var json = this.objectMapper.writeValueAsString(addressDto);

    var mvcResult = this.mockMvc.perform(post("/address")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    assertThat(mvcResult.getResponse().getContentAsString()).isNotNull();
    verify(this.addressService, times(1)).addAddressByUser(any());
  }

  @Test
  @DisplayName("Проверяет, что при выкидывание сервисом ошибки AddressDatabaseException, срабатывает обработчик ошибок")
  void addAddress_AddressDatabaseException() throws Exception {
    var addressDto = AddressDto.builder()
            .region("region")
            .city("city")
            .street("street")
            .house("house")
            .apartment("2ap")
            .postalCode(123456L)
            .build();
    when(this.addressService.addAddressByUser(any())).thenThrow(new AddressDatabaseException("error"));
    when(this.addressMapper.mapAddressToAddressDto(any())).thenReturn(mock(AddressDto.class));
    var json = this.objectMapper.writeValueAsString(addressDto);

    var mvcResult = this.mockMvc.perform(post("/address")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn();

    assertThat(mvcResult.getResponse().getContentAsString()).isNotNull();
    verify(this.addressService, times(1)).addAddressByUser(any());
  }

  @Test
  @DisplayName("Проверяет, что при выкидывание сервисом ошибки AccessDeniedException, срабатывает обработчик ошибок")
  void addAddress_AccessDeniedException() throws Exception {
    var addressDto = AddressDto.builder()
            .region("region")
            .city("city")
            .street("street")
            .house("house")
            .apartment("2ap")
            .postalCode(123456L)
            .build();
    when(this.addressService.addAddressByUser(any())).thenThrow(new AccessDeniedException("error"));
    when(this.addressMapper.mapAddressToAddressDto(any())).thenReturn(mock(AddressDto.class));
    var json = this.objectMapper.writeValueAsString(addressDto);

    var mvcResult = this.mockMvc.perform(post("/address")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isForbidden())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn();

    assertThat(mvcResult.getResponse().getContentAsString()).isNotNull();
    verify(this.addressService, times(1)).addAddressByUser(any());
  }

  @Test
  @DisplayName("Проверяет, что вызываются нужные сервисы, с нужными аргументами")
  void getAddress() throws Exception {
    var userId = 1L;
    var mvcResult = this.mockMvc.perform(
                    get("/address")
                            .param("userId", String.valueOf(userId)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    assertThat(mvcResult.getResponse().getContentAsString()).isNotNull();
    verify(this.addressService, times(1)).getAddresses(Optional.of(userId));
  }
}