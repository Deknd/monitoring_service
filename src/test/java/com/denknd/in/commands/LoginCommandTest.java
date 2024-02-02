package com.denknd.in.commands;

import com.denknd.entity.Roles;
import com.denknd.security.SecurityService;
import com.denknd.security.UserSecurity;
import com.denknd.validator.DataValidatorManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoginCommandTest {
  @Mock
  private DataValidatorManager dataValidatorManager;
  private LoginCommand loginCommand;
  @Mock
  private SecurityService securityService;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.loginCommand = new LoginCommand(this.dataValidatorManager, this.securityService);
  }
  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }
  @Test
  @DisplayName("Проверяет, что команда совпадает с ожидаемой")
  void getCommand() {
    var command = "login";

    var login = this.loginCommand.getCommand();

    assertThat(login).isEqualTo(command);
  }

  @Test
  @DisplayName("Проверяет, что обращается во все сервис для авторизации")
  void run() {
    var command = "login";
    var login = "login";
    var password = "password";
    when(this.dataValidatorManager.getValidInput(any(), any(), any())).thenReturn(login).thenReturn(password);
    when(this.dataValidatorManager.areAllValuesNotNullAndNotEmpty(any(), any())).thenReturn(true);
    when(this.securityService.authentication(any(),any())).thenReturn(mock(UserSecurity.class));
    var run = this.loginCommand.run(command, null);

    assertThat(run).isNotNull();
    verify(this.securityService, times(1)).authentication(eq(login), eq(password));
  }

  @Test
  @DisplayName("Проверяет, что если пользователя нет, вызывается сервис и возвращает null")
  void run_notUser() {
    var command = "login";
    var login = "login";
    var password = "password";
    when(this.dataValidatorManager.getValidInput(any(), any(), any())).thenReturn(login).thenReturn(password);
    when(this.dataValidatorManager.areAllValuesNotNullAndNotEmpty(any(), any())).thenReturn(true);
    when(this.securityService.authentication(eq(login), eq(password))).thenReturn(null);

    var result = this.loginCommand.run(command, null);

    verify(this.dataValidatorManager, times(2)).getValidInput(any(), any(), any());
    verify(this.securityService, times(1)).authentication(eq(login), eq(password));
    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, что когда данные вводятся не корректно, не вызывается сервис")
  void run_failedLogin() {
    var command = "login";

    var result = this.loginCommand.run(command, null);
    
    assertThat(result).isNotNull();
    verify(this.dataValidatorManager, times(2)).getValidInput(any(), any(), any());
    verify(this.securityService, times(0)).authentication(any(), any());
  }

  @Test
  @DisplayName("Проверяет, что не поддерживаются команды с доп параметрами")
  void run_failedCommand() {
    var command = "login asd";

    var result = this.loginCommand.run(command, null);
    
    assertThat(result).isNotNull();
    verify(this.dataValidatorManager, times(0)).getValidInput(any(), any(), any());
    verify(this.securityService, times(0)).authentication(any(), any());

  }

  @Test
  @DisplayName("Проверяет, что если использовать команду уже залогинившись, то сазу выходишь из данного метода")
  void run_userNotNull() {
    var command = "login";

    var result = this.loginCommand.run(command, mock(UserSecurity.class));

    assertThat(result).isNull();
    verify(this.dataValidatorManager, times(0)).getValidInput(any(), any(), any());
    verify(this.securityService, times(0)).authentication(any(), any());

  }

  @Test
  @DisplayName("Проверяет, что подсказка выдается, когда пользователь не авторизован")
  void getHelpCommand() {
    var command = "login";

    var helpCommand = this.loginCommand.getHelpCommand(null);

    assertThat(helpCommand).contains(command);
  }

  @Test
  @DisplayName("Проверяет, что подсказка не выдается, когда пользователь авторизован")
  void getHelpCommand_notHelp() {
    var command = "login";
    var roles = Roles.USER;

    var helpCommand = this.loginCommand.getHelpCommand(roles);

    assertThat(helpCommand).isNull();
  }

  @Test
  @DisplayName("Проверяет, что выводит сообщение")
  void getMakesAction() {
    var makesAction = this.loginCommand.getAuditActionDescription();
    assertThat(makesAction).isNotNull();
  }
}