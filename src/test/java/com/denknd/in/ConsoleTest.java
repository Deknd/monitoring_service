package com.denknd.in;

import com.denknd.in.commands.ConsoleCommand;
import com.denknd.out.audit.AuditService;
import com.denknd.security.SecurityService;
import com.denknd.security.UserSecurity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ConsoleTest {


  private Console console;
  @Mock
  private AuditService auditService;
  @Mock
  private Scanner scanner;
  @Mock
  private SecurityService securityService;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.console = new Console(this.scanner, this.auditService, this.securityService);
  }
  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }
  @Test
  @DisplayName("Проверяет, что консоль запускается, обращается в сервис секьюрити и запускает нужную команду")
  void run() {
    var testCommand = "test";
    when(this.scanner.nextLine()).thenReturn(testCommand).thenThrow(IllegalStateException.class);
    var consoleCommand = mock(ConsoleCommand.class);
    when(consoleCommand.getCommand()).thenReturn(testCommand);
    this.console.addCommand(consoleCommand);

    this.console.run();

    verify(consoleCommand, times(1)).run(eq(testCommand), any());
    verify(this.securityService, times(2)).getUserSecurity();
    verify(this.auditService, times(1)).addAction(any(), any(), any());
  }

  @Test
  @DisplayName("Проверяет, что консоль запускается и не известной командой не запускает ни каких сервисов")
  void run_notCommand() {
    var testCommand = "test";
    when(this.scanner.nextLine()).thenReturn(testCommand).thenThrow(IllegalStateException.class);
    var consoleCommand = mock(ConsoleCommand.class);
    when(consoleCommand.getCommand()).thenReturn("test2");
    this.console.addCommand(consoleCommand);

    this.console.run();

    verify(consoleCommand, times(0)).run(eq(testCommand), any());
    verify(this.securityService, times(0)).getUserSecurity();
    verify(this.auditService, times(0)).addAction(any(), any(), any());
  }

  @Test
  @DisplayName("Проверяет, что консоль запускается и если пользователь аутентифицирован то обращается в сервис и достает его")
  void run_readName() {
    var testCommand = "test";
    when(this.scanner.nextLine()).thenReturn(testCommand).thenThrow(IllegalStateException.class);
    var consoleCommand = mock(ConsoleCommand.class);
    when(consoleCommand.getCommand()).thenReturn("test2");
    this.console.addCommand(consoleCommand);
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(mock(UserSecurity.class));

    this.console.run();

    verify(consoleCommand, times(0)).run(eq(testCommand), any());
    verify(this.securityService, times(2)).getUserSecurity();
    verify(this.auditService, times(0)).addAction(any(), any(), any());
    verify(this.securityService, times(2)).getUserSecurity();
  }

  @Test
  @DisplayName("Проверяет, что выдаются все доступные команды")
  void commands() {
    var consoleCommand = mock(ConsoleCommand.class);
    this.console.addCommand(consoleCommand);

    var commandMap = this.console.commands();
    assertThat(commandMap).containsValue(consoleCommand);
  }

}