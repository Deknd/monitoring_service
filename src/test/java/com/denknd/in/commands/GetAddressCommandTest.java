package com.denknd.in.commands;

import com.denknd.entity.Address;
import com.denknd.entity.Role;
import com.denknd.entity.User;
import com.denknd.in.commands.functions.MyFunction;
import com.denknd.services.AddressService;
import com.denknd.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GetAddressCommandTest {
    private final String COMMAND = "get_addr";
    private final String USER_ID_PARAMETER = "user=";
    private AddressService addressService;
    private UserService userService;
    private MyFunction<String[], Long> longIdParserFromRawParameters;
    private GetAddressCommand getAddressCommand;
    @BeforeEach
    void setUp() {
        this.addressService = mock(AddressService.class);
        this.userService = mock(UserService.class);
        this.longIdParserFromRawParameters = mock(MyFunction.class);
        this.getAddressCommand = new GetAddressCommand(this.addressService, this.userService);
        this.getAddressCommand.setLongIdParserFromRawParameters(this.longIdParserFromRawParameters);
    }

    @Test
    @DisplayName("Проверяет, что команда ожидаемая")
    void getCommand() {
        var command = this.getAddressCommand.getCommand();

        assertThat(command).isEqualTo(this.COMMAND);
    }

    @Test
    @DisplayName("Проверяет, что возвращает пользователю с ролью Юзер его адреса и вызывает нужные сервисы")
    void run() {
        var user = mock(User.class);
        var userRole = Role.builder().roleName("USER").build();
        when(user.getRoles()).thenReturn(List.of(userRole));
        when(this.addressService.getAddresses(any())).thenReturn(List.of(mock(Address.class)));

        var run = this.getAddressCommand.run(this.COMMAND, user);

        assertThat(run).isNotNull();
        verify(this.addressService, times(1)).getAddresses(any());
        verify(this.longIdParserFromRawParameters, times(0)).apply(any(), any());
        verify(this.userService, times(0)).existUser(any());

    }
    @Test
    @DisplayName("Проверяет, что  пользователю с ролью Юзер вызывается сервис")
    void run_notAddress() {
        var user = mock(User.class);
        var userRole = Role.builder().roleName("USER").build();
        when(user.getRoles()).thenReturn(List.of(userRole));

        var run = this.getAddressCommand.run(this.COMMAND, user);

        assertThat(run).isNotNull();
        verify(this.addressService, times(1)).getAddresses(any());
        verify(this.longIdParserFromRawParameters, times(0)).apply(any(), any());
        verify(this.userService, times(0)).existUser(any());

    }

    @Test
    @DisplayName("Проверяет, что  пользователю с ролью админ вызываются все нужные сервисы, когда есть адреса")
    void run_Admin() {
        var user = mock(User.class);
        var userRole = Role.builder().roleName("ADMIN").build();
        when(user.getRoles()).thenReturn(List.of(userRole));
        when(this.longIdParserFromRawParameters.apply(any(), any())).thenReturn(12L);
        when(this.userService.existUser(any())).thenReturn(true);
        when(this.addressService.getAddresses(any())).thenReturn(List.of(mock(Address.class)));

        var run = this.getAddressCommand.run(this.COMMAND + " "+this.USER_ID_PARAMETER+"12", user);

        assertThat(run).isNotNull();
        verify(this.addressService, times(1)).getAddresses(any());
        verify(this.longIdParserFromRawParameters, times(1)).apply(any(), any());
        verify(this.userService, times(1)).existUser(any());
    }

    @Test
    @DisplayName("Проверяет, что  пользователю с ролью админ вызываются все нужные сервисы, когда есть нет адреса")
    void run_AdminNotAddress() {
        var user = mock(User.class);
        var userRole = Role.builder().roleName("ADMIN").build();
        when(user.getRoles()).thenReturn(List.of(userRole));
        when(this.longIdParserFromRawParameters.apply(any(), any())).thenReturn(12L);
        when(this.userService.existUser(any())).thenReturn(true);

        var run = this.getAddressCommand.run(this.COMMAND + " "+this.USER_ID_PARAMETER+"12", user);

        assertThat(run).isNotNull();
        verify(this.addressService, times(1)).getAddresses(any());
        verify(this.longIdParserFromRawParameters, times(1)).apply(any(), any());
        verify(this.userService, times(1)).existUser(any());
    }

    @Test
    @DisplayName("Проверяет, что если пользователя с введенным айди не существует, не обращается в адресСервис")
    void run_AdminNotUser() {
        var user = mock(User.class);
        var userRole = Role.builder().roleName("ADMIN").build();
        when(user.getRoles()).thenReturn(List.of(userRole));
        when(this.longIdParserFromRawParameters.apply(any(), any())).thenReturn(12L);
        when(this.userService.existUser(any())).thenReturn(false);

        var run = this.getAddressCommand.run(this.COMMAND + " " + this.USER_ID_PARAMETER + "12", user);

        assertThat(run).isNotNull();
        verify(this.addressService, times(0)).getAddresses(any());
        verify(this.longIdParserFromRawParameters, times(1)).apply(any(), any());
        verify(this.userService, times(1)).existUser(any());
    }
    @Test
    @DisplayName("Проверяет, что пользователя с неизвестной ролью выкидывает из метода и сервисы не вызыватся")
    void run_unknownRole() {
        var user = mock(User.class);
        var userRole = Role.builder().roleName("UNKNOWN").build();
        when(user.getRoles()).thenReturn(List.of(userRole));


        var run = this.getAddressCommand.run(this.COMMAND + " " + this.USER_ID_PARAMETER + "12", user);

        assertThat(run).isNull();
        verify(this.addressService, times(0)).getAddresses(any());
        verify(this.longIdParserFromRawParameters, times(0)).apply(any(), any());
        verify(this.userService, times(0)).existUser(any());
    }

    @Test
    @DisplayName("Проверяет, что пользователя без авторизованного пользователя выкидывает из метода")
    void run_notUser() {


        var run = this.getAddressCommand.run(this.COMMAND + " " + this.USER_ID_PARAMETER + "12", null);

        assertThat(run).isNull();
        verify(this.addressService, times(0)).getAddresses(any());
        verify(this.longIdParserFromRawParameters, times(0)).apply(any(), any());
        verify(this.userService, times(0)).existUser(any());
    }
    @Test
    @DisplayName("Проверяет, что подсказка соответствует ожидаемой")
    void getHelpCommand() {
        var userRole = List.of(Role.builder().roleName("USER").build());

        var helpCommand = this.getAddressCommand.getHelpCommand(userRole);

        assertThat(helpCommand).contains(this.COMMAND).doesNotContain(this.USER_ID_PARAMETER);

    }

    @Test
    @DisplayName("Проверяет, что подсказка доступна для админа")
    void getHelpCommand_Admin() {
        var userRole = List.of(Role.builder().roleName("ADMIN").build());

        var helpCommand = this.getAddressCommand.getHelpCommand(userRole);

        assertThat(helpCommand).contains(this.COMMAND, this.USER_ID_PARAMETER);

    }
    @Test
    @DisplayName("Проверяет, что подсказка не доступны для не известной роли")
    void getHelpCommand_unknown() {
        var userRole = List.of(Role.builder().roleName("unknown").build());

        var helpCommand = this.getAddressCommand.getHelpCommand(userRole);

        assertThat(helpCommand).isNull();

    }
    @Test
    @DisplayName("Проверяет, что подсказка не доступны для пользователя без роли")
    void getHelpCommand_empty() {

        var helpCommand = this.getAddressCommand.getHelpCommand(List.of());

        assertThat(helpCommand).isNull();

    }
    @Test
    @DisplayName("Проверяет, что подсказка не доступны для пользователя с ролями null")
    void getHelpCommand_null() {

        var helpCommand = this.getAddressCommand.getHelpCommand(null);

        assertThat(helpCommand).isNull();

    }
    @Test
    @DisplayName("Проверяет, что выводит сообщение")
    void getMakesAction(){
        var makesAction = this.getAddressCommand.getMakesAction();
        assertThat(makesAction).isNotNull();
    }
}