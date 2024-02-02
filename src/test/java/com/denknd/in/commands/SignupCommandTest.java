package com.denknd.in.commands;

import com.denknd.controllers.UserController;
import com.denknd.dto.UserCreateDto;
import com.denknd.entity.Roles;
import com.denknd.exception.UserAlreadyExistsException;
import com.denknd.security.UserSecurity;
import com.denknd.validator.DataValidatorManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SignupCommandTest {
    @Mock
    private UserController userController;
    @Mock
    private DataValidatorManager dataValidatorManager;
    private SignupCommand signupCommand;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        this.closeable = MockitoAnnotations.openMocks(this);
        this.signupCommand = new SignupCommand(this.userController, this.dataValidatorManager);
    }
    @AfterEach
    void tearDown() throws Exception {
        this.closeable.close();
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
    void run() throws UserAlreadyExistsException, NoSuchAlgorithmException {
        var command = "signup";
        var email = "emailadsf@emaildsf.com";
        var password = "password";
        var lastName = "LastName";
        var name = "Name";
        when(this.dataValidatorManager.getValidInput(any(),any(),any())).thenReturn(email).thenReturn(password).thenReturn(lastName).thenReturn(name);
        when(this.dataValidatorManager.areAllValuesNotNullAndNotEmpty(any(String[].class))).thenReturn(true);

        this.signupCommand.run(command, null);

        var userRawCaptor = ArgumentCaptor.forClass(UserCreateDto.class);
        verify(this.userController, times(1)).createUser(userRawCaptor.capture());
        var userRaw = userRawCaptor.getValue();
        assertThat(userRaw).isNotNull().satisfies(raw -> {
            assertThat(raw.email()).isEqualTo(email);
            assertThat(raw.password()).isEqualTo(password);
            assertThat(raw.lastName()).isEqualTo(lastName);
            assertThat(raw.firstName()).isEqualTo(name);
        });
    }

    @Test
    @DisplayName("Проверяет, что пользователь не собирается, если не вводить данные")
    void run_exit() throws UserAlreadyExistsException, NoSuchAlgorithmException {
        var command = "signup";

        this.signupCommand.run(command, null);

        verify(this.userController, times(0)).createUser(any());
    }



    @Test
    @DisplayName("Проверяет, что метод не доступен, когда пользователь авторизован")
    void run_NotCommandWhenAuth() throws UserAlreadyExistsException, NoSuchAlgorithmException {
        var command = "signup";
        var userActive = mock(UserSecurity.class);
        when(userActive.role()).thenReturn(null);

        this.signupCommand.run(command, userActive);

        verify(this.userController, times(0)).createUser(any());
    }

    @Test
    @DisplayName("Проверяет, что если userService выдает ошибку, выходит из метода с сообщением от ошибки")
    void run_throwException() throws UserAlreadyExistsException, NoSuchAlgorithmException {
        var command = "signup";
        var email = "emailadsf@emaildsf.com";
        var password = "password";
        var lastName = "LastName";
        var name = "Name";
        var exception = "exception";
        when(this.dataValidatorManager.getValidInput(any(),any(),any())).thenReturn(email).thenReturn(password).thenReturn(lastName).thenReturn(name);
        when(this.dataValidatorManager.areAllValuesNotNullAndNotEmpty(any(String[].class))).thenReturn(true);
        when(this.userController.createUser(any(UserCreateDto.class))).thenThrow(new UserAlreadyExistsException(exception));

        var run = this.signupCommand.run(command, null);

        assertThat(run).isEqualTo(exception);
        verify(this.userController, times(1)).createUser(any());
    }

    @Test
    @DisplayName("Если пользователь не авторизован, то подсказка доступна")
    void getHelpCommand() {
        var command = "signup";

        var helpMessage = this.signupCommand.getHelpCommand(null);

        assertThat(helpMessage).contains(command);
    }

    @Test
    @DisplayName("Если пользователь авторизован, то подсказка не доступна")
    void getHelpCommand_activeUser() {
        var roles = Roles.USER;

        var helpMessage = this.signupCommand.getHelpCommand(roles);

        assertThat(helpMessage).isNull();
    }
    @Test
    @DisplayName("Проверяет, что выводит сообщение")
    void getMakesAction(){
        var makesAction = this.signupCommand.getAuditActionDescription();
        assertThat(makesAction).isNotNull();
    }
}