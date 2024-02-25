package com.denknd.in.controllers;

import com.denknd.config.TestConfig;
import com.denknd.dto.UserCreateDto;
import com.denknd.entity.Parameters;
import com.denknd.exception.AccessDeniedException;
import com.denknd.exception.InvalidUserDataException;
import com.denknd.exception.UserAlreadyExistsException;
import com.denknd.mappers.UserMapper;
import com.denknd.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(controllers = {UserController.class})
@AutoConfigureMockMvc
@SpringJUnitConfig(TestConfig.class)
class UserControllerTest {
  @MockBean
  private UserService userService;
  @MockBean
  private UserMapper userMapper;
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("Проверяет, что метод вызывает все сервисы")
  void createUser() throws Exception {
    var userCreateDto = UserCreateDto.builder()
            .email("email@mail.com")
            .password("password")
            .lastName("lastName")
            .firstName("firstName")
            .build();
    var json = this.objectMapper.writeValueAsString(userCreateDto);

    this.mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    verify(this.userService, times(1)).registrationUser(any());
  }

  @Test
  @DisplayName("Проверяет, что метод вызывает все сервисы и обрабатывает ошибку UserAlreadyExistsException")
  void createUser_UserAlreadyExistsException() throws Exception {
    var userCreateDto = UserCreateDto.builder()
            .email("email@mail.com")
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
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));

    verify(this.userService, times(1)).registrationUser(any());
  }

  @Test
  @DisplayName("Проверяет, что метод вызывает все сервисы и обрабатывает ошибку NoSuchAlgorithmException")
  void createUser_NoSuchAlgorithmException() throws Exception {
    var userCreateDto = UserCreateDto.builder()
            .email("email@mail.com")
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
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));

    verify(this.userService, times(1)).registrationUser(any());
  }

  @Test
  @DisplayName("Проверяет, что метод вызывает все сервисы и обрабатывает ошибку InvalidUserDataException")
  void createUser_InvalidUserDataException() throws Exception {
    var userCreateDto = UserCreateDto.builder()
            .email("email@mail.com")
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
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));

    verify(this.userService, times(1)).registrationUser(any());
  }

  @Test
  @DisplayName("Проверяет, что метод вызывает все сервисы и обрабатывает ошибку AccessDeniedException")
  void createUser_AccessDeniedException() throws Exception {
    var userCreateDto = UserCreateDto.builder()
            .email("email@mail.com")
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
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));

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
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    var argumentCapture = ArgumentCaptor.forClass(Parameters.class);
    verify(this.userService, times(1)).getUser(argumentCapture.capture());
    var parameters = argumentCapture.getValue();
    assertThat(parameters.getUserId()).isEqualTo(userId);
    assertThat(parameters.getEmail()).isEqualTo(email);
  }

}