package com.denknd.in.commands;

import com.denknd.entity.Role;
import com.denknd.in.Console;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class HelpCommandTest {

    private HelpCommand helpCommand;
    private Console console;
    @BeforeEach
    void setUp() {
        this.console = mock(Console.class);
        this.helpCommand = new HelpCommand(this.console);
    }

    @Test
    @DisplayName(" Проверяет, что команда соответствует ожидаемой")
    void getCommand() {
        var command = "help";

        var helpCommand = this.helpCommand.getCommand();

        assertThat(helpCommand).isEqualTo(command);
    }

    @Test
    @DisplayName("Проверяет, что вызываеются не обходимые сервисы")
    void run() {
        var consoleCommand = mock(ConsoleCommand.class);
        var commands = new HashMap<String, ConsoleCommand>();
        commands.put("test", consoleCommand);
        when(this.console.commands()).thenReturn(commands);

        this.helpCommand.run("", null);

        verify(this.console, times(1)).commands();
        verify(consoleCommand, times(1)).getHelpCommand(any());
    }

    @Test
    @DisplayName("Проверяет, что подсказка выводиться для не авторизированного пользователя")
    void getHelpCommand() {
        var command = "help";

        var helpCommand = this.helpCommand.getHelpCommand(null);

        assertThat(helpCommand).contains(command);
    }
    @Test
    @DisplayName("Проверяет, что подсказка выводиться для авторизированного пользователя")
    void getHelpCommand_userRole() {
        var command = "help";
        var role = Role.builder().roleName("USER").build();

        var helpCommand = this.helpCommand.getHelpCommand(List.of(role));

        assertThat(helpCommand).contains(command);
    }
    @Test
    @DisplayName("Проверяет, что выводит сообщение")
    void getMakesAction(){
        var makesAction = this.helpCommand.getMakesAction();
        assertThat(makesAction).isNotNull();
    }
}