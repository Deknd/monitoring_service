package com.denknd.in.commands;

import com.denknd.entity.Role;
import com.denknd.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class LogoutCommandTest {

    private LogoutCommand logoutCommand;

    @BeforeEach
    void setUp() {
        this.logoutCommand = new LogoutCommand();
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
        var userMock = mock(User.class);

        var result = this.logoutCommand.run(command, userMock);

        assertThat(result).hasAllNullFieldsOrProperties();

    }
    @Test
    @DisplayName("Проверяет, что если команда с доп параметром не поддерживается")
    void run_notParam() {
        var command = "logout sdf";
        var userMock = mock(User.class);

        var result = this.logoutCommand.run(command, userMock);

        assertThat(result).isNull();

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
        var roles = List.of(Role.builder().build());

        var helpCommand = this.logoutCommand.getHelpCommand(roles);

        assertThat(helpCommand).contains(command);
    }

    @Test
    @DisplayName("Проверяет что если пользователь не авторизирован, не выводит сообщение")
    void getHelpCommand_userNotAuth() {
        var roles = new ArrayList<Role>();

        var helpCommand = this.logoutCommand.getHelpCommand(roles);

        assertThat(helpCommand).isNull();
    }
    @Test
    @DisplayName("Проверяет что если пользователь не авторизирован, не выводит сообщение")
    void getHelpCommand_rolesIsNull() {

        var helpCommand = this.logoutCommand.getHelpCommand(null);

        assertThat(helpCommand).isNull();
    }
    @Test
    @DisplayName("Проверяет, что выводит сообщение")
    void getMakesAction(){
        var makesAction = this.logoutCommand.getMakesAction();
        assertThat(makesAction).isNotNull();
    }
}