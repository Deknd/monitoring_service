package com.denknd.in.commands;

import com.denknd.controllers.AddressController;
import com.denknd.dto.AddressDto;
import com.denknd.entity.Roles;
import com.denknd.in.commands.functions.MyFunction;
import com.denknd.security.UserSecurity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GetAddressCommandTest {
  private final String COMMAND = "get_addr";
  private final String USER_ID_PARAMETER = "user=";
  @Mock
  private AddressController addressController;
  @Mock
  private MyFunction<String[], Long> longIdParserFromRawParameters;
  private GetAddressCommand getAddressCommand;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.getAddressCommand = new GetAddressCommand(this.addressController);
    this.getAddressCommand.setLongIdParserFromRawParameters(this.longIdParserFromRawParameters);
  }
  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
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
    var user = mock(UserSecurity.class);
    when(user.role()).thenReturn(Roles.USER);
    when(this.addressController.getAddress(any())).thenReturn(List.of(mock(AddressDto.class)));

    var run = this.getAddressCommand.run(this.COMMAND, user);

    assertThat(run).isNotNull();
    verify(this.addressController, times(1)).getAddress(any());
    verify(this.longIdParserFromRawParameters, times(0)).apply(any(), any());

  }
  @Test
  @DisplayName("Проверяет, что возвращает пользователю с ролью Юзер его адреса и вызывает нужные сервисы, когда нет адресов")
  void run_notAddress() {
    var user = mock(UserSecurity.class);
    when(user.role()).thenReturn(Roles.USER);
    when(this.addressController.getAddress(any())).thenReturn(List.of());

    var run = this.getAddressCommand.run(this.COMMAND, user);

    assertThat(run).isNotNull();
    verify(this.addressController, times(1)).getAddress(any());
    verify(this.longIdParserFromRawParameters, times(0)).apply(any(), any());

  }



  @Test
  @DisplayName("Проверяет, что  пользователю с ролью админ вызываются все нужные сервисы, когда есть адреса")
  void run_Admin() {
    var user = mock(UserSecurity.class);
    when(user.role()).thenReturn(Roles.ADMIN);
    var userId = 12L;
    when(this.longIdParserFromRawParameters.apply(any(), any())).thenReturn(userId);
    when(this.addressController.getAddress(eq(userId))).thenReturn(List.of(mock(AddressDto.class)));

    var run = this.getAddressCommand.run(this.COMMAND + " " + this.USER_ID_PARAMETER + "12", user);

    assertThat(run).isNotNull();
    verify(this.addressController, times(1)).getAddress(eq(userId));
    verify(this.longIdParserFromRawParameters, times(1)).apply(any(), any());
  }
  @Test
  @DisplayName("Проверяет, что  пользователю с ролью админ вызываются все нужные сервисы, когда нет есть адреса")
  void run_Admin_notAddress() {
    var user = mock(UserSecurity.class);
    when(user.role()).thenReturn(Roles.ADMIN);
    var userId = 12L;
    when(this.longIdParserFromRawParameters.apply(any(), any())).thenReturn(userId);
    when(this.addressController.getAddress(eq(userId))).thenReturn(List.of());

    var run = this.getAddressCommand.run(this.COMMAND + " " + this.USER_ID_PARAMETER + "12", user);

    assertThat(run).isNotNull();
    verify(this.addressController, times(1)).getAddress(any());
    verify(this.longIdParserFromRawParameters, times(1)).apply(any(), any());
  }



  @Test
  @DisplayName("Проверяет, что если ввести не верный айди, то не будут вызываться сервисы")
  void run_AdminNotUser() {
    var user = mock(UserSecurity.class);
    when(user.role()).thenReturn(Roles.ADMIN);
    when(this.longIdParserFromRawParameters.apply(any(), any())).thenReturn(null);

    var run = this.getAddressCommand.run(this.COMMAND + " " + this.USER_ID_PARAMETER + "12", user);

    assertThat(run).isNotNull();
    verify(this.addressController, times(0)).getAddress(any());
    verify(this.longIdParserFromRawParameters, times(1)).apply(any(), any());
  }

  @Test
  @DisplayName("Проверяет, что пользователя с неизвестной ролью выкидывает из метода и сервисы не вызыватся")
  void run_unknownRole() {
    var user = mock(UserSecurity.class);
    when(user.role()).thenReturn(null);


    var run = this.getAddressCommand.run(this.COMMAND + " " + this.USER_ID_PARAMETER + "12", user);

    assertThat(run).isNull();
    verify(this.addressController, times(0)).getAddress(any());
    verify(this.longIdParserFromRawParameters, times(0)).apply(any(), any());
  }

  @Test
  @DisplayName("Проверяет, что пользователя без авторизованного пользователя выкидывает из метода")
  void run_notUser() {


    var run = this.getAddressCommand.run(this.COMMAND + " " + this.USER_ID_PARAMETER + "12", null);

    assertThat(run).isNull();
    verify(this.addressController, times(0)).getAddress(any());
    verify(this.longIdParserFromRawParameters, times(0)).apply(any(), any());
  }

  @Test
  @DisplayName("Проверяет, что подсказка соответствует ожидаемой")
  void getHelpCommand() {
    var userRole = Roles.USER;

    var helpCommand = this.getAddressCommand.getHelpCommand(userRole);

    assertThat(helpCommand).contains(this.COMMAND).doesNotContain(this.USER_ID_PARAMETER);

  }

  @Test
  @DisplayName("Проверяет, что подсказка доступна для админа")
  void getHelpCommand_Admin() {
    var userRole = Roles.ADMIN;

    var helpCommand = this.getAddressCommand.getHelpCommand(userRole);

    assertThat(helpCommand).contains(this.COMMAND, this.USER_ID_PARAMETER);

  }

  @Test
  @DisplayName("Проверяет, что подсказка не доступны для пользователя с ролями null")
  void getHelpCommand_null() {

    var helpCommand = this.getAddressCommand.getHelpCommand(null);

    assertThat(helpCommand).isNull();
  }

  @Test
  @DisplayName("Проверяет, что выводит сообщение")
  void getMakesAction() {
    var makesAction = this.getAddressCommand.getAuditActionDescription();

    assertThat(makesAction).isNotNull();
  }
}