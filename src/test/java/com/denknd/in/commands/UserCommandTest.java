package com.denknd.in.commands;

import com.denknd.controllers.UserController;
import com.denknd.dto.UserDto;
import com.denknd.entity.Roles;
import com.denknd.in.commands.functions.MyFunction;
import com.denknd.security.UserSecurity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserCommandTest {
  @Mock
  private UserController userController;
  @Mock
  private MyFunction<String[], Long> longIdParserFromRawParameters;
  private UserCommand userCommand;
  private final String COMMAND = "user";
  private final String EMAIL_PARAM = "email=";
  private final String ID_PARAM = "id=";
  private AutoCloseable closeable;
  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.userCommand = new UserCommand(this.userController);
    this.userCommand.setIdParser(this.longIdParserFromRawParameters);
  }
  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }
  @Test
  @DisplayName("Проверяет, что метод возвращает ожидаемой ответ")
  void getCommand() {
    var command = this.userCommand.getCommand();

    assertThat(command).isEqualTo(this.COMMAND);
  }

  @Test
  @DisplayName("Проверяет, что при роли пользователя юзер, метод обращается в сервис и отдает информацию о пользователе в системе")
  void run() {
    var userRole = Roles.USER;

    var user = UserSecurity.builder()
            .userId(1L)
            .firstName("FirstName")
            .role(userRole)
            .build();
    when(userController.getUser(eq(user.userId()))).thenReturn(mock(UserDto.class));

    this.userCommand.run(this.COMMAND, user);


    verify(this.userController, times(1)).getUser(anyLong());
    verify(this.userController, times(0)).getUser(anyString());
    verify(this.longIdParserFromRawParameters, times(0)).apply(any(), any());

  }

  @Test
  @DisplayName("Проверяет, что при роли пользователя админ, метод обращается в сервис и отдает информацию о запрошенном по айди пользователе")
  void run_Admin() {
    var user = UserSecurity.builder()
            .userId(1L)
            .firstName("FirstName")
            .role(Roles.ADMIN)
            .build();
    var userFindId = 4L;
    var userFind = UserDto.builder()
            .userId(userFindId)
            .lastName("LastNameFIND")
            .firstName("FirstNameFIND")
            .email("TestEmailFIND")
            .build();
    when(this.longIdParserFromRawParameters.apply(any(), any())).thenReturn(userFindId);
    when(this.userController.getUser(anyLong())).thenReturn(userFind);


    var run = this.userCommand.run(this.COMMAND, user);

    assertThat(run).isNotNull();
    verify(this.userController, times(1)).getUser(anyLong());
    verify(this.userController, times(0)).getUser(anyString());
    verify(this.longIdParserFromRawParameters, times(1)).apply(any(), any());

  }

  @Test
  @DisplayName("Проверяет, что при роли пользователя админ, метод обращается в сервис и отдает информацию о запрошенном по email пользователе ")
  void run_AdminByEmail() {
    var user = UserSecurity.builder()
            .userId(1L)
            .firstName("FirstName")
            .role(Roles.ADMIN)
            .build();

    var userFindId = 4L;
    var userFind = UserDto.builder()
            .userId(userFindId)
            .lastName("LastNameFIND")
            .firstName("FirstNameFIND")
            .email("TestEmailFIND")
            .build();
    var commandsAndParam = this.COMMAND + " " + this.EMAIL_PARAM + userFind.email();
    when(this.userController.getUser(anyString())).thenReturn(userFind);


    var run = this.userCommand.run(commandsAndParam, user);

    assertThat(run).isNotNull();
    verify(this.userController, times(0)).getUser(anyLong());
    verify(this.userController, times(1)).getUser(anyString());
    verify(this.longIdParserFromRawParameters, times(1)).apply(any(), any());

  }

  @Test
  @DisplayName("Проверяет, что при роли пользователя админ, метод вернет null, если не передать доп параметры ")
  void run_AdminNotData() {
    var user = UserSecurity.builder()
            .userId(1L)
            .firstName("FirstName")
            .role(Roles.ADMIN)
            .build();

    var userFindId = 4L;
    var userFind = UserDto.builder()
            .userId(userFindId)
            .lastName("LastNameFIND")
            .firstName("FirstNameFIND")
            .email("TestEmailFIND")
            .build();
    var commandsAndParam = this.COMMAND + " ";
    when(this.userController.getUser(anyString())).thenReturn(userFind);


    var run = this.userCommand.run(commandsAndParam, user);

    assertThat(run).isNotNull();
    verify(this.userController, times(0)).getUser(anyLong());
    verify(this.userController, times(0)).getUser(anyString());
    verify(this.longIdParserFromRawParameters, times(1)).apply(any(), any());

  }

  @Test
  @DisplayName("Проверяет, что при неизвестной роли выходит из метода ")
  void run_unknownRole() {
    var user = UserSecurity.builder()
            .userId(1L)
            .firstName("FirstName")
            .role(null)
            .build();

    var commandsAndParam = this.COMMAND + " " + this.EMAIL_PARAM + "TestEmailFIND";


    var run = this.userCommand.run(commandsAndParam, user);

    assertThat(run).isNull();
    verify(this.userController, times(0)).getUser(anyLong());
    verify(this.userController, times(0)).getUser(anyString());
    verify(this.longIdParserFromRawParameters, times(0)).apply(any(), any());

  }

  @Test
  @DisplayName("Проверяет, что при не авторизованном пользователе выходит из метода ")
  void run_nullRole() {

    var commandsAndParam = this.COMMAND + " " + this.EMAIL_PARAM + "TestEmailFIND";


    var run = this.userCommand.run(commandsAndParam, null);

    assertThat(run).isNull();
    verify(this.userController, times(0)).getUser(anyLong());
    verify(this.userController, times(0)).getUser(anyString());
    verify(this.longIdParserFromRawParameters, times(0)).apply(any(), any());

  }

  @Test
  @DisplayName("Проверяет доступность команды хелп для пользователя с ролью юзер")
  void getHelpCommand() {
    var userRole = Roles.USER;

    var helpCommand = this.userCommand.getHelpCommand(userRole);

    assertThat(helpCommand).contains(this.COMMAND).doesNotContain(this.EMAIL_PARAM, this.ID_PARAM);

  }

  @Test
  @DisplayName("Проверяет доступность команды хелп для пользователя с ролью админ")
  void getHelpCommand_Admin() {
    var userRole = Roles.ADMIN;

    var helpCommand = this.userCommand.getHelpCommand(userRole);

    assertThat(helpCommand).contains(this.COMMAND, this.EMAIL_PARAM, this.ID_PARAM);

  }

  @Test
  @DisplayName("Проверяет доступность команды хелп для пользователя с ролью null")
  void getHelpCommand_nullRole() {

    var helpCommand = this.userCommand.getHelpCommand(null);

    assertThat(helpCommand).isNull();

  }

  @Test
  @DisplayName("Проверяет, что выводит сообщение")
  void getMakesAction() {
    var makesAction = this.userCommand.getAuditActionDescription();
    assertThat(makesAction).isNotNull();
  }
}