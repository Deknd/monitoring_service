package com.denknd.in.commands;

import com.denknd.entity.*;
import com.denknd.in.commands.functions.MyFunction;
import com.denknd.services.AddressService;
import com.denknd.services.MeterReadingService;
import com.denknd.services.TypeMeterService;
import com.denknd.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MeterValuesCommandTest {
    private AddressService addressService;
    private MeterReadingService meterReadingService;
    private TypeMeterService typeMeterService;
    private UserService userService;
    private Function<List<MeterReading>, String> meterReadingsToStringConverter;
    private Function<String[], Set<String>> typeMeterParametersParserFromRawParameters;
    private MyFunction<String[], Long> longIdParserFromRawParameters;
    private MyFunction<String[], YearMonth> dateParserFromRawParameters;

    private MeterValuesCommand meterValuesCommand;

    @BeforeEach
    void setUp() {
        this.addressService = mock(AddressService.class);
        this.meterReadingService = mock(MeterReadingService.class);
        this.typeMeterService = mock(TypeMeterService.class);
        this.userService = mock(UserService.class);
        this.meterReadingsToStringConverter = mock(Function.class);
        this.typeMeterParametersParserFromRawParameters = mock(Function.class);
        this.longIdParserFromRawParameters = mock(MyFunction.class);
        this.dateParserFromRawParameters = mock(MyFunction.class);

        this.meterValuesCommand = new MeterValuesCommand(this.addressService, this.meterReadingService, this.typeMeterService, this.userService);
        this.meterValuesCommand.setDateParserFromRawParameters(this.dateParserFromRawParameters);
        this.meterValuesCommand.setMeterReadingsToStringConverter(this.meterReadingsToStringConverter);
        this.meterValuesCommand.setTypeMeterParametersParserFromRawParameters(this.typeMeterParametersParserFromRawParameters);
        this.meterValuesCommand.setLongIdParserFromRawParameters(this.longIdParserFromRawParameters);

    }

    @Test
    @DisplayName("Проверяет, что выдаваемая команда актуальна")
    void getCommand() {
        var command = "meter-values";

        var command1 = this.meterValuesCommand.getCommand();

        assertThat(command1).isEqualTo(command);
    }

    @Test
    @DisplayName("Поверяет, что вызываются все нужные сервисы")
    void run() {
        var command = "meter-values";
        var userActive = mock(User.class);
        when(userActive.getRoles()).thenReturn(List.of(Role.builder().roleName("USER").build()));
        when(this.longIdParserFromRawParameters.apply(any(), any())).thenReturn(1L);
        var mock = mock(Address.class);
        when(mock.getAddressId()).thenReturn(1L);
        when(this.addressService.getAddresses(any())).thenReturn(List.of(mock));

        this.meterValuesCommand.run(command, userActive);

        verify(this.typeMeterParametersParserFromRawParameters, times(1)).apply(any());
        verify(this.dateParserFromRawParameters, times(1)).apply(any(), any());
        verify(this.longIdParserFromRawParameters, times(1)).apply(any(), any());
        verify(this.meterReadingsToStringConverter, times(1)).apply(any());
        verify(this.meterReadingService, times(1)).getActualMeterByAddress(any(), any(), any());
    }

    @Test
    @DisplayName("Поверяет, что с ролью юзера нельзя посмотреть историю не своего адреса")
    void run_noAddress() {
        var command = "meter-values";
        var userActive = mock(User.class);
        when(userActive.getRoles()).thenReturn(List.of(Role.builder().roleName("USER").build()));
        when(this.longIdParserFromRawParameters.apply(any(), any())).thenReturn(1L);
        var mock = mock(Address.class);
        when(this.addressService.getAddresses(any())).thenReturn(List.of(mock));

        this.meterValuesCommand.run(command, userActive);

        verify(this.typeMeterParametersParserFromRawParameters, times(1)).apply(any());
        verify(this.dateParserFromRawParameters, times(1)).apply(any(), any());
        verify(this.longIdParserFromRawParameters, times(1)).apply(any(), any());
        verify(this.meterReadingsToStringConverter, times(1)).apply(any());
        verify(this.meterReadingService, times(0)).getActualMeterByAddress(any(), any(), any());
    }

    @Test
    @DisplayName("Поверяет, что если не вводить адрес, метод пробежит по всем доступным адресам")
    void run_allAddress() {
        var command = "meter-values";
        var userActive = mock(User.class);
        when(userActive.getRoles()).thenReturn(List.of(Role.builder().roleName("USER").build()));
        var mock = mock(Address.class);
        when(mock.getAddressId()).thenReturn(1L);
        when(this.addressService.getAddresses(any())).thenReturn(List.of(mock));

        this.meterValuesCommand.run(command, userActive);

        verify(this.typeMeterParametersParserFromRawParameters, times(1)).apply(any());
        verify(this.dateParserFromRawParameters, times(1)).apply(any(), any());
        verify(this.longIdParserFromRawParameters, times(1)).apply(any(), any());
        verify(this.meterReadingsToStringConverter, times(1)).apply(any());
        verify(this.meterReadingService, times(1)).getActualMeterByAddress(any(), any(), any());
    }

    @Test
    @DisplayName("Поверяет, что с ролью Админ считывается еще один параметр")
    void run_adminRole() {
        var command = "meter-values";
        var userActive = mock(User.class);
        when(userActive.getRoles()).thenReturn(List.of(Role.builder().roleName("ADMIN").build()));
        var mock = mock(Address.class);
        when(mock.getAddressId()).thenReturn(1L);
        when(this.addressService.getAddresses(any())).thenReturn(List.of(mock));
        when(this.longIdParserFromRawParameters.apply(any(), any())).thenReturn(1L);
        when(this.userService.existUser(any())).thenReturn(true);

        this.meterValuesCommand.run(command, userActive);

        verify(this.typeMeterParametersParserFromRawParameters, times(1)).apply(any());
        verify(this.dateParserFromRawParameters, times(1)).apply(any(), any());
        verify(this.longIdParserFromRawParameters, times(2)).apply(any(), any());
        verify(this.meterReadingsToStringConverter, times(1)).apply(any());
        verify(this.meterReadingService, times(1)).getActualMeterByAddress(any(), any(), any());
    }

    @Test
    @DisplayName("Поверяет, что с ролью Админ считывается еще один параметр, если введенного айди пользователя не существует, метод прекращается")
    void run_adminRoleNoUser() {
        var command = "meter-values";
        var userActive = mock(User.class);
        when(userActive.getRoles()).thenReturn(List.of(Role.builder().roleName("ADMIN").build()));

        this.meterValuesCommand.run(command, userActive);

        verify(this.typeMeterParametersParserFromRawParameters, times(1)).apply(any());
        verify(this.dateParserFromRawParameters, times(1)).apply(any(), any());
        verify(this.longIdParserFromRawParameters, times(2)).apply(any(), any());
        verify(this.meterReadingsToStringConverter, times(0)).apply(any());
        verify(this.meterReadingService, times(0)).getActualMeterByAddress(any(), any(), any());
    }

    @Test
    @DisplayName("Поверяет, что с неизвестной ролью метод прекращается")
    void run_unknownRole() {
        var command = "meter-values";
        var userActive = mock(User.class);
        when(userActive.getRoles()).thenReturn(List.of(Role.builder().roleName("ads").build()));

        this.meterValuesCommand.run(command, userActive);

        verify(this.typeMeterParametersParserFromRawParameters, times(1)).apply(any());
        verify(this.dateParserFromRawParameters, times(1)).apply(any(), any());
        verify(this.longIdParserFromRawParameters, times(1)).apply(any(), any());
        verify(this.meterReadingsToStringConverter, times(0)).apply(any());
        verify(this.meterReadingService, times(0)).getActualMeterByAddress(any(), any(), any());
    }

    @Test
    @DisplayName("Поверяет, что с неизвестной командой метод прекращается")
    void run_unknownCommand() {
        var command = "meterывlues";
        var userActive = mock(User.class);

        this.meterValuesCommand.run(command, userActive);

        verify(this.typeMeterParametersParserFromRawParameters, times(0)).apply(any());
        verify(this.dateParserFromRawParameters, times(0)).apply(any(), any());
        verify(this.longIdParserFromRawParameters, times(0)).apply(any(), any());
        verify(this.meterReadingsToStringConverter, times(0)).apply(any());
        verify(this.meterReadingService, times(0)).getActualMeterByAddress(any(), any(), any());
    }

    @Test
    @DisplayName("Поверяет, что без пользователя, метод прекращается")
    void run_noUser() {
        var command = "meterывlues";

        this.meterValuesCommand.run(command, null);

        verify(this.typeMeterParametersParserFromRawParameters, times(0)).apply(any());
        verify(this.dateParserFromRawParameters, times(0)).apply(any(), any());
        verify(this.longIdParserFromRawParameters, times(0)).apply(any(), any());
        verify(this.meterReadingsToStringConverter, times(0)).apply(any());
        verify(this.meterReadingService, times(0)).getActualMeterByAddress(any(), any(), any());
    }

    @Test
    @DisplayName("Проверяет, что подсказка доступна пользователю с ролью Юзер")
    void getHelpCommand() {
        var role = List.of(Role.builder().roleName("USER").build());
        var type1 = TypeMeter.builder().typeCode("test1").typeDescription("description1").build();
        var type2 = TypeMeter.builder().typeCode("test2").typeDescription("description2").build();
        String command = "meter-values";
        String address = "addr=";
        String user = "user=";
        String data = "data=";
        when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(type1, type2));

        var helpCommand = this.meterValuesCommand.getHelpCommand(role);

        assertThat(helpCommand)
                .contains(command, address, data, type1.getTypeCode(), type1.getTypeDescription(), type2.getTypeCode(), type2.getTypeDescription())
                .doesNotContain(user);
    }

    @Test
    @DisplayName("Проверяет, что подсказка доступна пользователю с ролью Юзер")
    void getHelpCommand_Admin() {
        var role = List.of(Role.builder().roleName("ADMIN").build());
        var type1 = TypeMeter.builder().typeCode("test1").typeDescription("description1").build();
        var type2 = TypeMeter.builder().typeCode("test2").typeDescription("description2").build();
        String command = "meter-values";
        String address = "addr=";
        String user = "user=";
        String data = "data=";
        when(this.typeMeterService.getTypeMeter()).thenReturn(List.of(type1, type2));

        var helpCommand = this.meterValuesCommand.getHelpCommand(role);

        assertThat(helpCommand)
                .contains(command, address, data, type1.getTypeCode(), type1.getTypeDescription(), type2.getTypeCode(), type2.getTypeDescription(), user);
    }
    @Test
    @DisplayName("Проверяет, что подсказка не доступна пользователю с неизвестной ролью")
    void getHelpCommand_unknownRole() {
        var role = List.of(Role.builder().roleName("unknown").build());

        var helpCommand = this.meterValuesCommand.getHelpCommand(role);

        assertThat(helpCommand).isNull();
    }

    @Test
    @DisplayName("Проверяет, что подсказка не доступна пользователю с без роли")
    void getHelpCommand_noUser() {

        var helpCommand = this.meterValuesCommand.getHelpCommand(List.of());

        assertThat(helpCommand).isNull();
    }
    @Test
    @DisplayName("Проверяет, что выводит сообщение")
    void getMakesAction(){
        var makesAction = this.meterValuesCommand.getMakesAction();
        assertThat(makesAction).isNotNull();
    }
}