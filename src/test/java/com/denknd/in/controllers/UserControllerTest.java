package com.denknd.in.controllers;

import com.denknd.dto.UserCreateDto;
import com.denknd.entity.Parameters;
import com.denknd.exception.InvalidUserDataException;
import com.denknd.exception.UserAlreadyExistsException;
import com.denknd.in.controllers.ExceptionHandlerController;
import com.denknd.in.controllers.UserController;
import com.denknd.mappers.UserMapper;
import com.denknd.services.UserService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.AccessDeniedException;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {
  @Mock
  private UserService userService;
  @Mock
  private UserMapper userMapper;
  private AutoCloseable closeable;
  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    var userController = new UserController(this.userService, this.userMapper);
    this.mockMvc = MockMvcBuilders
            .standaloneSetup(userController)
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
  @DisplayName("Проверяет, что метод вызывает все сервисы")
  void createUser() throws Exception {
    var userCreateDto = UserCreateDto.builder()
            .email("email")
            .password("password")
            .lastName("lastName")
            .firstName("firstName")
            .build();
    var json = this.objectMapper.writeValueAsString(userCreateDto);

    this.mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    verify(this.userService, times(1)).registrationUser(any());
  }

  @Test
  @DisplayName("Проверяет, что метод вызывает все сервисы и обрабатывает ошибку UserAlreadyExistsException")
  void createUser_UserAlreadyExistsException() throws Exception {
    var userCreateDto = UserCreateDto.builder()
            .email("email")
            .password("password")
            .lastName("lastName")
            .firstName("firstName")
            .build();
    var json = this.objectMapper.writeValueAsString(userCreateDto);
    when(this.userService.registrationUser(any())).thenThrow(new UserAlreadyExistsException("error"));

    this.mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

    verify(this.userService, times(1)).registrationUser(any());
  }

  @Test
  @DisplayName("Проверяет, что метод вызывает все сервисы и обрабатывает ошибку NoSuchAlgorithmException")
  void createUser_NoSuchAlgorithmException() throws Exception {
    var userCreateDto = UserCreateDto.builder()
            .email("email")
            .password("password")
            .lastName("lastName")
            .firstName("firstName")
            .build();
    var json = this.objectMapper.writeValueAsString(userCreateDto);
    when(this.userService.registrationUser(any())).thenThrow(new NoSuchAlgorithmException("error"));

    this.mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isInternalServerError())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

    verify(this.userService, times(1)).registrationUser(any());
  }

  @Test
  @DisplayName("Проверяет, что метод вызывает все сервисы и обрабатывает ошибку InvalidUserDataException")
  void createUser_InvalidUserDataException() throws Exception {
    var userCreateDto = UserCreateDto.builder()
            .email("email")
            .password("password")
            .lastName("lastName")
            .firstName("firstName")
            .build();
    var json = this.objectMapper.writeValueAsString(userCreateDto);
    when(this.userService.registrationUser(any())).thenThrow(new InvalidUserDataException("error"));

    this.mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

    verify(this.userService, times(1)).registrationUser(any());
  }

  @Test
  @DisplayName("Проверяет, что метод вызывает все сервисы и обрабатывает ошибку AccessDeniedException")
  void createUser_AccessDeniedException() throws Exception {
    var userCreateDto = UserCreateDto.builder()
            .email("email")
            .password("password")
            .lastName("lastName")
            .firstName("firstName")
            .build();
    var json = this.objectMapper.writeValueAsString(userCreateDto);
    when(this.userService.registrationUser(any())).thenThrow(new AccessDeniedException("error"));

    this.mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isForbidden())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

    verify(this.userService, times(1)).registrationUser(any());
  }

  @Test
  @DisplayName("Проверяет, что вызывается сервисы с нужными параметрами")
  void getUser() throws Exception {
    var userId = 1L;
    var email = "email";

    this.mockMvc.perform(get("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("id", String.valueOf(userId))
                    .param("email", email))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    var argumentCapture = ArgumentCaptor.forClass(Parameters.class);
    verify(this.userService, times(1)).getUser(argumentCapture.capture());
    var parameters = argumentCapture.getValue();
    assertThat(parameters.getUserId()).isEqualTo(userId);
    assertThat(parameters.getEmail()).isEqualTo(email);
  }

}