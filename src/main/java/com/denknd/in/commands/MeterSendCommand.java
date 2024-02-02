package com.denknd.in.commands;

import com.denknd.controllers.AddressController;
import com.denknd.controllers.MeterReadingController;
import com.denknd.controllers.TypeMeterController;
import com.denknd.dto.MeterReadingRequestDto;
import com.denknd.entity.Roles;
import com.denknd.exception.MeterReadingConflictError;
import com.denknd.in.commands.functions.TypeMeterParametersParserFromRawParameters;
import com.denknd.security.UserSecurity;
import com.denknd.validator.DataValidatorManager;
import com.denknd.validator.Validator;
import lombok.Setter;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Класс представляющий команду консоли, при помощи которой отправляются показания счетчиков.
 */
@Setter
public class MeterSendCommand implements ConsoleCommand {
  /**
   * Команда, отвечающая за работу данного класса.
   */
  private final String COMMAND_NAME = "send";


  /**
   * Контролер для работы с адресами.
   */
  private final AddressController addressController;
  /**
   * Контролер для работы с показаниями.
   */
  private final MeterReadingController meterReadingController;
  /**
   * Контролер для работы с типами показаний.
   */
  private final TypeMeterController typeMeterController;

  /**
   * Функция, извлекающая из массива с параметрами нужные типы показаний.
   */
  private Function<String[], Set<String>> typeMeterParametersParserFromRawParameters;
  /**
   * Валидатор принятых данных.
   */
  private final DataValidatorManager dataValidatorManager;


  /**
   * Основной конструктор.
   *
   * @param typeMeterController    Контроллер для работы с типами показаний
   * @param addressController      Контроллер для работы с адресами
   * @param meterReadingController Контроллер для работы с показаниями
   * @param dataValidatorManager   Валидатор входящих данных
   */
  public MeterSendCommand(
          TypeMeterController typeMeterController,
          AddressController addressController,
          MeterReadingController meterReadingController,
          DataValidatorManager dataValidatorManager
  ) {
    this.typeMeterController = typeMeterController;
    this.addressController = addressController;
    this.meterReadingController = meterReadingController;
    this.dataValidatorManager = dataValidatorManager;
    this.typeMeterParametersParserFromRawParameters
            = new TypeMeterParametersParserFromRawParameters(
            typeMeterController);
  }

  /**
   * Возвращает команду, которая запускает выполнение метода run.
   *
   * @return команда для работы класса
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
    return "Отправляет показания";
  }

  /**
   * Основной метод класса.
   *
   * @param command    команда, полученная из консоли
   * @param userActive активный пользователь
   * @return возвращает сообщение об результате работы
   */
  @Override
  public String run(String command, UserSecurity userActive) {
    if (userActive == null) {
      return null;
    }
    if (userActive.role().equals(Roles.USER)) {
      var commands = command.split(" ");
      var parameterForSubmittingReadings
              = this.typeMeterParametersParserFromRawParameters.apply(commands)
              .stream()
              .findAny()
              .orElse(null);
      if (parameterForSubmittingReadings != null) {
        var idAddress = getIdAddress(userActive.userId());
        if (idAddress == null) {
          return "Обязательно нужно вводить идентификатор адреса";
        }
        var meterValue = getMeterValue();
        if (meterValue == null) {
          return "Нужно обязательно вводить показания";
        }
        var meterReadingDto = MeterReadingRequestDto.builder()
                .meterValue(meterValue)
                .addressId(idAddress)
                .code(parameterForSubmittingReadings)
                .build();
        try {
          var meterReadingResult
                  = this.meterReadingController.addMeterReadingValue(meterReadingDto, userActive.userId());
          return "Показания приняты: \n" + meterReadingResult;
        } catch (MeterReadingConflictError e) {
          return e.getMessage();
        }
      }
    }
    return null;
  }

  /**
   * Получает ввод из консоли.
   *
   * @param userId Идентификатор пользователя
   * @return возвращает айди адреса
   */
  private Long getIdAddress(Long userId) {
    var addressesByActiveUser
            = this.addressController.getAddress(userId);
    if (addressesByActiveUser.isEmpty()) {
      System.out.println("У вас не добавлено адресов. Добавьте адрес, чтоб подать показания");
      return null;
    }
    var collectAddresses = addressesByActiveUser
            .stream()
            .map(Record::toString)
            .collect(Collectors.joining("\n"));
    System.out.println("Ваши адреса:\n" + collectAddresses);
    var addressIdRaw = this.dataValidatorManager.getValidInput(
            "Введите Id вашего адреса: ",
            Validator.DIGITAL_TYPE,
            "Нужно ввести Id вашего адреса, он выведен выше"
    );
    try {
      return Long.parseLong(addressIdRaw);
    } catch (NumberFormatException | NullPointerException e) {
      return null;
    }
  }

  /**
   * Получает с консоли показания.
   *
   * @return показания в формате Double
   */
  private Double getMeterValue() {
    var meterValue = this.dataValidatorManager.getValidInput(
            "Введите показания: ",
            Validator.DOUBLE_TYPE,
            "Введите цифры с счетчика(прим. \"12345\", \"234234.234\")"
    );
    try {
      return Double.parseDouble(meterValue);
    } catch (NumberFormatException | NullPointerException e) {
      return null;
    }
  }

  /**
   * Подсказка для команды help.
   *
   * @param role роль, доступная пользователю
   * @return возвращает сообщение с подсказкой по работе с данной командой
   */
  @Override
  public String getHelpCommand(Roles role) {
    if (role == null) {
      return null;
    }
    if (role.equals(Roles.USER)) {
      var typeMeters = this.typeMeterController.getTypeMeterCodes();
      var collect = typeMeters
              .stream()
              .map(typeMeter -> typeMeter.typeCode() + " - " + typeMeter.typeDescription())
              .collect(Collectors.joining(", "));
      return this.COMMAND_NAME
              + " - используется для отправки показаний счетчиков. Обязательный параметры: "
              + collect + ". Пример: " + this.COMMAND_NAME + " " + typeMeters.stream().findAny().get().typeCode();
    }
    return null;
  }
}
