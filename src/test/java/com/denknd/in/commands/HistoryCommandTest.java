package com.denknd.in.commands;

import com.denknd.entity.Address;
import com.denknd.entity.MeterReading;
import com.denknd.entity.Role;
import com.denknd.entity.User;
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

class HistoryCommandTest {

    private AddressService addressService;
    private MeterReadingService meterReadingService;
    private UserService userService;
    private Function<String[], Set<String>> typeMeterParametersParserFromRawParameters;
    private Function<List<MeterReading>, String> meterReadingsHistoryToStringConverter;
    private MyFunction<String[], Long> longIdParserFromRawParameters;
    private MyFunction<String[], YearMonth> dateParserFromRawParameters;

    private HistoryCommand historyCommand;
    @BeforeEach
    void setUp() {
        this.addressService = mock(AddressService.class);
        this.meterReadingService = mock(MeterReadingService.class);
        this.userService = mock(UserService.class);
        this.typeMeterParametersParserFromRawParameters = mock(Function.class);
        this.meterReadingsHistoryToStringConverter = mock(Function.class);
        this.longIdParserFromRawParameters = mock(MyFunction.class);
        this.dateParserFromRawParameters = mock(MyFunction.class);
        this.historyCommand = new HistoryCommand(this.addressService, this.meterReadingService, mock(TypeMeterService.class), this.userService);
        this.historyCommand.setTypeMeterParametersParserFromRawParameters(this.typeMeterParametersParserFromRawParameters);
        this.historyCommand.setMeterReadingsHistoryToStringConverter(this.meterReadingsHistoryToStringConverter);
        this.historyCommand.setLongIdParserFromRawParameters(this.longIdParserFromRawParameters);
        this.historyCommand.setDateParserFromRawParameters(this.dateParserFromRawParameters);
    }

    @Test
    @DisplayName("Проверяет, что возвращается ожидаемая команда")
    void getCommand() {
        var command = "history";
        var historyCommand = this.historyCommand.getCommand();

        assertThat(historyCommand).isEqualTo(command);
    }

    @Test
    @DisplayName("Проверяет, что все параметры парсятся и вызывается метод сервиса")
    void run() {
        var command = "history";
        var role = Role.builder().roleName("USER").build();
        var user = mock(User.class);
        var address = mock(Address.class);
        when(address.getAddressId()).thenReturn(1L);
        when(user.getRoles()).thenReturn(List.of(role));
        when(this.longIdParserFromRawParameters.apply(any(), any())).thenReturn(1L);
        when(this.addressService.getAddresses(any())).thenReturn(List.of(address));

        this.historyCommand.run(command, user);

        verify(this.addressService, times(1)).getAddresses(any());
        verify(this.typeMeterParametersParserFromRawParameters, times(1)).apply(any());
        verify(this.longIdParserFromRawParameters, times(1)).apply(any(), any());
        verify(this.dateParserFromRawParameters, times(2)).apply(any(), any());
        verify(this.meterReadingsHistoryToStringConverter, times(1)).apply(any());
        verify(this.meterReadingService, times(1)).getHistoryMeterByAddress(any(), any(), any(), any());

    }

    @Test
    @DisplayName("Проверяет, что когда пробуешь получить данные другого пользователя с ролью ЮЗЕР, не вызывается сервис")
    void run_notAddress() {
        var command = "history";
        var role = Role.builder().roleName("USER").build();
        var user = mock(User.class);
        var address = mock(Address.class);
        when(address.getAddressId()).thenReturn(4L);
        when(user.getRoles()).thenReturn(List.of(role));
        when(this.longIdParserFromRawParameters.apply(any(), any())).thenReturn(1L);
        when(this.addressService.getAddresses(any())).thenReturn(List.of(address));

        this.historyCommand.run(command, user);

        verify(this.addressService, times(1)).getAddresses(any());
        verify(this.typeMeterParametersParserFromRawParameters, times(1)).apply(any());
        verify(this.longIdParserFromRawParameters, times(1)).apply(any(), any());
        verify(this.dateParserFromRawParameters, times(2)).apply(any(), any());
        verify(this.meterReadingsHistoryToStringConverter, times(1)).apply(any());
        verify(this.meterReadingService, times(0)).getHistoryMeterByAddress(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Проверяет, что достаются все доступные адреса пользователя")
    void run_UserAddress() {
        var command = "history";
        var role = Role.builder().roleName("USER").build();
        var user = mock(User.class);
        var address = mock(Address.class);
        when(address.getAddressId()).thenReturn(4L);
        when(user.getRoles()).thenReturn(List.of(role));
        when(this.addressService.getAddresses(any())).thenReturn(List.of(address));

        this.historyCommand.run(command, user);

        verify(this.addressService, times(1)).getAddresses(any());
        verify(this.typeMeterParametersParserFromRawParameters, times(1)).apply(any());
        verify(this.longIdParserFromRawParameters, times(1)).apply(any(), any());
        verify(this.dateParserFromRawParameters, times(2)).apply(any(), any());
        verify(this.meterReadingsHistoryToStringConverter, times(1)).apply(any());
        verify(this.meterReadingService, times(1)).getHistoryMeterByAddress(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Проверяет, что все параметры парсятся под ролью админа и вызывается метод сервиса")
    void run_Admin() {
        var command = "history";
        var role = Role.builder().roleName("ADMIN").build();
        var user = mock(User.class);
        var address = mock(Address.class);
        when(address.getAddressId()).thenReturn(1L);
        when(user.getRoles()).thenReturn(List.of(role));
        when(this.longIdParserFromRawParameters.apply(any(), any())).thenReturn(1L);
        when(this.addressService.getAddresses(any())).thenReturn(List.of(address));
        when(this.userService.existUser(any())).thenReturn(true);

        this.historyCommand.run(command, user);

        verify(this.addressService, times(1)).getAddresses(any());
        verify(this.typeMeterParametersParserFromRawParameters, times(1)).apply(any());
        verify(this.longIdParserFromRawParameters, times(2)).apply(any(), any());
        verify(this.dateParserFromRawParameters, times(2)).apply(any(), any());
        verify(this.meterReadingsHistoryToStringConverter, times(1)).apply(any());
        verify(this.meterReadingService, times(1)).getHistoryMeterByAddress(any(), any(), any(), any());

    }

    @Test
    @DisplayName("Проверяет, что выходит из метода, когда под ролью Админа пробуют достать пользователя не существующим айди")
    void run_Admin_NotUser() {
        var command = "history";
        var role = Role.builder().roleName("ADMIN").build();
        var user = mock(User.class);
        var address = mock(Address.class);
        when(address.getAddressId()).thenReturn(1L);
        when(user.getRoles()).thenReturn(List.of(role));
        when(this.longIdParserFromRawParameters.apply(any(), any())).thenReturn(1L);
        when(this.userService.existUser(any())).thenReturn(false);

        this.historyCommand.run(command, user);

        verify(this.addressService, times(0)).getAddresses(any());
        verify(this.typeMeterParametersParserFromRawParameters, times(1)).apply(any());
        verify(this.longIdParserFromRawParameters, times(2)).apply(any(), any());
        verify(this.dateParserFromRawParameters, times(2)).apply(any(), any());
        verify(this.meterReadingsHistoryToStringConverter, times(0)).apply(any());
        verify(this.meterReadingService, times(0)).getHistoryMeterByAddress(any(), any(), any(), any());

    }

    @Test
    @DisplayName("Проверяет, что выходит из метода если нет команды")
    void run_notCommand() {
        var command = "historady";
        var user = mock(User.class);


        this.historyCommand.run(command, user);

        verify(this.addressService, times(0)).getAddresses(any());
        verify(this.typeMeterParametersParserFromRawParameters, times(0)).apply(any());
        verify(this.longIdParserFromRawParameters, times(0)).apply(any(), any());
        verify(this.dateParserFromRawParameters, times(0)).apply(any(), any());
        verify(this.meterReadingsHistoryToStringConverter, times(0)).apply(any());
        verify(this.meterReadingService, times(0)).getHistoryMeterByAddress(any(), any(), any(), any());

    }

    @Test
    @DisplayName("Проверяет, что выходит из метода, когда нет пользователя")
    void run_notUSer() {
        var command = "history";

        this.historyCommand.run(command, null);

        verify(this.addressService, times(0)).getAddresses(any());
        verify(this.typeMeterParametersParserFromRawParameters, times(0)).apply(any());
        verify(this.longIdParserFromRawParameters, times(0)).apply(any(), any());
        verify(this.dateParserFromRawParameters, times(0)).apply(any(), any());
        verify(this.meterReadingsHistoryToStringConverter, times(0)).apply(any());
        verify(this.meterReadingService, times(0)).getHistoryMeterByAddress(any(), any(), any(), any());

    }

    @Test
    @DisplayName("Проверяет, что все доступные команды для роли юзера отображаются")
    void getHelpCommand() {
        String command = "history";
        String address = "addr=";
        String user = "user=";
        String start = "start_date=";
        String end = "end_date=";
        var roleUser = List.of(Role.builder().roleName("USER").build());

        var helpCommand = this.historyCommand.getHelpCommand(roleUser);

        assertThat(helpCommand).contains(command, address, start, end).doesNotContain(user);
    }
    @Test
    @DisplayName("Проверяет, что все доступные команды для роли админа отображаются")
    void getHelpCommand_Admin() {
        String command = "history";
        String address = "addr=";
        String user = "user=";
        String start = "start_date=";
        String end = "end_date=";
        var roleUser = List.of(Role.builder().roleName("ADMIN").build());

        var helpCommand = this.historyCommand.getHelpCommand(roleUser);

        assertThat(helpCommand).contains(command, address, start, end, user);
    }
    @Test
    @DisplayName("Проверяет, что не авторизированному пользователю не показывается данная подсказка")
    void getHelpCommand_notUser() {

        var helpCommand = this.historyCommand.getHelpCommand(List.of());

        assertThat(helpCommand).isNull();
    }

    @Test
    @DisplayName("Проверяет, что пользователю с неизвестной ролью не отображается данная подсказка")
    void getHelpCommand_randomRole() {

        var roleUser = List.of(Role.builder().roleName("asdasd").build());

        var helpCommand = this.historyCommand.getHelpCommand(roleUser);

        assertThat(helpCommand).isNull();
    }
    @Test
    @DisplayName("Проверяет, что выводит сообщение")
    void getMakesAction(){
        var makesAction = this.historyCommand.getMakesAction();
        assertThat(makesAction).isNotNull();
    }
}