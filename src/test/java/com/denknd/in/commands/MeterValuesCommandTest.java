package com.denknd.in.commands;

import com.denknd.controllers.MeterReadingController;
import com.denknd.controllers.TypeMeterController;
import com.denknd.dto.MeterReadingResponseDto;
import com.denknd.dto.TypeMeterDto;
import com.denknd.entity.*;
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

class MeterValuesCommandTest {
    @Mock
    private MeterReadingController meterReadingController;
    @Mock
    private TypeMeterController typeMeterController;
    @Mock
    private Function<List<MeterReadingResponseDto>, String> meterReadingsToStringConverter;
    @Mock
    private Function<String[], Set<String>> typeMeterParametersParserFromRawParameters;
    @Mock
    private MyFunction<String[], Long> longIdParserFromRawParameters;
    @Mock
    private MyFunction<String[], YearMonth> dateParserFromRawParameters;

    private MeterValuesCommand meterValuesCommand;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        this.closeable = MockitoAnnotations.openMocks(this);

        this.meterValuesCommand = new MeterValuesCommand(this.typeMeterController, this.meterReadingController);
        this.meterValuesCommand.setDateParserFromRawParameters(this.dateParserFromRawParameters);
        this.meterValuesCommand.setMeterReadingsToStringConverter(this.meterReadingsToStringConverter);
        this.meterValuesCommand.setTypeMeterParametersParserFromRawParameters(this.typeMeterParametersParserFromRawParameters);
        this.meterValuesCommand.setLongIdParserFromRawParameters(this.longIdParserFromRawParameters);
    }
    @AfterEach
    void tearDown() throws Exception {
        this.closeable.close();
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
        var userActive = mock(UserSecurity.class);
        when(userActive.role()).thenReturn(Roles.USER);


        var run = this.meterValuesCommand.run(command, userActive);

        verify(this.typeMeterParametersParserFromRawParameters, times(1)).apply(any());
        verify(this.dateParserFromRawParameters, times(1)).apply(any(), any());
        verify(this.longIdParserFromRawParameters, times(1)).apply(any(), any());
        verify(this.meterReadingsToStringConverter, times(1)).apply(any());
        verify(this.meterReadingController, times(1)).getMeterReadings(any(), any(), any(), any());
    }




    @Test
    @DisplayName("Поверяет, что с ролью Админ считывается еще один параметр")
    void run_adminRole() {
        var command = "meter-values";
        var userActive = mock(UserSecurity.class);
        when(userActive.role()).thenReturn(Roles.ADMIN);

        this.meterValuesCommand.run(command, userActive);

        verify(this.typeMeterParametersParserFromRawParameters, times(1)).apply(any());
        verify(this.dateParserFromRawParameters, times(1)).apply(any(), any());
        verify(this.longIdParserFromRawParameters, times(2)).apply(any(), any());
        verify(this.meterReadingsToStringConverter, times(1)).apply(any());
        verify(this.meterReadingController, times(1)).getMeterReadings(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Поверяет, что с неизвестной ролью метод прекращается")
    void run_unknownRole() {
        var command = "meter-values";
        var userActive = mock(UserSecurity.class);
        when(userActive.role()).thenReturn(null);

        this.meterValuesCommand.run(command, userActive);

        verify(this.typeMeterParametersParserFromRawParameters, times(0)).apply(any());
        verify(this.dateParserFromRawParameters, times(0)).apply(any(), any());
        verify(this.longIdParserFromRawParameters, times(0)).apply(any(), any());
        verify(this.meterReadingsToStringConverter, times(0)).apply(any());
        verify(this.meterReadingController, times(0)).getMeterReadings(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Поверяет, что с неизвестной командой метод прекращается")
    void run_unknownCommand() {
        var command = "meterывlues";
        var userActive = mock(UserSecurity.class);

        this.meterValuesCommand.run(command, userActive);

        verify(this.typeMeterParametersParserFromRawParameters, times(0)).apply(any());
        verify(this.dateParserFromRawParameters, times(0)).apply(any(), any());
        verify(this.longIdParserFromRawParameters, times(0)).apply(any(), any());
        verify(this.meterReadingsToStringConverter, times(0)).apply(any());
        verify(this.meterReadingController, times(0)).getMeterReadings(any(), any(), any(), any());
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
        verify(this.meterReadingController, times(0)).getMeterReadings(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Проверяет, что подсказка доступна пользователю с ролью Юзер")
    void getHelpCommand() {
        var role = Roles.USER;
        var type1 = TypeMeterDto.builder().typeCode("test1").typeDescription("description1").build();
        var type2 = TypeMeterDto.builder().typeCode("test2").typeDescription("description2").build();
        String command = "meter-values";
        String address = "addr=";
        String user = "user=";
        String data = "data=";
        when(this.typeMeterController.getTypeMeterCodes()).thenReturn(Set.of(type1, type2));

        var helpCommand = this.meterValuesCommand.getHelpCommand(role);

        assertThat(helpCommand)
                .contains(command, address, data, type1.typeCode(), type1.typeDescription(), type2.typeCode(), type2.typeDescription())
                .doesNotContain(user);
    }

    @Test
    @DisplayName("Проверяет, что подсказка доступна пользователю с ролью Юзер")
    void getHelpCommand_Admin() {
        var role = Roles.ADMIN;
        var type1 = TypeMeterDto.builder().typeCode("test1").typeDescription("description1").build();
        var type2 = TypeMeterDto.builder().typeCode("test2").typeDescription("description2").build();
        String command = "meter-values";
        String address = "addr=";
        String user = "user=";
        String data = "data=";
        when(this.typeMeterController.getTypeMeterCodes()).thenReturn(Set.of(type1, type2));

        var helpCommand = this.meterValuesCommand.getHelpCommand(role);

        assertThat(helpCommand)
                .contains(command, address, data, type1.typeCode(), type1.typeDescription(), type2.typeCode(), type2.typeDescription(), user);
    }

    @Test
    @DisplayName("Проверяет, что подсказка не доступна пользователю с без роли")
    void getHelpCommand_noUser() {

        var helpCommand = this.meterValuesCommand.getHelpCommand(null);

        assertThat(helpCommand).isNull();
    }
    @Test
    @DisplayName("Проверяет, что выводит сообщение")
    void getMakesAction(){
        var makesAction = this.meterValuesCommand.getAuditActionDescription();
        assertThat(makesAction).isNotNull();
    }
}