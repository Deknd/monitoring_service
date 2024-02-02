package com.denknd.in.commands;

import com.denknd.controllers.AddressController;
import com.denknd.dto.AddressDto;
import com.denknd.entity.Roles;
import com.denknd.in.commands.functions.LongIdParserFromRawParameters;
import com.denknd.in.commands.functions.MyFunction;
import com.denknd.security.UserSecurity;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс представляющий команду консоли для получения доступных адресов пользователя.
 */
@Setter
@RequiredArgsConstructor
public class GetAddressCommand implements ConsoleCommand {
  /**
   * Команда, которая отвечает за работу этого класса.
   */
  private final String COMMAND_NAME = "get_addr";
  /**
   * Параметр для получения идентификатора пользователя из консоли.
   */
  private final String USER_ID_PARAMETER = "user=";
  /**
   * Контролер для работы с адресами
   */
  private final AddressController addressController;
  /**
   * Парсер параметров в Long.
   */
  private MyFunction<String[], Long> longIdParserFromRawParameters
          = new LongIdParserFromRawParameters();


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
    return "Получает доступные адреса";
  }

  /**
   * Основной метод класса.
   *
   * @param command    команда полученная из консоли
   * @param userActive активный пользователь
   * @return возвращает сообщение об результате работы
   */
  @Override
  public String run(String command, UserSecurity userActive) {
    if (userActive == null || userActive.role() == null) {
      return null;
    }

    if (userActive.role().equals(Roles.USER)) {

      var addresses = this.addressController.getAddress(userActive.userId());
      if (addresses.isEmpty()) {
        return "У вас не зарегистрировано ни одного адреса";
      }
      var collect = this.convertAddressesToString(addresses);

      return "Ваши адреса: \n" + collect;
    } else if (userActive.role().equals(Roles.ADMIN)) {
      var commandAndParam = command.split(" ");

      var id = this.longIdParserFromRawParameters.apply(commandAndParam, this.USER_ID_PARAMETER);
      if (id != null) {
        var addresses = this.addressController.getAddress(id);
        if (addresses.isEmpty()) {
          return "У пользователя с id-" + id + " не зарегистрировано ни одного адреса";
        }
        var collect = this.convertAddressesToString(addresses);
        return "Адреса пользователя с id-" + id + " : \n" + collect;
      } else {
        return "Нет адресов у пользователя с id = " + id;
      }
    }
    return null;
  }

  /**
   * Конвертирует список адресов в строку.
   *
   * @param addresses список адресов
   * @return строку из адресов
   */
  private String convertAddressesToString(List<AddressDto> addresses) {
    return addresses.stream().map(address ->
                    "    "+address.toString())
            .collect(Collectors.joining(",\n    "));
  }

  /**
   * Подсказка для команды help.
   *
   * @param role роли доступные пользователю
   * @return строка с подсказкой по работе с данной командой
   */
  @Override
  public String getHelpCommand(Roles role) {
    if (role == null) {
      return null;
    }
    var paramMessage =
            role.equals(Roles.ADMIN)
                    ? "\nДополнительные параметры:\n    "
                    + this.USER_ID_PARAMETER
                    + " - ищет пользователя по заданному id(пример: "
                    + this.USER_ID_PARAMETER
                    + "234) Обязателен"
                    : "";
    if (role.equals(Roles.USER) || role.equals(Roles.ADMIN)) {
      return this.COMMAND_NAME + " - показывает адреса пользователя" + paramMessage;
    }
    return null;
  }
}
