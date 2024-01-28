package com.denknd.in.commands;

import com.denknd.entity.Role;
import com.denknd.entity.User;
import com.denknd.services.RoleService;
import com.denknd.services.UserService;
import com.denknd.validator.Validators;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LoginCommandTest {
    private Scanner scanner;
    private UserService userService;
    private RoleService roleService;
    private Validators validators;
    private LoginCommand loginCommand;

    @BeforeEach
    void setUp() {
        this.scanner = mock(Scanner.class);
        this.userService = mock(UserService.class);
        this.roleService = mock(RoleService.class);
        this.validators = mock(Validators.class);
        this.loginCommand = new LoginCommand(userService, roleService, validators,scanner);
    }

    @Test
    @DisplayName("Проверяет, что команда совпадает с ожидаемой")
    void getCommand() {
        var command = "login";

        var login = this.loginCommand.getCommand();

        assertThat(login).isEqualTo(command);


    }

    @Test
    @DisplayName("Проверяет, что обращается во все сервисы для сбора User")
    void run() {
        var command = "login";
        var login = "login";
        var password = "password";
        var mockUser = mock(User.class);
        var userId = 1L;
        when(mockUser.getUserId()).thenReturn(userId);
        when(this.userService.loginUser(eq(login), eq(password))).thenReturn(mockUser);
        when(this.validators.isValid(any(),any(),any(), any())).thenReturn(login).thenReturn(password);
        when(this.validators.notNullValue(any(), any())).thenReturn(true);

        this.loginCommand.run(command, null);

        verify(this.userService, times(1)).loginUser(eq(login), eq(password));
        verify(this.roleService, times(1)).getRoles(eq(userId));
    }
    @Test
    @DisplayName("Проверяет, что если пользователя нет, возвращает null")
    void run_notUser() {
        var command = "login";
        var login = "login";
        var password = "password";
        var mockUser = mock(User.class);
        var userId = 1L;
        when(mockUser.getUserId()).thenReturn(userId);
        when(this.scanner.nextLine()).thenReturn(login).thenReturn(password);
        when(this.validators.isValid(any(),any(),any(), any())).thenReturn(login).thenReturn(password);
        when(this.validators.notNullValue(any(), any())).thenReturn(true);

        var result = this.loginCommand.run(command, null);

        verify(this.userService, times(1)).loginUser(any(), any());
        verify(this.roleService, times(0)).getRoles(any());
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Проверяет, что когда данные вводятся не корректно, возвращается null")
    void run_failedLogin() {
        var command = "login";
        var mockUser = mock(User.class);
        var userId = 1L;
        when(mockUser.getUserId()).thenReturn(userId);

        var result = this.loginCommand.run(command, null);

        verify(this.userService, times(0)).loginUser(any(), any());
        verify(this.roleService, times(0)).getRoles(any());
        assertThat(result).isNull();
    }
    @Test
    @DisplayName("Проверяет, что не поддерживаются команды с доп параметрами")
    void run_failedCommand() {
        var command = "login asd";

        var result = this.loginCommand.run(command, null);

        verify(this.userService, times(0)).loginUser(any(), any());
        verify(this.roleService, times(0)).getRoles(any());
        assertThat(result).isNull();
    }
    @Test
    @DisplayName("Проверяет, что если использовать команду уже залогинившись, то сазу выходишь из данного метода")
    void run_userNotNull() {
        var command = "login";

        var result = this.loginCommand.run(command, mock(User.class));

        verify(this.userService, times(0)).loginUser(any(), any());
        verify(this.roleService, times(0)).getRoles(any());
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Проверяет, что подсказка выдается, когда пользователь не авторизован")
    void getHelpCommand() {
        var command = "login";
        var roles = new ArrayList<Role>();

        var helpCommand = this.loginCommand.getHelpCommand(roles);

        assertThat(helpCommand).contains(command);
    }

    @Test
    @DisplayName("Проверяет, что подсказка не выдается, когда пользователь авторизован")
    void getHelpCommand_notHelp() {
        var command = "login";
        var roles = List.of(Role.builder().build());

        var helpCommand = this.loginCommand.getHelpCommand(roles);

        assertThat(helpCommand).isNull();
    }
    @Test
    @DisplayName("Проверяет, что выводит сообщение")
    void getMakesAction(){
        var makesAction = this.loginCommand.getMakesAction();
        assertThat(makesAction).isNotNull();
    }
}