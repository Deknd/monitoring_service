package com.denknd.in.commands;

import com.denknd.controllers.TypeMeterController;
import com.denknd.dto.TypeMeterDto;
import com.denknd.entity.Roles;
import com.denknd.security.UserSecurity;
import com.denknd.validator.DataValidatorManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AddTypeMeterCommandTest {

    private AddTypeMeterCommand addTypeMeterCommand;
    @Mock
    private DataValidatorManager dataValidatorManager;
    @Mock
    private TypeMeterController typeMeterController;
    private final String COMMAND = "add_type";
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        this.closeable = MockitoAnnotations.openMocks(this);
        this.addTypeMeterCommand = new AddTypeMeterCommand(this.dataValidatorManager, this.typeMeterController);
    }
    @AfterEach
    void tearDown() throws Exception {
        this.closeable.close();
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
        var user = mock(UserSecurity.class);
        var roles = Roles.ADMIN;
        when(user.role()).thenReturn(roles);
        var code = "test";
        var description = "description test";
        var metric = "min";
        when(this.dataValidatorManager.getValidInput(any(), any(), any())).thenReturn(code).thenReturn(description).thenReturn(metric);
        when(this.dataValidatorManager.areAllValuesNotNullAndNotEmpty(any(String[].class))).thenReturn(true);
        when(this.typeMeterController.addNewType(any())).thenReturn(mock(TypeMeterDto.class));

        this.addTypeMeterCommand.run(this.COMMAND, user);

        var typeCaptor = ArgumentCaptor.forClass(TypeMeterDto.class);
        verify(this.typeMeterController, times(1)).addNewType(typeCaptor.capture());
        var type = typeCaptor.getValue();
        assertThat(type.typeCode()).isEqualTo(code);
        assertThat(type.typeDescription()).isEqualTo(description);
        assertThat(type.metric()).isEqualTo(metric);
    }
    @Test
    @DisplayName("Проверяет, что при не правильном заполнении, не вызывается сервис для сохранения")
    void run_nullData() {
        var user = mock(UserSecurity.class);
        var roles = Roles.ADMIN;
        when(user.role()).thenReturn(roles);

        when(this.dataValidatorManager.areAllValuesNotNullAndNotEmpty(any(String[].class))).thenReturn(false);

        this.addTypeMeterCommand.run(this.COMMAND, user);

        verify(this.typeMeterController, times(0)).addNewType(any());

    }
    @Test
    @DisplayName("Проверяет, что ролью Юзера данный эндпоинт не доступен")
    void run_notAdmin() {
        var user = mock(UserSecurity.class);
        var roles = Roles.USER;
        when(user.role()).thenReturn(roles);

        this.addTypeMeterCommand.run(this.COMMAND, user);

        verify(this.dataValidatorManager, times(0)).getValidInput(any(), any(), any());
        verify(this.typeMeterController, times(0)).addNewType(any());

    }

    @Test
    @DisplayName("Проверяет, что не авторизованному пользователю данный эндпоинт не доступен")
    void run_notUser() {

        this.addTypeMeterCommand.run(this.COMMAND, null);

        verify(this.dataValidatorManager, times(0)).getValidInput(any(), any(), any());
        verify(this.typeMeterController, times(0)).addNewType(any());

    }

    @Test
    @DisplayName("Проверяет, что подсказка доступна пользователю с ролью админ")
    void getHelpCommand() {
        var roles = Roles.ADMIN;

        var helpCommand = this.addTypeMeterCommand.getHelpCommand(roles);

        assertThat(helpCommand).contains(this.COMMAND);
    }

    @Test
    @DisplayName("Проверяет, что подсказка не доступна пользователю с ролью юзер")
    void getHelpCommand_notAdmin() {
        var roles = Roles.USER;

        var helpCommand = this.addTypeMeterCommand.getHelpCommand(roles);

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
        var makesAction = this.addTypeMeterCommand.getAuditActionDescription();
        assertThat(makesAction).isNotNull();
    }
}