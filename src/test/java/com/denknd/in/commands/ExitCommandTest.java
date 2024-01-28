package com.denknd.in.commands;

import com.denknd.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ExitCommandTest {

    private Scanner scanner;
    private ExitCommand exitCommand;

    @BeforeEach
    void setUp() {
        this.scanner = mock(Scanner.class);
        this.exitCommand = new ExitCommand(this.scanner);
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
        var user = mock(User.class);

        this.exitCommand.run(command, user);

        verify(this.scanner, times(1)).close();

    }
    @Test
    @DisplayName("Проверяет, что сканер закрывается")
    void run_failedCommand() {
        var command = "exit asd";
        var user = mock(User.class);

        var failed = this.exitCommand.run(command, user);

        verify(this.scanner, times(0)).close();
        assertThat(failed).contains(command);

    }

    @Test
    @DisplayName("проверяет, что подсказка содержит название команды")
    void getHelpCommand() {
        var command = "exit";

        var helpCommand = this.exitCommand.getHelpCommand(List.of());

        assertThat(helpCommand).contains(command);
    }
    @Test
    @DisplayName("Проверяет, что выводит сообщение")
    void getMakesAction(){
        var makesAction = this.exitCommand.getMakesAction();
        assertThat(makesAction).isNotNull();
    }
}