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
import java.util.stream.Collectors;

/**
 * Обрабатывает команду {@link  MeterValuesCommand#COMMAND}, полученную из консоли
 */
@Setter
public class MeterValuesCommand implements ConsoleCommand<String> {
    /**
     * Команда, которая будет ловиться в консоли
     */
    private final String COMMAND = "meter-values";
    /**
     * Дополнительный параметр, по которому будет фильтроваться вывод, отвечает за прием addressId
     */
    private final String ADDRESS_ID_PARAMETER = "addr=";
    /**
     * Дополнительный параметр, по которому будет фильтроваться вывод, отвечает за прием userId. Доступен только админу.
     */
    private final String USER_ID_PARAMETER = "user=";
    /**
     * Дополнительный параметр, по которому будет фильтроваться вывод, отвечает за прием даты.
     */
    private final String DATE_PARAMETER = "data=";
    /**
     * Сервис по работе с адресами
     */
    private final AddressService addressService;
    /**
     * Сервис по работе с пользователями
     */
    private final UserService userService;
    /**
     * Сервис по работе с показаниями
     */
    private final MeterReadingService meterReadingService;
    /**
     * Сервис по работе с типами показаний
     */
    private final TypeMeterService typeMeterService;
    /**
     * Функция, которая конвертирует список показаний в строку
     */
    private Function<List<MeterReading>, String> meterReadingsToStringConverter;
    /**
     * Функция, которая достает из введенных параметров типы показаний
     */
    private Function<String[], Set<String>> typeMeterParametersParserFromRawParameters;
    /**
     * Парсит введённые параметры и достает из них Id пользователя
     */
    private MyFunction<String[], Long> longIdParserFromRawParameters;
    /**
     * Парсит введённые параметры и достает из них дату
     */
    private MyFunction<String[], YearMonth> dateParserFromRawParameters;

    /**
     * Обрабатывает команду {@link  MeterValuesCommand#COMMAND}, полученную из консоли
     *
     * @param addressService      сервис для работы с адресами
     * @param meterReadingService сервис для работы с показаниями
     * @param typeMeterService    сервис для работы с типами показаний
     * @param userService         сервис для работы с пользователями
     */
    public MeterValuesCommand(AddressService addressService, MeterReadingService meterReadingService, TypeMeterService typeMeterService, UserService userService) {
        this.addressService = addressService;
        this.meterReadingService = meterReadingService;
        this.typeMeterService = typeMeterService;
        this.userService = userService;
        this.meterReadingsToStringConverter = new DefaultMeterReadingsToStringConverter();
        this.typeMeterParametersParserFromRawParameters = new TypeMeterParametersParserFromRawParameters(typeMeterService);
        this.longIdParserFromRawParameters = new LongIdParserFromRawParameters();
        this.dateParserFromRawParameters = new DateParserFromRawParameters();
    }

    /**
     * Возвращает команда для обработки в консоли
     *
     * @return возвращает {@link  MeterValuesCommand#COMMAND}
     */
    @Override
    public String getCommand() {
        return this.COMMAND;
    }

    @Override
    public String getMakesAction() {
        return "Получает актуальные показание";
    }


    /**
     * Обрабатывает запрос из консоли
     *
     * @param command    команда введенная пользователем
     * @param userActive пользователь, который сейчас находится в системе
     * @return возвращает показания доступные пользователю по введенным параметрам
     */
    @Override
    public String run(String command, User userActive) {
        if (userActive == null) {
            return null;
        }
        var commandAndParam = command.split(" ");
        if (Arrays.asList(commandAndParam).contains(this.COMMAND)) {
            //Получение данных
            var acceptedParameters = this.typeMeterParametersParserFromRawParameters.apply(commandAndParam);
            var dateFilter = this.dateParserFromRawParameters.apply(commandAndParam, this.DATE_PARAMETER);
            var addressId = this.longIdParserFromRawParameters.apply(commandAndParam, this.ADDRESS_ID_PARAMETER);
            if (userActive.getRoles().contains(Role.builder().roleName("ADMIN").build())) {
                var userId = this.longIdParserFromRawParameters.apply(commandAndParam, this.USER_ID_PARAMETER);
                if (!this.userService.existUser(userId)) {
                    return "Юзера с id: " + userId + " не существует";
                }
                //Получение списка показаний
                var meterReadings = this.getMeterReadingsByAddressId(addressId, userId, acceptedParameters, dateFilter);
                //Вывод показаний пользователю по указанным параметрам
                return this.meterReadingsToStringConverter.apply(meterReadings);

            } else if (userActive.getRoles().contains(Role.builder().roleName("USER").build())) {
                //Получение списка показаний
                var meterReadings = this.getMeterReadingsByAddressId(addressId, userActive.getUserId(), acceptedParameters, dateFilter);
                //Вывод показаний пользователю
                return this.meterReadingsToStringConverter.apply(meterReadings);
            }
        }
        return null;
    }

    /**
     * Делает запрос в сервис для получения списка показаний
     *
     * @param addressId          идентификатор адреса, по которому будет запрос(может быть null)
     * @param userId             идентификатор пользователя, для которого будет выполнен запрос
     * @param acceptedParameters параметры типов показаний, по которых будет выполнен запрос(может быть null)
     * @param date               месяц и год, для вывода показаний в этот период
     * @return возвращает доступные показания по введенным параметрам
     */
    private List<MeterReading> getMeterReadingsByAddressId(Long addressId, Long userId, Set<String> acceptedParameters, YearMonth date) {
        var actualAddressesForActiveUser = this.addressService.getAddresses(userId).stream().map(Address::getAddressId).toList();

        if (addressId != null) {
            if (actualAddressesForActiveUser.contains(addressId)) {
                return this.meterReadingService.getActualMeterByAddress(addressId, acceptedParameters, date);
            } else {
                System.out.println("Не найдено адреса с Id: " + addressId);
                return List.of();
            }
        } else {
            var meterReadingsAllAddress = new ArrayList<MeterReading>();
            for (Long addressIdForActiveUser : actualAddressesForActiveUser) {
                meterReadingsAllAddress.addAll(this.meterReadingService.getActualMeterByAddress(addressIdForActiveUser, acceptedParameters, date));
            }
            return List.copyOf(meterReadingsAllAddress);
        }
    }


    /**
     * Функция, которая вызывается при выполнении команды help
     *
     * @param roles доступные роли пользователя
     * @return возвращает подсказку для пользователя по команде {@link  MeterValuesCommand#COMMAND}
     */
    @Override
    public String getHelpCommand(List<Role> roles) {
        if (roles.isEmpty()) {
            return null;
        }
        var userRole = Role.builder().roleName("USER").build();
        var userAdmin = Role.builder().roleName("ADMIN").build();
        var containsAdminRole = roles.contains(userAdmin);

        var helpUserIdParameter = containsAdminRole ? this.USER_ID_PARAMETER + " - достает показания по id пользователя(прим.: " + this.USER_ID_PARAMETER + "{userId}),\n       " : "";
        if (roles.contains(userRole) || containsAdminRole) {
            var typeMeter = this.typeMeterService.getTypeMeter();
            var collect = typeMeter.stream()
                    .map(type -> type.getTypeCode() + " - " + type.getTypeDescription())
                    .collect(Collectors.joining(",\n       "));
            return this.COMMAND + " - без параметров получает актуальное состояние показаний по всем адресам\n" +
                    "Дополнительные параметры: \n       " + helpUserIdParameter + this.ADDRESS_ID_PARAMETER + " - фильтрует показания по адресу(прим.: " + this.ADDRESS_ID_PARAMETER + "{addressId}),\n       " +
                    collect + ", \n       " + this.DATE_PARAMETER + " - фильтрует по дате(прим.:" + this.DATE_PARAMETER + "mm-YYYY" + ")" + ". \n       Пример: " + this.COMMAND + " " + this.ADDRESS_ID_PARAMETER + "{addressId} " + typeMeter.stream().findFirst().get().getTypeCode()+" "+this.DATE_PARAMETER+"12-1999";
        }
        return null;
    }


}
