package com.denknd.in.commands;

import com.denknd.controllers.MeterReadingController;
import com.denknd.controllers.TypeMeterController;
import com.denknd.dto.MeterReadingResponseDto;
import com.denknd.entity.Address;
import com.denknd.entity.MeterReading;
import com.denknd.entity.Roles;
import com.denknd.in.commands.functions.MyFunction;
import com.denknd.security.UserSecurity;
import com.denknd.services.AddressService;
import com.denknd.services.MeterReadingService;
import com.denknd.services.TypeMeterService;
import com.denknd.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class HistoryCommandTest {
    @Mock
    private MeterReadingController meterReadingController;
    @Mock
    private Function<String[], Set<String>> typeMeterParametersParserFromRawParameters;
    @Mock
    private Function<List<MeterReadingResponseDto>, String> meterReadingsHistoryToStringConverter;
    @Mock
    private MyFunction<String[], Long> longIdParserFromRawParameters;
    @Mock
    private MyFunction<String[], YearMonth> dateParserFromRawParameters;

    private HistoryCommand historyCommand;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        this.closeable = MockitoAnnotations.openMocks(this);
        this.historyCommand = new HistoryCommand(mock(TypeMeterController.class),this.meterReadingController);
        this.historyCommand.setTypeMeterParametersParserFromRawParameters(this.typeMeterParametersParserFromRawParameters);
        this.historyCommand.setMeterReadingsHistoryToStringConverter(this.meterReadingsHistoryToStringConverter);
        this.historyCommand.setLongIdParserFromRawParameters(this.longIdParserFromRawParameters);
        this.historyCommand.setDateParserFromRawParameters(this.dateParserFromRawParameters);
    }
    @AfterEach
    void tearDown() throws Exception {
        this.closeable.close();
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
        var role = Roles.USER;
        var user = mock(UserSecurity.class);
        var address = mock(Address.class);
        when(address.getAddressId()).thenReturn(1L);
        when(user.role()).thenReturn(role);

        this.historyCommand.run(command, user);

        verify(this.typeMeterParametersParserFromRawParameters, times(1)).apply(any());
        verify(this.longIdParserFromRawParameters, times(1)).apply(any(), any());
        verify(this.dateParserFromRawParameters, times(2)).apply(any(), any());
        verify(this.meterReadingsHistoryToStringConverter, times(1)).apply(any());
        verify(this.meterReadingController, times(1)).getHistoryMeterReading(any(), any(), any(), any(), any());

    }



    @Test
    @DisplayName("Проверяет, что все параметры парсятся под ролью админа и вызывается метод сервиса")
    void run_Admin() {
        var command = "history";
        var role = Roles.ADMIN;
        var user = mock(UserSecurity.class);
        var address = mock(Address.class);
        when(address.getAddressId()).thenReturn(1L);
        when(user.role()).thenReturn(role);
        when(this.longIdParserFromRawParameters.apply(any(), any())).thenReturn(1L);

        this.historyCommand.run(command, user);

        verify(this.typeMeterParametersParserFromRawParameters, times(1)).apply(any());
        verify(this.longIdParserFromRawParameters, times(2)).apply(any(), any());
        verify(this.dateParserFromRawParameters, times(2)).apply(any(), any());
        verify(this.meterReadingsHistoryToStringConverter, times(1)).apply(any());
        verify(this.meterReadingController, times(1)).getHistoryMeterReading(any(), any(), any(), any(), any());

    }

    @Test
    @DisplayName("Проверяет, что выходит из метода если нет команды")
    void run_notCommand() {
        var command = "historady";
        var user = mock(UserSecurity.class);


        this.historyCommand.run(command, user);

        verify(this.typeMeterParametersParserFromRawParameters, times(0)).apply(any());
        verify(this.longIdParserFromRawParameters, times(0)).apply(any(), any());
        verify(this.dateParserFromRawParameters, times(0)).apply(any(), any());
        verify(this.meterReadingsHistoryToStringConverter, times(0)).apply(any());
        verify(this.meterReadingController, times(0)).getHistoryMeterReading(any(),any(), any(), any(), any());

    }

    @Test
    @DisplayName("Проверяет, что выходит из метода, когда нет пользователя")
    void run_notUSer() {
        var command = "history";

        this.historyCommand.run(command, null);

        verify(this.typeMeterParametersParserFromRawParameters, times(0)).apply(any());
        verify(this.longIdParserFromRawParameters, times(0)).apply(any(), any());
        verify(this.dateParserFromRawParameters, times(0)).apply(any(), any());
        verify(this.meterReadingsHistoryToStringConverter, times(0)).apply(any());
        verify(this.meterReadingController, times(0)).getHistoryMeterReading(any(),any(), any(), any(), any());

    }

    @Test
    @DisplayName("Проверяет, что все доступные команды для роли юзера отображаются")
    void getHelpCommand() {
        String command = "history";
        String address = "addr=";
        String user = "user=";
        String start = "start_date=";
        String end = "end_date=";
        var roleUser = Roles.USER;

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
        var roleUser = Roles.ADMIN;

        var helpCommand = this.historyCommand.getHelpCommand(roleUser);

        assertThat(helpCommand).contains(command, address, start, end, user);
    }
    @Test
    @DisplayName("Проверяет, что не авторизированному пользователю не показывается данная подсказка")
    void getHelpCommand_notUser() {

        var helpCommand = this.historyCommand.getHelpCommand(null);

        assertThat(helpCommand).isNull();
    }

    @Test
    @DisplayName("Проверяет, что выводит сообщение")
    void getMakesAction(){
        var makesAction = this.historyCommand.getAuditActionDescription();
        assertThat(makesAction).isNotNull();
    }
}