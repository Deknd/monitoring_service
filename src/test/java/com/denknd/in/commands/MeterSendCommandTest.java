package com.denknd.in.commands;

import com.denknd.entity.*;
import com.denknd.services.AddressService;
import com.denknd.services.MeterReadingService;
import com.denknd.services.TypeMeterService;
import com.denknd.validator.Validators;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MeterSendCommandTest {

    private TypeMeterService typeMeterService;
    private AddressService addressService;
    private MeterReadingService meterReadingService;
    private Validators validators;
    private Scanner scanner;
    private MeterSendCommand meterSendCommand;
    private Function<String[], Set<String>> typeMeterParametersParserFromRawParameters;

    @BeforeEach
    void setUp() {
        this.typeMeterService = mock(TypeMeterService.class);
        this.addressService = mock(AddressService.class);
        this.meterReadingService = mock(MeterReadingService.class);
        this.validators = mock(Validators.class);
        this.scanner = mock(Scanner.class);
        this.typeMeterParametersParserFromRawParameters = mock(Function.class);
        this.meterSendCommand = new MeterSendCommand(this.typeMeterService, this.addressService, this.meterReadingService, this.validators, this.scanner);
        this.meterSendCommand.setTypeMeterParametersParserFromRawParameters(this.typeMeterParametersParserFromRawParameters);
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
    void run() {
        var command = "send";
        var param = "param";
        var user = mock(User.class);
        when(user.getRoles()).thenReturn(List.of(Role.builder().roleName("USER").build()));
        var address = mock(Address.class);
        when(address.getAddressId()).thenReturn(1L);
        when(this.addressService.getAddresses(any())).thenReturn(List.of(address));
        when(this.typeMeterParametersParserFromRawParameters.apply(any())).thenReturn(Set.of(param));
        var meterValue = "12323.324";
        when(this.validators.isValid(any(), any(), any(), any())).thenReturn("1").thenReturn(meterValue);
        var typeMeter = TypeMeter.builder().typeCode(param).build();
        when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(typeMeter));

        this.meterSendCommand.run(command + " " + param, user);

        var meterReadingCaptor = ArgumentCaptor.forClass(MeterReading.class);
        verify(this.meterReadingService, times(1)).addMeterValue(meterReadingCaptor.capture());
        var meterReading = meterReadingCaptor.getValue();
        assertThat(meterReading.getAddress()).isEqualTo(address);
        assertThat(meterReading.getTypeMeter()).isEqualTo(typeMeter);
        assertThat(String.valueOf(meterReading.getMeterValue())).isEqualTo(meterValue);
        assertThat(meterReading.getSubmissionMonth()).isEqualTo(YearMonth.now());
        assertThat(meterReading.getMeter()).isNull();
        assertThat(meterReading.getTimeSendMeter()).isBefore(OffsetDateTime.now());

    }

    @Test
    @DisplayName("Проверяется, что выходит из метода, когда старый показания, больше новых")
    void run_noValidMeter() {
        var command = "send";
        var param = "param";
        var user = mock(User.class);
        when(user.getRoles()).thenReturn(List.of(Role.builder().roleName("USER").build()));
        var address = mock(Address.class);
        when(address.getAddressId()).thenReturn(1L);
        when(this.addressService.getAddresses(any())).thenReturn(List.of(address));
        when(this.typeMeterParametersParserFromRawParameters.apply(any())).thenReturn(Set.of(param));
        var meterValue = "12323.324";
        when(this.validators.isValid(any(), any(), any(), any())).thenReturn("1").thenReturn(meterValue);
        var typeMeter = TypeMeter.builder().typeCode(param).build();
        when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(typeMeter));
        when(this.meterReadingService.getActualMeter(any(), any())).thenReturn(MeterReading.builder().meterValue(123123123).submissionMonth(YearMonth.now().minusMonths(1)).build());


        this.meterSendCommand.run(command + " " + param, user);

        verify(this.meterReadingService, times(0)).addMeterValue(any());


    }

    @Test
    @DisplayName("Проверяется, что выходит из метода, когда не вводятся показания счетчика")
    void run_noMeter() {
        var command = "send";
        var param = "param";
        var user = mock(User.class);
        when(user.getRoles()).thenReturn(List.of(Role.builder().roleName("USER").build()));
        var address = mock(Address.class);
        when(address.getAddressId()).thenReturn(1L);
        when(this.addressService.getAddresses(any())).thenReturn(List.of(address));
        when(this.typeMeterParametersParserFromRawParameters.apply(any())).thenReturn(Set.of(param));
        when(this.validators.isValid(any(), any(), any(), any())).thenReturn("1").thenReturn("dasd");
        var typeMeter = TypeMeter.builder().typeCode(param).build();
        when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(typeMeter));
        when(this.meterReadingService.getActualMeter(any(), any())).thenReturn(MeterReading.builder().meterValue(123123123).submissionMonth(YearMonth.now().minusMonths(1)).build());

        this.meterSendCommand.run(command + " " + param, user);

        verify(this.meterReadingService, times(0)).addMeterValue(any());

    }

    @Test
    @DisplayName("Проверяется, что выходит из метода, если данные уже в этом месяце внесены")
    void run_repeatMeter() {
        var command = "send";
        var param = "param";
        var user = mock(User.class);
        when(user.getRoles()).thenReturn(List.of(Role.builder().roleName("USER").build()));
        var address = mock(Address.class);
        when(address.getAddressId()).thenReturn(1L);
        when(this.addressService.getAddresses(any())).thenReturn(List.of(address));
        when(this.typeMeterParametersParserFromRawParameters.apply(any())).thenReturn(Set.of(param));
        when(this.validators.isValid(any(), any(), any(), any())).thenReturn("1").thenReturn("dasd");
        var typeMeter = TypeMeter.builder().typeCode(param).build();
        when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(typeMeter));
        when(this.meterReadingService.getActualMeter(any(), any())).thenReturn(MeterReading.builder().meterValue(123123123).submissionMonth(YearMonth.now()).build());

        this.meterSendCommand.run(command + " " + param, user);

        verify(this.meterReadingService, times(0)).addMeterValue(any());

    }

    @Test
    @DisplayName("Проверяется, что выходит из метода, если водите не свой адрес")
    void run_noOwnerAddress() {
        var command = "send";
        var param = "param";
        var user = mock(User.class);
        when(user.getRoles()).thenReturn(List.of(Role.builder().roleName("USER").build()));
        var address = mock(Address.class);
        when(address.getAddressId()).thenReturn(1L);
        when(this.addressService.getAddresses(any())).thenReturn(List.of(address));
        when(this.typeMeterParametersParserFromRawParameters.apply(any())).thenReturn(Set.of(param));
        var typeMeter = TypeMeter.builder().typeCode(param).build();
        when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(typeMeter));

        this.meterSendCommand.run(command + " " + param, user);

        verify(this.meterReadingService, times(0)).addMeterValue(any());

    }

    @Test
    @DisplayName("Проверяется, что выходит из метода, когда у пользователя не добавлено ни одного адреса")
    void run_noAddress() {
        var command = "send";
        var param = "param";
        var user = mock(User.class);
        when(user.getRoles()).thenReturn(List.of(Role.builder().roleName("USER").build()));

        this.meterSendCommand.run(command + " " + param, user);

        verify(this.meterReadingService, times(0)).addMeterValue(any());

    }

    @Test
    @DisplayName("Проверяется, что выходит из метода, когда у пользователя не Роль ЮЗЕР")
    void run_noRoleUser() {
        var command = "send";
        var param = "param";
        var user = mock(User.class);
        when(user.getRoles()).thenReturn(List.of(Role.builder().roleName("ADMIN").build()));

        this.meterSendCommand.run(command + " " + param, user);

        verify(this.meterReadingService, times(0)).addMeterValue(any());

    }

    @Test
    @DisplayName("Проверяется, что выходит из метода, когда пользователь не авторизован")
    void run_noRole() {
        var command = "send";
        var param = "param";

        this.meterSendCommand.run(command + " " + param, null);

        verify(this.meterReadingService, times(0)).addMeterValue(any());

    }


    @Test
    @DisplayName("Проверяется, что подсказка выводиться для пользователя с ролью ЮЗЕР")
    void getHelpCommand() {
        var command = "send";

        var role = List.of(Role.builder().roleName("USER").build());
        var type1 = TypeMeter.builder().typeCode("test1").typeDescription("description1").build();
        var type2 = TypeMeter.builder().typeCode("test2").typeDescription("description2").build();

        when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(type1, type2));

        var helpCommand = this.meterSendCommand.getHelpCommand(role);

        assertThat(helpCommand).contains(command, type1.getTypeCode(), type1.getTypeDescription(), type2.getTypeCode(), type2.getTypeDescription());

    }
    @Test
    @DisplayName("Проверяется, что подсказка не выводиться для пользователя с ролью АДМИН")
    void getHelpCommand_RoleAdmin() {

        var role = List.of(Role.builder().roleName("ADMIN").build());

        var helpCommand = this.meterSendCommand.getHelpCommand(role);

        assertThat(helpCommand).isNull();

    }
    @Test
    @DisplayName("Проверяется, что подсказка не выводиться для пользователя без роли")
    void getHelpCommand_noRole() {


        var helpCommand = this.meterSendCommand.getHelpCommand(List.of());

        assertThat(helpCommand).isNull();

    }
    @Test
    @DisplayName("Проверяет, что выводит сообщение")
    void getMakesAction(){
        var makesAction = this.meterSendCommand.getMakesAction();
        assertThat(makesAction).isNotNull();
    }
}