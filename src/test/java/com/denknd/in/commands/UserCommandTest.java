package com.denknd.in.commands;

import com.denknd.entity.Role;
import com.denknd.entity.User;
import com.denknd.in.commands.functions.MyFunction;
import com.denknd.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserCommandTest {

    private UserService userService;
    private MyFunction<String[], Long> longIdParserFromRawParameters;

    private UserCommand userCommand;
    private final String COMMAND = "user";
    private final String EMAIL_PARAM = "email=";
    private final String ID_PARAM = "id=";

    @BeforeEach
    void setUp() {
        this.userService = mock(UserService.class);
        this.longIdParserFromRawParameters = mock(MyFunction.class);
        this.userCommand = new UserCommand(this.userService);
        this.userCommand.setLongIdParserFromRawParameters(this.longIdParserFromRawParameters);
    }

    @Test
    @DisplayName("Проверяет, что метод возвращает ожидаемой ответ")
    void getCommand() {
        var command = this.userCommand.getCommand();

        assertThat(command).isEqualTo(this.COMMAND);
    }

    @Test
    @DisplayName("Проверяет, что при роли пользователя юзер, метод не обращается в сервис и отдает информацию о пользователе в системе")
    void run() {
        var userRole = Role.builder().roleName("USER").build();

        var user = User.builder()
                .userId(1L)
                .lastName("LastName")
                .firstName("FirstName")
                .email("TestEmail")
                .roles(List.of(userRole))
                .build();

        var run = this.userCommand.run(this.COMMAND, user);

        assertThat(run).contains(String.valueOf(user.getUserId()), user.getEmail(), user.getFirstName(), user.getLastName());

        verify(this.userService, times(0)).existUser(any());
        verify(this.userService, times(0)).getUserById(any());
        verify(this.userService, times(0)).existUserByEmail(any());
        verify(this.userService, times(0)).existUserByEmail(any());

    }

    @Test
    @DisplayName("Проверяет, что при роли пользователя админ, метод обращается в сервис и отдает информацию о запрошенном по айди пользователе")
    void run_Admin() {
        var adminRole = Role.builder().roleName("ADMIN").build();
        var userRole = Role.builder().roleName("USER").build();

        var user = User.builder()
                .userId(1L)
                .lastName("LastName")
                .firstName("FirstName")
                .email("TestEmail")
                .roles(List.of(adminRole))
                .build();
        var userFindId = 4L;
        var userFind = User.builder()
                .userId(userFindId)
                .lastName("LastNameFIND")
                .firstName("FirstNameFIND")
                .email("TestEmailFIND")
                .roles(List.of(userRole))
                .build();
        when(this.longIdParserFromRawParameters.apply(any(), any())).thenReturn(userFindId);
        when(this.userService.existUser(eq(userFindId))).thenReturn(true);
        when(this.userService.getUserById(eq(userFindId))).thenReturn(userFind);

        var run = this.userCommand.run(this.COMMAND, user);

        assertThat(run).isNotNull();
        verify(this.userService, times(1)).existUser(any());
        verify(this.userService, times(1)).getUserById(any());
        verify(this.userService, times(0)).existUserByEmail(any());
        verify(this.userService, times(0)).existUserByEmail(any());

    }

    @Test
    @DisplayName("Проверяет, что при роли пользователя админ, метод обращается в сервис и отдает информацию о запрошенном по email пользователе ")
    void run_AdminByEmail() {
        var adminRole = Role.builder().roleName("ADMIN").build();
        var userRole = Role.builder().roleName("USER").build();

        var user = User.builder()
                .userId(1L)
                .lastName("LastName")
                .firstName("FirstName")
                .email("TestEmail")
                .roles(List.of(adminRole))
                .build();

        var userFindId = 4L;
        var userFind = User.builder()
                .userId(userFindId)
                .lastName("LastNameFIND")
                .firstName("FirstNameFIND")
                .email("TestEmailFIND")
                .roles(List.of(userRole))
                .build();
        var commandsAndParam = this.COMMAND + " " + this.EMAIL_PARAM + userFind.getEmail();
        when(this.userService.existUserByEmail(eq(userFind.getEmail()))).thenReturn(true);
        when(this.userService.getUserByEmail(eq(userFind.getEmail()))).thenReturn(userFind);

        var run = this.userCommand.run(commandsAndParam, user);

        assertThat(run).isNotNull();
        verify(this.userService, times(0)).existUser(any());
        verify(this.userService, times(0)).getUserById(any());
        verify(this.userService, times(1)).existUserByEmail(any());
        verify(this.userService, times(1)).existUserByEmail(any());

    }
    @Test
    @DisplayName("Проверяет, что при неизвестной роли выходит из метода ")
    void run_unknownRole() {
        var unknownRole = Role.builder().roleName("UNKNOWN").build();

        var user = User.builder()
                .userId(1L)
                .lastName("LastName")
                .firstName("FirstName")
                .email("TestEmail")
                .roles(List.of(unknownRole))
                .build();

        var commandsAndParam = this.COMMAND + " " + this.EMAIL_PARAM + "TestEmailFIND";


        var run = this.userCommand.run(commandsAndParam, user);

        assertThat(run).isNull();
        verify(this.userService, times(0)).existUser(any());
        verify(this.userService, times(0)).getUserById(any());
        verify(this.userService, times(0)).existUserByEmail(any());
        verify(this.userService, times(0)).existUserByEmail(any());

    }
    @Test
    @DisplayName("Проверяет, что при не авторизованном пользователе выходит из метода ")
    void run_nullRole() {

        var commandsAndParam = this.COMMAND + " " + this.EMAIL_PARAM + "TestEmailFIND";


        var run = this.userCommand.run(commandsAndParam, null);

        assertThat(run).isNull();
        verify(this.userService, times(0)).existUser(any());
        verify(this.userService, times(0)).getUserById(any());
        verify(this.userService, times(0)).existUserByEmail(any());
        verify(this.userService, times(0)).existUserByEmail(any());

    }

    @Test
    @DisplayName("Проверяет доступность команды хелп для пользователя с ролью юзер")
    void getHelpCommand() {
        var userRole = List.of(Role.builder().roleName("USER").build());

        var helpCommand = this.userCommand.getHelpCommand(userRole);

        assertThat(helpCommand).contains(this.COMMAND).doesNotContain(this.EMAIL_PARAM, this.ID_PARAM);

    }
    @Test
    @DisplayName("Проверяет доступность команды хелп для пользователя с ролью админ")
    void getHelpCommand_Admin() {
        var userRole = List.of(Role.builder().roleName("ADMIN").build());

        var helpCommand = this.userCommand.getHelpCommand(userRole);

        assertThat(helpCommand).contains(this.COMMAND, this.EMAIL_PARAM, this.ID_PARAM);

    }
    @Test
    @DisplayName("Проверяет доступность команды хелп для пользователя с неизвестной ролью")
    void getHelpCommand_unknownRole() {
        var userRole = List.of(Role.builder().roleName("UNKNOWN").build());

        var helpCommand = this.userCommand.getHelpCommand(userRole);

        assertThat(helpCommand).isNull();

    }
    @Test
    @DisplayName("Проверяет доступность команды хелп для пользователя без роли")
    void getHelpCommand_emptyRole() {

        var helpCommand = this.userCommand.getHelpCommand(List.of());

        assertThat(helpCommand).isNull();

    }
    @Test
    @DisplayName("Проверяет доступность команды хелп для пользователя с ролью null")
    void getHelpCommand_nullRole() {

        var helpCommand = this.userCommand.getHelpCommand(null);

        assertThat(helpCommand).isNull();

    }
    @Test
    @DisplayName("Проверяет, что выводит сообщение")
    void getMakesAction(){
        var makesAction = this.userCommand.getMakesAction();
        assertThat(makesAction).isNotNull();
    }
}