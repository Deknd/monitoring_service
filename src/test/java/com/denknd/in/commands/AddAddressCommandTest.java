package com.denknd.in.commands;

import com.denknd.entity.Address;
import com.denknd.entity.Role;
import com.denknd.entity.User;
import com.denknd.services.AddressService;
import com.denknd.validator.Validators;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AddAddressCommandTest {

    private AddAddressCommand addAddressCommand;
    private AddressService addressService;
    private Validators validators;
    private Scanner scanner;

    @BeforeEach
    void setUp() {
        this.addressService = mock(AddressService.class);
        this.validators = mock(Validators.class);
        this.scanner = mock(Scanner.class);
        this.addAddressCommand = new AddAddressCommand(this.addressService, this.validators, this.scanner);
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
        var user = mock(User.class);
        var region = "region";
        var city = "city";
        var street = " street";
        var house = "house";
        var apartment = "apartment";
        var postalCode = "123456";
        when(this.validators.isValid(any(),any(),any(),any())).thenReturn(region).thenReturn(city).thenReturn(street).thenReturn(house).thenReturn(apartment).thenReturn(postalCode);
        when(this.validators.notNullValue(any(String[].class))).thenReturn(true);
        when(this.addressService.addAddressByUser(any())).thenReturn(mock(Address.class));
        when(user.getAddresses()).thenReturn(null);

        var run = this.addAddressCommand.run(command, user);

        assertThat(run).contains("Адрес добавлен");
        var addressCapture = ArgumentCaptor.forClass(Address.class);
        verify(this.addressService, times(1)).addAddressByUser(addressCapture.capture());
        var addressCaptureValue = addressCapture.getValue();
        assertThat(addressCaptureValue.getRegion()).isEqualTo(region);
        assertThat(addressCaptureValue.getCity()).isEqualTo(city);
        assertThat(addressCaptureValue.getStreet()).isEqualTo(street);
        assertThat(addressCaptureValue.getHouse()).isEqualTo(house);
        assertThat(addressCaptureValue.getApartment()).isEqualTo(apartment);
        assertThat(String.valueOf(addressCaptureValue.getPostalCode())).isEqualTo(postalCode);
        assertThat(addressCaptureValue.getOwner()).isEqualTo(user);
    }

    @Test
    @DisplayName("проверяет что данная команда вызывает сервис с полностью собранным адресом и создает новый лист адресов")
    void run_newList() {
        var command = "add_addr";
        var user = mock(User.class);
        var region = "region";
        var city = "city";
        var street = " street";
        var house = "house";
        var apartment = "apartment";
        var postalCode = "123456";
        when(this.validators.isValid(any(),any(),any(),any())).thenReturn(region).thenReturn(city).thenReturn(street).thenReturn(house).thenReturn(apartment).thenReturn(postalCode);
        when(this.validators.notNullValue(any(String[].class))).thenReturn(true);
        when(this.addressService.addAddressByUser(any())).thenReturn(mock(Address.class));

        var run = this.addAddressCommand.run(command, user);

        assertThat(run).isNotNull();
        verify(this.validators, times(6)).isValid(any(),any(),any(),any());
        var addressCapture = ArgumentCaptor.forClass(Address.class);
        verify(this.addressService, times(1)).addAddressByUser(addressCapture.capture());
        var addressCaptureValue = addressCapture.getValue();
        assertThat(addressCaptureValue.getRegion()).isEqualTo(region);
        assertThat(addressCaptureValue.getCity()).isEqualTo(city);
        assertThat(addressCaptureValue.getStreet()).isEqualTo(street);
        assertThat(addressCaptureValue.getHouse()).isEqualTo(house);
        assertThat(addressCaptureValue.getApartment()).isEqualTo(apartment);
        assertThat(String.valueOf(addressCaptureValue.getPostalCode())).isEqualTo(postalCode);
        assertThat(addressCaptureValue.getOwner()).isEqualTo(user);
    }
    @Test
    @DisplayName("проверяет что при вводе не корректных данных, метод не вызывается addressService")
    void run_failedValue() {
        var command = "add_addr";
        var user = mock(User.class);
        when(this.validators.notNullValue(any(String[].class))).thenReturn(false);

        var run = this.addAddressCommand.run(command, user);

        assertThat(run).isNotNull();
        verify(this.validators, times(6)).isValid(any(),any(),any(),any());
        verify(this.addressService, times(0)).addAddressByUser(any());
    }
    @Test
    @DisplayName("проверяет что при вводе дополнительных параметров, сервис не вызывается")
    void run_failedParam() {
        var command = "add_addr sdsd";
        var user = mock(User.class);

        var run = this.addAddressCommand.run(command, user);

        assertThat(run).isNotNull();
        verify(this.validators, times(0)).isValid(any(),any(),any(),any());
        verify(this.addressService, times(0)).addAddressByUser(any());
    }
    @Test
    @DisplayName("проверяет что при вызове не авторизированным, сервис не вызывается")
    void run_notUser() {
        var command = "add_addr";

        var run = this.addAddressCommand.run(command, null);

        assertThat(run).isNull();
        verify(this.validators, times(0)).isValid(any(),any(),any(),any());
        verify(this.addressService, times(0)).addAddressByUser(any());
    }

    @Test
    @DisplayName("Проверяет, что подсказка доступна пользователю с ролью ЮЗЕР")
    void getHelpCommand() {
        var command = "add_addr";
        var roles = List.of(Role.builder().roleName("USER").build());

        var helpCommand = this.addAddressCommand.getHelpCommand(roles);
        assertThat(helpCommand).contains(command);
    }
    @Test
    @DisplayName("Проверяет, что подсказка не доступна пользователю без роли")
    void getHelpCommand_notRole() {
        var command = "add_addr";
        List<Role> roles = List.of();

        var helpCommand = this.addAddressCommand.getHelpCommand(roles);
        assertThat(helpCommand).isNull();
    }
    @Test
    @DisplayName("Проверяет, что подсказка не доступна пользователю другой роли")
    void getHelpCommand_randomRole() {
        var roles = List.of(Role.builder().roleName("DSFF").build());

        var helpCommand = this.addAddressCommand.getHelpCommand(roles);
        assertThat(helpCommand).isNull();
    }
    @Test
    @DisplayName("Проверяет, что выводит сообщение")
    void getMakesAction(){
        var makesAction = this.addAddressCommand.getMakesAction();
        assertThat(makesAction).isNotNull();
    }
}