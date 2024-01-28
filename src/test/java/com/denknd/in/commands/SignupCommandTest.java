package com.denknd.in.commands;

import com.denknd.entity.Role;
import com.denknd.entity.User;
import com.denknd.exception.UserAlreadyExistsException;
import com.denknd.services.RoleService;
import com.denknd.services.UserService;
import com.denknd.validator.Validators;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SignupCommandTest {
    private UserService userService;
    private RoleService roleService;
    private Validators validators;
    private Scanner scanner;
    private SignupCommand signupCommand;

    @BeforeEach
    void setUp() {
        this.userService = mock(UserService.class);
        this.roleService = mock(RoleService.class);
        this.scanner = mock(Scanner.class);
        this.validators = mock(Validators.class);

        this.signupCommand = new SignupCommand(userService, roleService,validators, scanner);

    }

    @Test
    @DisplayName("Проверяет, что команда соответствует ожидаемой")
    void getCommand() {
        var command = "signup";

        var result = this.signupCommand.getCommand();

        assertThat(result).isEqualTo(command);
    }

    @Test
    @DisplayName("Проверяет, что пользователь собирается правильно")
    void run() throws UserAlreadyExistsException {
        var command = "signup";
        var email = "emailadsf@emaildsf.com";
        var password = "password";
        var lastName = "LastName";
        var name = "Name";
        var user = User.builder().userId(1L).build();
        when(this.validators.isValid(any(),any(),any(),any())).thenReturn(email).thenReturn(password).thenReturn(lastName).thenReturn(name);
        when(this.validators.notNullValue(any(String[].class))).thenReturn(true);
        when(this.userService.registrationUser(any(User.class))).thenReturn(user);

        this.signupCommand.run(command, null);

        var userRawCaptor = ArgumentCaptor.forClass(User.class);
        verify(this.userService, times(1)).registrationUser(userRawCaptor.capture());
        var userRaw = userRawCaptor.getValue();
        assertThat(userRaw).isNotNull().satisfies(raw -> {
            assertThat(raw.getUserId()).isNull();
            assertThat(raw.getEmail()).isEqualTo(email);
            assertThat(raw.getPassword()).isEqualTo(password);
            assertThat(raw.getLastName()).isEqualTo(lastName);
            assertThat(raw.getFirstName()).isEqualTo(name);
        });
        var roleCaptor = ArgumentCaptor.forClass(Role[].class);
        verify(this.roleService, times(1)).addRoles(eq(user.getUserId()), roleCaptor.capture());
        var role = roleCaptor.getValue();
        assertThat(role).contains(Role.builder().roleName("USER").build());
    }

    @Test
    @DisplayName("Проверяет, что пользователь не собирается, если не вводить данные")
    void run_exit() throws UserAlreadyExistsException {
        var command = "signup";

        when(this.scanner.nextLine()).thenReturn("");


        this.signupCommand.run(command, null);

        verify(this.userService, times(0)).registrationUser(any());
        verify(this.roleService, times(0)).addRoles(any(), any());

    }



    @Test
    @DisplayName("Проверяет, что метод не доступен, когда пользователь авторизован")
    void run_NotCommandWhenAuth() throws UserAlreadyExistsException {
        var command = "signup";
        var roles = List.of(Role.builder().build());
        var userActive = mock(User.class);
        when(userActive.getRoles()).thenReturn(roles);

        this.signupCommand.run(command, userActive);

        verify(this.userService, times(0)).registrationUser(any(User.class));
        verify(this.roleService, times(0)).addRoles(anyLong(), any(Role.class));
    }

    @Test
    @DisplayName("Проверяет, что если userService выдает ошибку, выходит из метода с сообщением от ошибки")
    void run_throwException() throws UserAlreadyExistsException {
        var command = "signup";
        var email = "emailadsf@emaildsf.com";
        var password = "password";
        var lastName = "LastName";
        var name = "Name";
        var exception = "exception";
        when(this.validators.isValid(any(),any(),any(),any())).thenReturn(email).thenReturn(password).thenReturn(lastName).thenReturn(name);
        when(this.validators.notNullValue(any(String[].class))).thenReturn(true);
        when(this.userService.registrationUser(any(User.class))).thenThrow(new UserAlreadyExistsException(exception));

        var run = this.signupCommand.run(command, null);

        assertThat(run).isEqualTo(exception);

    }

    @Test
    @DisplayName("Если пользователь не авторизован, то подсказка доступна")
    void getHelpCommand() {
        var command = "signup";
        var roles = new ArrayList<Role>();

        var helpMessage = this.signupCommand.getHelpCommand(roles);

        assertThat(helpMessage).contains(command);
    }

    @Test
    @DisplayName("Если пользователь авторизован, то подсказка не доступна")
    void getHelpCommand_activeUser() {
        var roles = new ArrayList<Role>();
        roles.add(Role.builder().build());

        var helpMessage = this.signupCommand.getHelpCommand(roles);

        assertThat(helpMessage).isNull();
    }
    @Test
    @DisplayName("Проверяет, что выводит сообщение")
    void getMakesAction(){
        var makesAction = this.signupCommand.getMakesAction();
        assertThat(makesAction).isNotNull();
    }
}