package com.denknd.in.commands;

import com.denknd.entity.Role;
import com.denknd.entity.TypeMeter;
import com.denknd.entity.User;
import com.denknd.services.TypeMeterService;
import com.denknd.validator.Validators;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AddTypeMeterCommandTest {

    private AddTypeMeterCommand addTypeMeterCommand;
    private Validators validators;
    private Scanner scanner;
    private TypeMeterService typeMeterService;
    private final String COMMAND = "add_type";

    @BeforeEach
    void setUp() {
        this.validators = mock(Validators.class);
        this.scanner = mock(Scanner.class);
        this.typeMeterService = mock(TypeMeterService.class);
        this.addTypeMeterCommand = new AddTypeMeterCommand(this.validators, this.scanner, this.typeMeterService);
    }

    @Test
    @DisplayName("Проверяется, что команда ожидаемая")
    void getCommand() {

        var command = this.addTypeMeterCommand.getCommand();

        assertThat(command).isEqualTo(this.COMMAND);

    }

    @Test
    @DisplayName("Проверяет, что правильно собирается объект и вызывается сервис для его сохранения")
    void run() {
        var user = mock(User.class);
        var roles = List.of(Role.builder().roleName("ADMIN").build());
        when(user.getRoles()).thenReturn(roles);
        var code = "test";
        var description = "description test";
        var metric = "min";
        when(this.validators.isValid(any(), any(), any(), any())).thenReturn(code).thenReturn(description).thenReturn(metric);
        when(this.validators.notNullValue(any(String[].class))).thenReturn(true);
        when(this.typeMeterService.addNewTypeMeter(any())).thenReturn(mock(TypeMeter.class));

        this.addTypeMeterCommand.run(this.COMMAND, user);

        var typeCaptor = ArgumentCaptor.forClass(TypeMeter.class);
        verify(this.typeMeterService, times(1)).addNewTypeMeter(typeCaptor.capture());
        var type = typeCaptor.getValue();
        assertThat(type.getTypeCode()).isEqualTo(code);
        assertThat(type.getTypeDescription()).isEqualTo(description);
        assertThat(type.getMetric()).isEqualTo(metric);
    }
    @Test
    @DisplayName("Проверяет, что при не правильном заполнении, не вызывается сервис для сохранения")
    void run_nullData() {
        var user = mock(User.class);
        var roles = List.of(Role.builder().roleName("ADMIN").build());
        when(user.getRoles()).thenReturn(roles);

        when(this.validators.notNullValue(any(String[].class))).thenReturn(false);

        this.addTypeMeterCommand.run(this.COMMAND, user);

        verify(this.typeMeterService, times(0)).addNewTypeMeter(any());

    }
    @Test
    @DisplayName("Проверяет, что ролью Юзера данный эндпоинт не доступен")
    void run_notAdmin() {
        var user = mock(User.class);
        var roles = List.of(Role.builder().roleName("USER").build());
        when(user.getRoles()).thenReturn(roles);

        this.addTypeMeterCommand.run(this.COMMAND, user);

        verify(this.validators, times(0)).isValid(any(), any(), any(), any());
        verify(this.typeMeterService, times(0)).addNewTypeMeter(any());

    }

    @Test
    @DisplayName("Проверяет, что не авторизованному пользователю данный эндпоинт не доступен")
    void run_notUser() {

        this.addTypeMeterCommand.run(this.COMMAND, null);

        verify(this.validators, times(0)).isValid(any(), any(), any(), any());
        verify(this.typeMeterService, times(0)).addNewTypeMeter(any());

    }

    @Test
    @DisplayName("Проверяет, что подсказка доступна пользователю с ролью админ")
    void getHelpCommand() {
        var roles = List.of(Role.builder().roleName("ADMIN").build());

        var helpCommand = this.addTypeMeterCommand.getHelpCommand(roles);

        assertThat(helpCommand).contains(this.COMMAND);
    }

    @Test
    @DisplayName("Проверяет, что подсказка не доступна пользователю с ролью юзер")
    void getHelpCommand_notAdmin() {
        var roles = List.of(Role.builder().roleName("USER").build());

        var helpCommand = this.addTypeMeterCommand.getHelpCommand(roles);

        assertThat(helpCommand).isNull();
    }
    @Test
    @DisplayName("Проверяет, что подсказка не доступна пользователю без роли")
    void getHelpCommand_notRoles() {

        var helpCommand = this.addTypeMeterCommand.getHelpCommand(List.of());

        assertThat(helpCommand).isNull();
    }
    @Test
    @DisplayName("Проверяет, что подсказка не доступна пользователю без роли")
    void getHelpCommand_null() {

        var helpCommand = this.addTypeMeterCommand.getHelpCommand(null);

        assertThat(helpCommand).isNull();
    }
    @Test
    @DisplayName("Проверяет, что выводит сообщение")
    void getMakesAction(){
        var makesAction = this.addTypeMeterCommand.getMakesAction();
        assertThat(makesAction).isNotNull();
    }
}