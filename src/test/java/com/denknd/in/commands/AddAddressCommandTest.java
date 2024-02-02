package com.denknd.in.commands;

import com.denknd.controllers.AddressController;
import com.denknd.dto.AddressDto;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AddAddressCommandTest {

  private AddAddressCommand addAddressCommand;
  @Mock
  private AddressController addressController;
  @Mock
  private DataValidatorManager dataValidatorManager;
  private AutoCloseable closeable;


  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.addAddressCommand = new AddAddressCommand(this.addressController, this.dataValidatorManager);
  }
  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }
  @Test
  @DisplayName("Проверяет, что возвращается ожидаемый код")
  void getCommand() {
    var command = "add_addr";

    var addressCommand = this.addAddressCommand.getCommand();

    assertThat(addressCommand).isEqualTo(command);
  }

  @Test
  @DisplayName("проверяет что данная команда вызывает сервис с полностью собранным адресом")
  void run() {
    var command = "add_addr";
    var userSecurity = UserSecurity.builder().role(Roles.USER).build();
    var region = "region";
    var city = "city";
    var street = " street";
    var house = "house";
    var apartment = "apartment";
    var postalCode = "123456";
    when(this.dataValidatorManager.getValidInput(any(), any(), any())).thenReturn(region).thenReturn(city).thenReturn(street).thenReturn(house).thenReturn(apartment).thenReturn(postalCode);
    when(this.dataValidatorManager.areAllValuesNotNullAndNotEmpty(any(String[].class))).thenReturn(true);
    when(this.addressController.addAddress(any(), any())).thenReturn(mock(AddressDto.class));

    var run = this.addAddressCommand.run(command, userSecurity);

    assertThat(run).contains("Адрес добавлен");
    var addressCapture = ArgumentCaptor.forClass(AddressDto.class);
    verify(this.addressController, times(1)).addAddress(addressCapture.capture(), any());
    var addressCaptureValue = addressCapture.getValue();
    assertThat(addressCaptureValue.region()).isEqualTo(region);
    assertThat(addressCaptureValue.city()).isEqualTo(city);
    assertThat(addressCaptureValue.street()).isEqualTo(street);
    assertThat(addressCaptureValue.house()).isEqualTo(house);
    assertThat(addressCaptureValue.apartment()).isEqualTo(apartment);
    assertThat(String.valueOf(addressCaptureValue.postalCode())).isEqualTo(postalCode);
  }

  @Test
  @DisplayName("проверяет что данная команда вызывает сервис с полностью собранным адресом и создает новый лист адресов")
  void run_newList() {
    var command = "add_addr";
    var userSecurity = UserSecurity.builder().role(Roles.USER).build();
    var region = "region";
    var city = "city";
    var street = " street";
    var house = "house";
    var apartment = "apartment";
    var postalCode = "123456";
    when(this.dataValidatorManager.getValidInput(any(), any(), any())).thenReturn(region).thenReturn(city).thenReturn(street).thenReturn(house).thenReturn(apartment).thenReturn(postalCode);
    when(this.dataValidatorManager.areAllValuesNotNullAndNotEmpty(any(String[].class))).thenReturn(true);
    when(this.addressController.addAddress(any(), any())).thenReturn(mock(AddressDto.class));


    var run = this.addAddressCommand.run(command, userSecurity);

    assertThat(run).isNotNull();
    verify(this.dataValidatorManager, times(6)).getValidInput(any(), any(), any());
    var addressCapture = ArgumentCaptor.forClass(AddressDto.class);
    verify(this.addressController, times(1)).addAddress(addressCapture.capture(), any());
    var addressCaptureValue = addressCapture.getValue();
    assertThat(addressCaptureValue.region()).isEqualTo(region);
    assertThat(addressCaptureValue.city()).isEqualTo(city);
    assertThat(addressCaptureValue.street()).isEqualTo(street);
    assertThat(addressCaptureValue.house()).isEqualTo(house);
    assertThat(addressCaptureValue.apartment()).isEqualTo(apartment);
    assertThat(String.valueOf(addressCaptureValue.postalCode())).isEqualTo(postalCode);
  }


  @Test
  @DisplayName("проверяет что при вводе не корректных данных, метод не вызывается addressService")
  void run_failedValue() {
    var command = "add_addr";
    var user = UserSecurity.builder().role(Roles.USER).build();
    when(this.dataValidatorManager.areAllValuesNotNullAndNotEmpty(any(String[].class))).thenReturn(false);

    var run = this.addAddressCommand.run(command, user);

    assertThat(run).isNotNull();
    verify(this.dataValidatorManager, times(6)).getValidInput(any(), any(), any());
    verify(this.addressController, times(0)).addAddress(any(), any());
  }

  @Test
  @DisplayName("проверяет что при вводе дополнительных параметров, сервис не вызывается")
  void run_failedParam() {
    var command = "add_addr sdsd";
    var user = mock(UserSecurity.class);

    var run = this.addAddressCommand.run(command, user);

    assertThat(run).isNotNull();
    verify(this.dataValidatorManager, times(0)).getValidInput(any(), any(), any());
    verify(this.addressController, times(0)).addAddress(any(), any());
  }

  @Test
  @DisplayName("проверяет что при вызове не авторизированным, сервис не вызывается")
  void run_notUser() {
    var command = "add_addr";

    var run = this.addAddressCommand.run(command, null);

    assertThat(run).isNull();
    verify(this.dataValidatorManager, times(0)).getValidInput(any(), any(), any());
    verify(this.addressController, times(0)).addAddress(any(), any());
  }

  @Test
  @DisplayName("Проверяет, что подсказка доступна пользователю с ролью ЮЗЕР")
  void getHelpCommand() {
    var command = "add_addr";
    var roles = Roles.USER;

    var helpCommand = this.addAddressCommand.getHelpCommand(roles);
    assertThat(helpCommand).contains(command);
  }

  @Test
  @DisplayName("Проверяет, что подсказка не доступна пользователю без роли")
  void getHelpCommand_notRole() {

    var helpCommand = this.addAddressCommand.getHelpCommand(null);
    assertThat(helpCommand).isNull();
  }

  @Test
  @DisplayName("Проверяет, что выводит сообщение")
  void getMakesAction() {
    var makesAction = this.addAddressCommand.getAuditActionDescription();
    assertThat(makesAction).isNotNull();
  }
}