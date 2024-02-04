package com.denknd.in.commands;

import com.denknd.controllers.MeterReadingController;
import com.denknd.controllers.TypeMeterController;
import com.denknd.dto.MeterReadingResponseDto;
import com.denknd.entity.Roles;
import com.denknd.in.commands.functions.*;
import com.denknd.security.UserSecurity;
import lombok.Setter;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Команда консоли для вывода истории подачи показаний.
 */
@Setter
public class HistoryCommand implements ConsoleCommand {
  /**
   * Команда, отвечающая за выполнение данной команды.
   */
  private final String COMMAND_NAME = "history";
  /**
   * Дополнительный параметр для передачи идентификатора адреса.
   */
  private final String ADDRESS_ID_PARAMETER = "addr=";
  /**
   * Дополнительный параметр для передачи идентификатора пользователя.
   */
  private final String USER_ID_PARAMETER = "user=";
  /**
   * Дополнительный параметр для передачи даты начала списка.
   */
  private final String START_DATE_PARAMETER = "start_date=";
  /**
   * Дополнительный параметр для передачи даты конца списка.
   */
  private final String END_DATE_PARAMETER = "end_date=";
  /**
   * Контролер для работы с показаниями
   */
  private final MeterReadingController meterReadingController;
  /**
   * Извлекает доступные типы показаний из консольной команды.
   */
  private Function<String[], Set<String>> typeMeterParametersParserFromRawParameters;
  /**
   * Конвертирует историю показаний в строку.
   */
  private Function<List<MeterReadingResponseDto>, String> meterReadingsHistoryToStringConverter;
  /**
   * Извлекает из консольной команды значение типа Long.
   */
  private MyFunction<String[], Long> longIdParserFromRawParameters;
  /**
   * Извлекает из консольной команды значение типа YearMonth.
   */
  private MyFunction<String[], YearMonth> dateParserFromRawParameters;


  public HistoryCommand(
          TypeMeterController typeMeterController,
          MeterReadingController meterReadingController
  ) {

    this.meterReadingController = meterReadingController;
    this.meterReadingsHistoryToStringConverter = new MeterReadingsHistoryToStringConverter();
    this.typeMeterParametersParserFromRawParameters
            = new TypeMeterParametersParserFromRawParameters(typeMeterController);
    this.longIdParserFromRawParameters = new LongIdParserFromRawParameters();
    this.dateParserFromRawParameters = new DateParserFromRawParameters();
  }

  /**
   * Возвращает строку с командой, которая запускает выполнение метода run.
   *
   * @return строка с командой для выполнения действия класса
   */
  @Override
  public String getCommand() {
    return this.COMMAND_NAME;
  }

  /**
   * Возвращает пояснение к работе класса.
   *
   * @return пояснение к работе класса для аудита
   */
  @Override
  public String getAuditActionDescription() {
    return "Выводит историю подачи показаний";
  }

  /**
   * Основной метод класса.
   *
   * @param command    команда полученная из консоли
   * @param userActive активный юзер
   * @return возвращает сообщение об результате работы
   */
  @Override
  public String run(String command, UserSecurity userActive) {
    if (userActive == null) {
      return null;
    }
    var commandAndParam = command.split(" ");
    if (Arrays.asList(commandAndParam).contains(this.COMMAND_NAME)) {
      var acceptedParameters
              = this.typeMeterParametersParserFromRawParameters.apply(commandAndParam);
      var addressId = this.longIdParserFromRawParameters.apply(
              commandAndParam,
              this.ADDRESS_ID_PARAMETER);
      var startDate = this.dateParserFromRawParameters.apply(
              commandAndParam,
              this.START_DATE_PARAMETER);
      var endDate = this.dateParserFromRawParameters.apply(
              commandAndParam,
              this.END_DATE_PARAMETER);

      if (userActive.role().equals(Roles.ADMIN)) {
        var userId = this.longIdParserFromRawParameters.apply(
                commandAndParam,
                this.USER_ID_PARAMETER);

        var meterReadings =
                this.meterReadingController.getHistoryMeterReading(
                        addressId,
                        userId,
                        acceptedParameters,
                        startDate,
                        endDate);


        return this.meterReadingsHistoryToStringConverter.apply(meterReadings);
      } else if (userActive.role().equals(Roles.USER)) {
        var meterReadings =
                this.meterReadingController.getHistoryMeterReading(
                        addressId,
                        userActive.userId(),
                        acceptedParameters,
                        startDate,
                        endDate
                );
        return this.meterReadingsHistoryToStringConverter.apply(meterReadings);
      }
    }
    return null;
  }


  /**
   * Подсказка для команды help.
   *
   * @param role роль доступная пользователю
   * @return возвращает сообщение с подсказкой по работе с данной командой
   */
  @Override
  public String getHelpCommand(Roles role) {
    if (role== null) {
      return null;
    }
    var containsAdminRole = role.equals(Roles.ADMIN);
    var helpUserIdParameter = containsAdminRole
            ?
            this.USER_ID_PARAMETER
                    + " - извлекает показания по идентификатору пользователя (пример: "
                    + USER_ID_PARAMETER
                    + "{userId}),\n               "
            : "";

    if (role.equals(Roles.USER) || containsAdminRole) {

      return this.COMMAND_NAME
              + " - без параметров возвращает всю историю подачи показаний "
              + "пользователя по всем его адресам.\n Дополнительные параметры: \n               "
              + helpUserIdParameter
              + this.ADDRESS_ID_PARAMETER + " - добавляет фильтрацию по адресу(прим.: "
              + this.ADDRESS_ID_PARAMETER + "{addressId}),\n               "
              + this.START_DATE_PARAMETER + " - добавляет фильтрацию по"
              + " дате начала выдачи истории(прим.: "
              + this.START_DATE_PARAMETER + "12-1999), \n               "
              + this.END_DATE_PARAMETER + " - добавляет фильтрацию по"
              + " дате до которой нужна история(прим.: "
              + this.END_DATE_PARAMETER + "20-2023)";
    }
    return null;
  }
}
