package com.denknd.in.commands;

import com.denknd.entity.Address;
import com.denknd.entity.MeterReading;
import com.denknd.entity.Role;
import com.denknd.entity.User;
import com.denknd.in.commands.functions.TypeMeterParametersParserFromRawParameters;
import com.denknd.services.AddressService;
import com.denknd.services.MeterReadingService;
import com.denknd.services.TypeMeterService;
import com.denknd.validator.IValidator;
import com.denknd.validator.Validators;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Setter
public class MeterSendCommand implements ConsoleCommand<String> {

    private final String COMMAND = "send";
    private final Role USER_ROLE = Role.builder().roleName("USER").build();
    private final TypeMeterService typeMeterService;
    private final AddressService addressService;
    private final MeterReadingService meterReadingService;
    private Function<String[], Set<String>> typeMeterParametersParserFromRawParameters;
    private final Validators validators;
    private final Scanner scanner;

    public MeterSendCommand(TypeMeterService typeMeterService, AddressService addressService, MeterReadingService meterReadingService, Validators validators, Scanner scanner) {
        this.typeMeterService = typeMeterService;
        this.addressService = addressService;
        this.meterReadingService = meterReadingService;
        this.validators = validators;
        this.scanner = scanner;
        this.typeMeterParametersParserFromRawParameters = new TypeMeterParametersParserFromRawParameters(this.typeMeterService);
    }

    @Override
    public String getCommand() {
        return this.COMMAND;
    }

    @Override
    public String getMakesAction() {
        return "Отправляет показания";
    }

    @Override
    public String run(String command, User userActive) {
        if (userActive == null) {
            return null;
        }
        if (userActive.getRoles().contains(this.USER_ROLE)) {
            //получаем доступные адреса пользователя
            var addresses = this.addressService.getAddresses(userActive.getUserId());
            if (addresses.isEmpty()) {
                return "У вас не добавлено ни одного адреса";
            }

            var commands = command.split(" ");
            //достаем доп параметр из команды
            var typeMeterParam = this.typeMeterParametersParserFromRawParameters.apply(commands).stream().findAny().orElse(null);
            //получаем доступные типы показаний
            var typeMeterList = this.typeMeterService.getTypeMeter();
            //проверяем, что полученный от пользователя код, есть в нашем списке
            if (typeMeterParam!=null) {
                //Достаем из списка адресов, все айдишники
                var collectId = addresses.stream().map(Address::getAddressId).toList();
                //получаем от пользователя айди адреса, на который он хочет подать показания
                var idAddress = getIdAddress(addresses);
                if (!collectId.contains(idAddress)) {
                    return "Данный адрес записан не на вас";
                }
                //Достаем из списка адресов, адрес по введенному айди
                var address = addresses.stream().filter(addr -> addr.getAddressId().equals(idAddress)).findFirst().get();

                //получаем из списка объект с типом показаний
                var typeMeter = typeMeterList.stream().filter(type -> type.getTypeCode().equals(typeMeterParam)).findFirst().get();

                //Получаем актуальные показания по данному типу
                var actualMeter = this.meterReadingService.getActualMeter(idAddress, typeMeterParam);
                var submissionMonth = YearMonth.now();
                //Проверяем, что данные за данный месяц еще не внесены
                if (actualMeter!= null && submissionMonth.isBefore(actualMeter.getSubmissionMonth())
                        || actualMeter != null &&submissionMonth.equals(actualMeter.getSubmissionMonth())) {
                    return "Данные за " + submissionMonth + " уже внесены";
                }
                //получаем от пользователя показания
                var meterValue = getMeterValue();
                if (meterValue == null) {
                    return "Нужно обязательно вводить показания";
                }
                if (actualMeter == null || actualMeter.getMeter() == null) {
                    System.out.println("Нужно вызвать мастера для проверки и пломбирования счетчика");
                }
                //Если были показания до этого и они меньше, чем сейчас переданные, то работа метода прекращается
                if (actualMeter != null && Double.compare(meterValue, actualMeter.getMeterValue()) < 0) {
                    return "Не верные показания или новый счетчик. Вызовите мастера для проверки и пломбирования счетчика";
                }

                var meterReadingValue = MeterReading.builder()
                        .address(address)
                        .typeMeter(typeMeter)
                        .meterValue(meterValue)
                        .submissionMonth(submissionMonth)
                        .meter(actualMeter!=null && actualMeter.getMeter() != null ? actualMeter.getMeter() : null)
                        .timeSendMeter(OffsetDateTime.now())
                        .build();
                var meterReadingResult = this.meterReadingService.addMeterValue(meterReadingValue);
                return "Показания приняты: \n" + meterReadingResult;

            }
        }

        return null;
    }


    private Long getIdAddress(List<Address> addresses) {
        var collectAddresses = addresses.stream().map(address -> address.getAddressId() + " - " + address.toString()).collect(Collectors.joining("\n"));
        System.out.println("Ваши адреса:\n" + collectAddresses);
        var addressIdRaw = this.validators.isValid(
                "Введите Id вашего адреса: ",
                IValidator.DIGITAL_TYPE,
                "Нужно ввести Id вашего адреса, он выведен выше",
                this.scanner
        );
        try {
            return Long.parseLong(addressIdRaw);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    private Double getMeterValue() {
        var meterValue = this.validators.isValid(
                "Введите показания: ",
                IValidator.DOUBLE_TYPE,
                "Введите цифры с счетчика(прим. \"12345\", \"234234.234\")",
                this.scanner
        );
        try {
            return Double.parseDouble(meterValue);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    @Override
    public String getHelpCommand(List<Role> roles) {
        if (roles.isEmpty()) {
            return null;
        }
        if (roles.contains(Role.builder().roleName("USER").build())) {
            var typeMeterList = this.typeMeterService.getTypeMeter();
            var collect = typeMeterList.stream().map(typeMeter -> typeMeter.getTypeCode() + " - " + typeMeter.getTypeDescription()).collect(Collectors.joining(", "));
            return this.COMMAND +
                    " - используется для отправки показаний счетчиков. Обязательный параметры: "
                    + collect + ". Пример: " + this.COMMAND + " " + typeMeterList.get(0).getTypeCode();

        }
        return null;
    }


}
