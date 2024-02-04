package com.denknd.controllers;

import com.denknd.dto.UserCreateDto;
import com.denknd.entity.Roles;
import com.denknd.entity.User;
import com.denknd.exception.InvalidUserDataException;
import com.denknd.exception.UserAlreadyExistsException;
import com.denknd.mappers.UserMapper;
import com.denknd.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.NoSuchAlgorithmException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {
  @Mock
  private UserService userService;
  @Mock
  private UserMapper userMapper;
  private UserController userController;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.userController = new UserController(this.userService, this.userMapper);
  }
  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }
  @Test
  @DisplayName("Проверяет, что метод вызывает все сервисы")
  void createUser() throws UserAlreadyExistsException, NoSuchAlgorithmException, InvalidUserDataException {
    var userCreateDto = mock(UserCreateDto.class);
    when(this.userMapper.mapUserCreateDtoToUser(eq(userCreateDto))).thenReturn(mock(User.class));
    when(this.userService.registrationUser(any())).thenReturn(mock(User.class));

    this.userController.createUser(userCreateDto);

    verify(this.userService, times(1)).registrationUser(any());
  }

  @Test
  @DisplayName("Проверяет, что вызывается сервисы с нужными параметрами")
  void getUser() {
    var userId = 1L;
    when(this.userService.existUser(eq(userId))).thenReturn(true);

    this.userController.getUser(userId);

    verify(this.userService, times(1)).getUserById(eq(userId));
  }
  @Test
  @DisplayName("Проверяет, что не вызывается сервис, когда нет пользователя с данным айди")
  void getUser_userId_notExist() {
    var userId = 1L;
    when(this.userService.existUser(eq(userId))).thenReturn(false);

    this.userController.getUser(userId);

    verify(this.userService, times(0)).getUserById(any());
  }

  @Test
  @DisplayName("Проверяет, что вызывается сервисы с нужными параметрами")
  void getUser_email() {
    var email = "email";
    when(this.userService.existUserByEmail(eq(email))).thenReturn(true);

    this.userController.getUser(email);

    verify(this.userService, times(1)).getUserByEmail(any());
  }
  @Test
  @DisplayName("Проверяет, что вызывается сервисы с нужными параметрами")
  void getUser_email_notExist() {
    var email = "email";
    when(this.userService.existUserByEmail(eq(email))).thenReturn(false);

    this.userController.getUser(email);

    verify(this.userService, times(0)).getUserByEmail(any());
  }
}