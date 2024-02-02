package com.denknd.in.commands;

import com.denknd.entity.Roles;
import com.denknd.security.SecurityService;
import com.denknd.security.UserSecurity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class LogoutCommandTest {

  private LogoutCommand logoutCommand;
  @Mock
  private SecurityService securityService;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.logoutCommand = new LogoutCommand(this.securityService);
  }

  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }

  @Test
  @DisplayName("Проверяет, что возвращает ожидаемую команду")
  void getCommand() {
    var command = "logout";

    var logout = this.logoutCommand.getCommand();

    assertThat(logout).isEqualTo(command);
  }

  @Test
  @DisplayName("Проверяет, что метод возвращает нового полностью пустого пользователя")
  void run() {
    var command = "logout";
    var userMock = mock(UserSecurity.class);

    var result = this.logoutCommand.run(command, userMock);

    verify(this.securityService, times(1)).logout();
    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, что если команда с доп параметром не поддерживается")
  void run_notParam() {
    var command = "logout sdf";
    var userMock = mock(UserSecurity.class);

    var result = this.logoutCommand.run(command, userMock);

    verify(this.securityService, times(0)).logout();
    assertThat(result).isNotNull();

  }

  @Test
  @DisplayName("Проверяет, что если пользователя нет, то возвращает null")
  void run_notUser() {
    var command = "logout";

    var result = this.logoutCommand.run(command, null);

    assertThat(result).isNull();

  }

  @Test
  @DisplayName("Проверяет что выводит сообщения для команды help")
  void getHelpCommand() {
    var command = "logout";
    var roles = Roles.USER;

    var helpCommand = this.logoutCommand.getHelpCommand(roles);

    assertThat(helpCommand).contains(command);
  }



  @Test
  @DisplayName("Проверяет что если пользователь не авторизирован, не выводит сообщение")
  void getHelpCommand_rolesIsNull() {

    var helpCommand = this.logoutCommand.getHelpCommand(null);

    assertThat(helpCommand).isNull();
  }

  @Test
  @DisplayName("Проверяет, что выводит сообщение")
  void getMakesAction() {
    var makesAction = this.logoutCommand.getAuditActionDescription();
    assertThat(makesAction).isNotNull();
  }
}