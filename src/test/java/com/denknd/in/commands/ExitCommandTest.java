package com.denknd.in.commands;

import com.denknd.security.UserSecurity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ExitCommandTest {

  @Mock
  private Scanner scanner;
  private ExitCommand exitCommand;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.exitCommand = new ExitCommand(this.scanner);
  }
  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }
  @Test
  @DisplayName("Проверяет что команда соответствует ожидаемой")
  void getCommand() {
    var command = "exit";

    var exit = this.exitCommand.getCommand();

    assertThat(exit).isEqualTo(command);
  }

  @Test
  @DisplayName("Проверяет, что сканер закрывается")
  void run() {
    var command = "exit";
    var user = mock(UserSecurity.class);

    this.exitCommand.run(command, user);

    verify(this.scanner, times(1)).close();

  }

  @Test
  @DisplayName("Проверяет, что сканер закрывается")
  void run_failedCommand() {
    var command = "exit asd";
    var user = mock(UserSecurity.class);

    var failed = this.exitCommand.run(command, user);

    verify(this.scanner, times(0)).close();
    assertThat(failed).contains(command);

  }

  @Test
  @DisplayName("проверяет, что подсказка содержит название команды")
  void getHelpCommand() {
    var command = "exit";

    var helpCommand = this.exitCommand.getHelpCommand(null);

    assertThat(helpCommand).contains(command);
  }

  @Test
  @DisplayName("Проверяет, что выводит сообщение")
  void getMakesAction() {
    var makesAction = this.exitCommand.getAuditActionDescription();
    assertThat(makesAction).isNotNull();
  }
}