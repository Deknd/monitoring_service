package com.denknd.in.commands;

import com.denknd.entity.Address;
import com.denknd.entity.MeterReading;
import com.denknd.entity.Role;
import com.denknd.entity.User;
import com.denknd.in.commands.functions.*;
import com.denknd.services.AddressService;
import com.denknd.services.MeterReadingService;
import com.denknd.services.TypeMeterService;
import com.denknd.services.UserService;
import lombok.Setter;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Класс представляющий команду консоли, при помощи которой выдается история подачи показаний
 */
@Setter
public class HistoryCommand implements ConsoleCommand<String> {
    /**
     * Команда, которая отвечает за работу этого класса
     */
    private final String COMMAND = "history";
    /**
     * Дополнительный параметр, для передачи айди адреса
     */
    private final String ADDRESS_ID_PARAMETER = "addr=";
    /**
     * Дополнительный параметр, для передачи айди пользователя
     */
    private final String USER_ID_PARAMETER = "user=";
    /**
     * Дополнительный параметр, для передачи даты начало списка
     */
    private final String START_DATE_PARAMETER = "start_date=";
    /**
     * Дополнительный параметр, для передачи даты конца списка
     */
    private final String END_DATE_PARAMETER = "end_date=";
    /**
     * Сервис для работы с адресами
     */
    private final AddressService addressService;
    /**
     * Сервис для работы с показаниями
     */
    private final MeterReadingService meterReadingService;
    /**
     * Сервис для работы с пользователями
     */
    private final UserService userService;
    /**
     * Извлекает доступные типы показаний из консольной команды
     */
    private Function<String[], Set<String>> typeMeterParametersParserFromRawParameters;
    /**
     * Конвертирует историю показаний в строку
     */
    private Function<List<MeterReading>, String> meterReadingsHistoryToStringConverter;
    /**
     * достает из консольной команды Long
     */
    private MyFunction<String[], Long> longIdParserFromRawParameters;
    /**
     * достает из консольной команды дату YearMonth
     */
    private MyFunction<String[], YearMonth> dateParserFromRawParameters;

    /**
     * Конструктор для класса
     * @param addressService сервис для работы с адресами
     * @param meterReadingService сервис для работы с показаниями
     * @param typeMeterService сервис для работы с типами показаний
     * @param userService сервис для работы с пользователями
     */
    public HistoryCommand(AddressService addressService, MeterReadingService meterReadingService, TypeMeterService typeMeterService, UserService userService) {
        this.addressService = addressService;
        this.meterReadingService = meterReadingService;
        this.userService = userService;
        this.meterReadingsHistoryToStringConverter = new MeterReadingsHistoryToStringConverter();
        this.typeMeterParametersParserFromRawParameters = new TypeMeterParametersParserFromRawParameters(typeMeterService);
        this.longIdParserFromRawParameters = new LongIdParserFromRawParameters();
        this.dateParserFromRawParameters = new DateParserFromRawParameters();
    }

    /**
     * Возвращает команду, которая запускает работу метода run
     * @return команда для работы класса
     */
    @Override
    public String getCommand() {
        return this.COMMAND;
    }
    /**
     * Возвращает пояснение работы класса
     * @return пояснение, что делает класс, для аудита
     */
    @Override
    public String getMakesAction() {
        return "Выводит историю подачи показаний";
    }
    /**
     * Основной метод класса
     * @param command команда полученная из консоли
     * @param userActive активный юзер
     * @return возвращает сообщение об результате работы
     */
    @Override
    public String run(String command, User userActive) {
        if (userActive == null) {
            return null;
        }
        var commandAndParam = command.split(" ");
        if (Arrays.asList(commandAndParam).contains(this.COMMAND)) {
            var acceptedParameters = this.typeMeterParametersParserFromRawParameters.apply(commandAndParam);
            var addressId = this.longIdParserFromRawParameters.apply(commandAndParam, this.ADDRESS_ID_PARAMETER);
            var startDate = this.dateParserFromRawParameters.apply(commandAndParam, this.START_DATE_PARAMETER);
            var endDate = this.dateParserFromRawParameters.apply(commandAndParam, this.END_DATE_PARAMETER);

            if (userActive.getRoles().contains(Role.builder().roleName("ADMIN").build())) {
                var userId = this.longIdParserFromRawParameters.apply(commandAndParam, this.USER_ID_PARAMETER);
                if (!this.userService.existUser(userId)) {
                    return "Юзера с id: " + userId + " не существует";
                }
                var meterReadings = this.getHistoryByAddressId(addressId, userId, acceptedParameters, startDate, endDate);
                return this.meterReadingsHistoryToStringConverter.apply(meterReadings);
            } else if (userActive.getRoles().contains(Role.builder().roleName("USER").build())) {
                var meterReadings = this.getHistoryByAddressId(addressId, userActive.getUserId(), acceptedParameters, startDate, endDate);
                return this.meterReadingsHistoryToStringConverter.apply(meterReadings);
            }
        }
        return null;
    }

    /**
     * Метод для выдачи истории по полученным данным
     * @param addressId идентификатор адреса
     * @param userId идентификатор пользователя
     * @param acceptedParameters разрешенные параметры
     * @param startDate дата начала списка показаний
     * @param endDate дата конца списка показаний
     * @return возвращает список с показаниями
     */
    private List<MeterReading> getHistoryByAddressId(Long addressId, Long userId, Set<String> acceptedParameters, YearMonth startDate, YearMonth endDate) {
        var actualAddressesForActiveUser = this.addressService.getAddresses(userId).stream().map(Address::getAddressId).toList();

        if (addressId != null) {
            if (actualAddressesForActiveUser.contains(addressId)) {
                return this.meterReadingService.getHistoryMeterByAddress(addressId, acceptedParameters, startDate, endDate);
            } else {
                System.out.println("Не найдено адреса с Id: " + addressId);
                return List.of();
            }
        } else {
            var meterReadingsAllAddress = new ArrayList<MeterReading>();
            for (Long addressIdForActiveUser : actualAddressesForActiveUser) {
                meterReadingsAllAddress.addAll(this.meterReadingService.getHistoryMeterByAddress(addressIdForActiveUser, acceptedParameters, startDate, endDate));
            }
            return List.copyOf(meterReadingsAllAddress);
        }
    }
    /**
     * Подсказка для команды help
     * @param roles роли доступные пользователю
     * @return возвращает сообщение с подсказкой по работе с данной командой
     */
    @Override
    public String getHelpCommand(List<Role> roles) {
        if (roles.isEmpty()) {
            return null;
        }
        var userRole = Role.builder().roleName("USER").build();
        var userAdmin = Role.builder().roleName("ADMIN").build();
        var containsAdminRole = roles.contains(userAdmin);
        var helpUserIdParameter = containsAdminRole ? this.USER_ID_PARAMETER + " - достает показания по id пользователя(прим.: " + this.USER_ID_PARAMETER + "{userId}),\n               " : "";

        if (roles.contains(userRole) || containsAdminRole) {

            return this.COMMAND + " - без параметров возвращает всю историю подачи показаний пользователя по всем его адресам.\n Дополнительные параметры: \n               " +
                    helpUserIdParameter +
                    this.ADDRESS_ID_PARAMETER + " - добавляет фильтрацию по адресу(прим.: " + this.ADDRESS_ID_PARAMETER + "{addressId}),\n               " +
                    this.START_DATE_PARAMETER + " - добавляет фильтрацию по дате начала выдачи истории(прим.: " + this.START_DATE_PARAMETER + "12-1999), \n               " +
                    this.END_DATE_PARAMETER + " - добавляет фильтрацию по дате до которой нужна история(прим.: " + this.END_DATE_PARAMETER + "20-2023)";
        }
        return null;
    }
}
