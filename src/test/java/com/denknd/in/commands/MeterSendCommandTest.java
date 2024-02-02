package com.denknd.in.commands;

import com.denknd.controllers.AddressController;
import com.denknd.controllers.MeterReadingController;
import com.denknd.controllers.TypeMeterController;
import com.denknd.dto.AddressDto;
import com.denknd.dto.MeterReadingRequestDto;
import com.denknd.dto.TypeMeterDto;
import com.denknd.entity.Roles;
import com.denknd.exception.MeterReadingConflictError;
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
import java.util.Set;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MeterSendCommandTest {
    @Mock
    private TypeMeterController typeMeterController;
    @Mock
    private AddressController addressController;
    @Mock
    private MeterReadingController meterReadingController;
    @Mock
    private DataValidatorManager dataValidatorManager;
    private MeterSendCommand meterSendCommand;
    @Mock
    private Function<String[], Set<String>> typeMeterParametersParserFromRawParameters;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        this.closeable = MockitoAnnotations.openMocks(this);
        this.meterSendCommand = new MeterSendCommand(this.typeMeterController, this.addressController, this.meterReadingController, this.dataValidatorManager);
        this.meterSendCommand.setTypeMeterParametersParserFromRawParameters(this.typeMeterParametersParserFromRawParameters);
    }
    @AfterEach
    void tearDown() throws Exception {
        this.closeable.close();
    }
    @Test
    @DisplayName("Проверяет, что возвращаемая команда актуальна")
    void getCommand() {
        var command = "send";

        var sendCommand = this.meterSendCommand.getCommand();

        assertThat(sendCommand).isEqualTo(command);
    }

    @Test
    @DisplayName("Проверяется, что вызывается метод сервиса и в него передается актуальный объект с показаниями")
    void run() throws MeterReadingConflictError {
        var command = "send";
        var param = "param";
        var user = UserSecurity.builder().role(Roles.USER).build();
        var address = AddressDto.builder().addressId(1L).build();
        when(this.addressController.getAddress(any())).thenReturn(List.of(address));
        when(this.typeMeterParametersParserFromRawParameters.apply(any())).thenReturn(Set.of(param));
        var meterValue = "12323.324";
        when(this.dataValidatorManager.getValidInput(any(), any(), any())).thenReturn("1").thenReturn(meterValue);

        this.meterSendCommand.run(command + " " + param, user);

        var meterReadingCaptor = ArgumentCaptor.forClass(MeterReadingRequestDto.class);
        verify(this.addressController, times(1)).getAddress(any());
        verify(this.typeMeterParametersParserFromRawParameters, times(1)).apply(any());
        verify(this.dataValidatorManager, times(2)).getValidInput(any(), any(), any());
        verify(this.meterReadingController, times(1)).addMeterReadingValue(meterReadingCaptor.capture(), any());
        var meterReading = meterReadingCaptor.getValue();
        assertThat(meterReading.addressId()).isEqualTo(1L);
        assertThat(String.valueOf(meterReading.meterValue())).isEqualTo(meterValue);
        assertThat(meterReading.code()).isEqualTo(param);

    }

    @Test
    @DisplayName("Проверяется, что обрабатывается ошибка полученная от meterReadingService")
    void run_noValidMeter() throws MeterReadingConflictError {
        var command = "send";
        var param = "param";
        var user = UserSecurity.builder().userId(2L).role(Roles.USER).build();
        var address = AddressDto.builder().addressId(1L).build();
        when(this.addressController.getAddress(eq(user.userId()))).thenReturn(List.of(address));
        when(this.typeMeterParametersParserFromRawParameters.apply(any(String[].class))).thenReturn(Set.of(param));
        var meterValue = "12323.324";
        when(this.dataValidatorManager.getValidInput(any(), any(), any())).thenReturn("1").thenReturn(meterValue);
        when(this.meterReadingController.addMeterReadingValue(any(MeterReadingRequestDto.class), any())).thenThrow(MeterReadingConflictError.class);

        this.meterSendCommand.run(command + " " + param, user);

        var meterReadingCaptor = ArgumentCaptor.forClass(MeterReadingRequestDto.class);
        verify(this.addressController, times(1)).getAddress(any());
        verify(this.typeMeterParametersParserFromRawParameters, times(1)).apply(any());
        verify(this.dataValidatorManager, times(2)).getValidInput(any(), any(), any());
        verify(this.meterReadingController, times(1)).addMeterReadingValue(meterReadingCaptor.capture(), any());
        var meterReading = meterReadingCaptor.getValue();
        assertThat(meterReading.addressId()).isEqualTo(1L);
        assertThat(String.valueOf(meterReading.meterValue())).isEqualTo(meterValue);
        assertThat(meterReading.code()).isEqualTo(param);


    }

    @Test
    @DisplayName("Проверяется, что выходит из метода, когда не вводятся показания счетчика")
    void run_noMeter() throws MeterReadingConflictError {
        var command = "send";
        var param = "param";
        var user = UserSecurity.builder().userId(2L).role(Roles.USER).build();
        var address = AddressDto.builder().addressId(1L).build();
        when(this.addressController.getAddress(eq(user.userId()))).thenReturn(List.of(address));
        when(this.typeMeterParametersParserFromRawParameters.apply(any())).thenReturn(Set.of(param));
        when(this.dataValidatorManager.getValidInput(any(), any(), any())).thenReturn("1").thenReturn(null);

        this.meterSendCommand.run(command + " " + param, user);

        verify(this.addressController, times(1)).getAddress(any());
        verify(this.typeMeterParametersParserFromRawParameters, times(1)).apply(any());
        verify(this.dataValidatorManager, times(2)).getValidInput(any(), any(), any());
        verify(this.meterReadingController, times(0)).addMeterReadingValue(any(), any());

    }



    @Test
    @DisplayName("Проверяется, что выходит из метода, если вводить не число в адрес айди")
    void run_failedAddressId() throws MeterReadingConflictError {
        var command = "send";
        var param = "param";
        var user = UserSecurity.builder().userId(2L).role(Roles.USER).build();
        var address = AddressDto.builder().addressId(1L).build();
        when(this.addressController.getAddress(eq(user.userId()))).thenReturn(List.of(address));
        when(this.typeMeterParametersParserFromRawParameters.apply(any())).thenReturn(Set.of(param));
        when(this.dataValidatorManager.getValidInput(any(), any(), any())).thenReturn("noNumber");

        this.meterSendCommand.run(command + " " + param, user);

        verify(this.addressController, times(1)).getAddress(any());
        verify(this.typeMeterParametersParserFromRawParameters, times(1)).apply(any());
        verify(this.dataValidatorManager, times(1)).getValidInput(any(), any(), any());
        verify(this.meterReadingController, times(0)).addMeterReadingValue(any(), any());
    }

    @Test
    @DisplayName("Проверяется, что выходит из метода, когда у пользователя не добавлено ни одного адреса")
    void run_noAddress() throws MeterReadingConflictError {
        var command = "send";
        var param = "param";
        var user = mock(UserSecurity.class);
        when(user.role()).thenReturn(Roles.USER);
        when(this.typeMeterParametersParserFromRawParameters.apply(any())).thenReturn(Set.of(param));

        this.meterSendCommand.run(command + " " + param, user);

        verify(this.addressController, times(1)).getAddress(any());
        verify(this.typeMeterParametersParserFromRawParameters, times(1)).apply(any());
        verify(this.dataValidatorManager, times(0)).getValidInput(any(), any(), any());
        verify(this.meterReadingController, times(0)).addMeterReadingValue(any(), any());
    }

    @Test
    @DisplayName("Проверяется, что выходит из метода, когда у пользователя не Роль ЮЗЕР")
    void run_noRoleUser() throws MeterReadingConflictError {
        var command = "send";
        var param = "param";
        var user = mock(UserSecurity.class);
        when(user.role()).thenReturn(Roles.ADMIN);

        this.meterSendCommand.run(command + " " + param, user);

        verify(this.addressController, times(0)).getAddress(any());
        verify(this.typeMeterParametersParserFromRawParameters, times(0)).apply(any());
        verify(this.dataValidatorManager, times(0)).getValidInput(any(), any(), any());
        verify(this.meterReadingController, times(0)).addMeterReadingValue(any(), any());
    }

    @Test
    @DisplayName("Проверяется, что выходит из метода, когда пользователь не авторизован")
    void run_noRole() throws MeterReadingConflictError {
        var command = "send";
        var param = "param";

        this.meterSendCommand.run(command + " " + param, null);

        verify(this.addressController, times(0)).getAddress(any());
        verify(this.typeMeterParametersParserFromRawParameters, times(0)).apply(any());
        verify(this.dataValidatorManager, times(0)).getValidInput(any(), any(), any());
        verify(this.meterReadingController, times(0)).addMeterReadingValue(any(), any());
    }


    @Test
    @DisplayName("Проверяется, что подсказка выводиться для пользователя с ролью ЮЗЕР")
    void getHelpCommand() {
        var command = "send";
        var role = Roles.USER;
        var type1 = TypeMeterDto.builder().typeCode("test1").typeDescription("description1").build();
        var type2 = TypeMeterDto.builder().typeCode("test2").typeDescription("description2").build();
        when(this.typeMeterController.getTypeMeterCodes()).thenReturn(Set.of(type1, type2));

        var helpCommand = this.meterSendCommand.getHelpCommand(role);

        assertThat(helpCommand).contains(command, type1.typeCode(), type1.typeDescription(), type2.typeCode(), type2.typeDescription());

    }
    @Test
    @DisplayName("Проверяется, что подсказка не выводиться для пользователя с ролью АДМИН")
    void getHelpCommand_RoleAdmin() {

        var role = Roles.ADMIN;

        var helpCommand = this.meterSendCommand.getHelpCommand(role);

        assertThat(helpCommand).isNull();

    }
    @Test
    @DisplayName("Проверяется, что подсказка не выводиться для пользователя без роли")
    void getHelpCommand_noRole() {


        var helpCommand = this.meterSendCommand.getHelpCommand(null);

        assertThat(helpCommand).isNull();

    }
    @Test
    @DisplayName("Проверяет, что выводит сообщение")
    void getMakesAction(){
        var makesAction = this.meterSendCommand.getAuditActionDescription();
        assertThat(makesAction).isNotNull();
    }
}