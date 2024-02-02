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
import java.util.stream.Collectors;

/**
 * Обрабатывает команду, полученную из консоли, для получения актуальных показаний счетчиков.
 */
@Setter
public class MeterValuesCommand implements ConsoleCommand {
  /**
   * Команда, которая будет обрабатываться в консоли.
   */
  private final String COMMAND_NAME = "meter-values";
  /**
   * Дополнительный параметр, фильтрующий вывод по адресу (addressId).
   */
  private final String ADDRESS_ID_PARAMETER = "addr=";
  /**
   * Дополнительный параметр, фильтрующий вывод по пользователю (userId). Доступен только админу.
   */
  private final String USER_ID_PARAMETER = "user=";
  /**
   * Дополнительный параметр, фильтрующий вывод по дате.
   */
  private final String DATE_PARAMETER = "data=";
  /**
   * Контролер для работы с показаниями.
   */
  private final MeterReadingController meterReadingController;
  /**
   * Контролер для работы с типами показаний.
   */
  private final TypeMeterController typeMeterController;
  /**
   * Функция, конвертирующая список показаний в строку.
   */
  private Function<List<MeterReadingResponseDto>, String> meterReadingsToStringConverter;
  /**
   * Функция, извлекающая из введенных параметров типы показаний.
   */
  private Function<String[], Set<String>> typeMeterParametersParserFromRawParameters;
  /**
   * Парсит введенные параметры и извлекает из них Id пользователя.
   */
  private MyFunction<String[], Long> longIdParserFromRawParameters;
  /**
   * Парсит введенные параметры и извлекает из них дату.
   */
  private MyFunction<String[], YearMonth> dateParserFromRawParameters;

  /**
   * Обрабатывает команду, полученную из консоли, для получения актуальных показаний счетчиков.
   *
   * @param typeMeterController    Контроллер для работы с типами показаний.
   * @param meterReadingController Контроллер для работы с показаниями.
   */
  public MeterValuesCommand(TypeMeterController typeMeterController, MeterReadingController meterReadingController) {
    this.meterReadingController = meterReadingController;
    this.typeMeterController = typeMeterController;
    this.meterReadingsToStringConverter = new DefaultMeterReadingsToStringConverter();
    this.typeMeterParametersParserFromRawParameters = new TypeMeterParametersParserFromRawParameters(typeMeterController);
    this.longIdParserFromRawParameters = new LongIdParserFromRawParameters();
    this.dateParserFromRawParameters = new DateParserFromRawParameters();
  }

  /**
   * Возвращает команду для обработки в консоли.
   *
   * @return возвращает {@link MeterValuesCommand#COMMAND_NAME}
   */
  @Override
  public String getCommand() {
    return this.COMMAND_NAME;
  }

  /**
   * Возвращает пояснение работы класса.
   *
   * @return пояснение, что делает класс, для аудита
   */
  @Override
  public String getAuditActionDescription() {
    return "Получает актуальные показание";
  }


  /**
   * Обрабатывает запрос из консоли.
   *
   * @param command    команда, введенная пользователем
   * @param userActive пользователь, который сейчас находится в системе
   * @return возвращает показания, доступные пользователю по введенным параметрам
   */
  @Override
  public String run(String command, UserSecurity userActive) {
    if (userActive == null || userActive.role() == null) {
      return null;
    }
    var commandAndParam = command.split(" ");
    if (Arrays.asList(commandAndParam).contains(this.COMMAND_NAME)) {
      var apply = this.typeMeterParametersParserFromRawParameters.apply(commandAndParam);
      var dateFilter = this.dateParserFromRawParameters.apply(commandAndParam, this.DATE_PARAMETER);
      var addressId = this.longIdParserFromRawParameters.apply(commandAndParam, this.ADDRESS_ID_PARAMETER);
      if (userActive.role().equals(Roles.ADMIN)) {
        var userId = this.longIdParserFromRawParameters.apply(commandAndParam, this.USER_ID_PARAMETER);
        var meterReadings =
                this.meterReadingController.getMeterReadings(
                        addressId,
                        userId,
                        apply,
                        dateFilter);
        return this.meterReadingsToStringConverter.apply(meterReadings);

      } else if (userActive.role().equals(Roles.USER)) {

        var meterReadings =
                this.meterReadingController.getMeterReadings(
                        addressId,
                        userActive.userId(),
                        apply,
                        dateFilter);
        return this.meterReadingsToStringConverter.apply(meterReadings);
      }
    }
    return null;
  }


  /**
   * Функция, вызываемая при выполнении команды help.
   *
   * @param role доступная роль пользователя
   * @return возвращает подсказку для пользователя по команде {@link MeterValuesCommand#COMMAND_NAME}
   */
  @Override
  public String getHelpCommand(Roles role) {
    if (role == null) {
      return null;
    }
    var containsAdminRole = role.equals(Roles.ADMIN);

    var helpUserIdParameter = containsAdminRole
            ?
            this.USER_ID_PARAMETER
                    + " - достает показания по id пользователя(прим.: " + this.USER_ID_PARAMETER
                    + "{userId}),\n       "
            :
            "";
    if (role.equals(Roles.USER) || containsAdminRole) {
      var typeMeter = this.typeMeterController.getTypeMeterCodes();
      var collect = typeMeter.stream()
              .map(type -> type.typeCode() + " - " + type.typeDescription())
              .collect(Collectors.joining(",\n       "));
      return this.COMMAND_NAME + " - без параметров получает актуальное состояние показаний по всем адресам\n"
              + "Дополнительные параметры: \n       " + helpUserIdParameter + this.ADDRESS_ID_PARAMETER
              + " - фильтрует показания по адресу(прим.: " + this.ADDRESS_ID_PARAMETER + "{addressId}),\n       "
              + collect + ", \n       " + this.DATE_PARAMETER + " - фильтрует по дате(прим.:"
              + this.DATE_PARAMETER + "mm-YYYY" + ")" + ". \n       Пример: " + this.COMMAND_NAME + " "
              + this.ADDRESS_ID_PARAMETER + "{addressId} " + typeMeter.stream().findFirst().get().typeCode()
              + " " + this.DATE_PARAMETER + "12-1999";
    }
    return null;
  }


}
