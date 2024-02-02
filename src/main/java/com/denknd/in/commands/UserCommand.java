package com.denknd.in.commands;

import com.denknd.controllers.UserController;
import com.denknd.entity.Roles;
import com.denknd.in.commands.functions.LongIdParserFromRawParameters;
import com.denknd.in.commands.functions.MyFunction;
import com.denknd.security.UserSecurity;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

/**
 * Класс представляющий команду консоли, при помощи которой добавляется новый адрес.
 */
@Setter
@RequiredArgsConstructor
public class UserCommand implements ConsoleCommand {
  /**
   * Команда, которая отвечает за работу этого класса.
   */
  private final String COMMAND = "user";
  /**
   * Дополнительный параметр, для получения информации о пользователи по емайл.
   */
  private final String EMAIL_PARAM = "email=";
  /**
   * Дополнительный параметр, для получения информации о пользователи по айди.
   */
  private final String ID_PARAM = "id=";
  /**
   * Контролер для работы с пользователями
   */
  private final UserController userController;
  /**
   * Функция по извлечению из параметров числа.
   */
  private MyFunction<String[], Long> idParser
          = new LongIdParserFromRawParameters();

  /**
   * Возвращает команду, которая запускает работу метода run.
   *
   * @return команда для работы класса
   */
  @Override
  public String getCommand() {
    return this.COMMAND;
  }

  /**
   * Возвращает пояснение работы класса.
   *
   * @return пояснение, что делает класс, для аудита
   */
  @Override
  public String getAuditActionDescription() {
    return "Выдает информацию о пользователе";
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
    if (userActive == null || userActive.role() == null) {
      return null;
    }
    if (userActive.role().equals(Roles.USER)) {
      return this.userController.getUser(userActive.userId()).toString();
    } else if (userActive.role().equals(Roles.ADMIN)) {
      var result = processAdminCommand(command);
      if (result == null) {
        return "Без параметров возвратит null";
      }
      return result;
    }
    return null;
  }

  /**
   * Обработка команды администратора
   *
   * @param command команда полученная из консоли
   * @return результат команды
   */
  private String processAdminCommand(String command) {
    var commandAndParam = command.split(" ");
    var id = this.idParser.apply(commandAndParam, this.ID_PARAM);
    var email = getEmailFromCommand(commandAndParam);

    if (id != null) {
      return this.userController.getUser(id).toString();
    }
    if (email != null) {
      return this.userController.getUser(email).toString();
    }
    return null;
  }

  /**
   * Получения email из параметров
   *
   * @param commandAndParam параметры из консоли
   * @return Электронный адрес переданный в параметрах или null, если ни чего не передавалось
   */
  private String getEmailFromCommand(String[] commandAndParam) {
    return Arrays.stream(commandAndParam)
            .filter(param -> param.contains(this.EMAIL_PARAM))
            .map(param -> param.replace(this.EMAIL_PARAM, ""))
            .findFirst().orElse(null);
  }


  /**
   * Подсказка для команды help.
   *
   * @param role роль доступная пользователю
   * @return возвращает сообщение с подсказкой по работе с данной командой
   */
  @Override
  public String getHelpCommand(Roles role) {

    if (role == null) {
      return null;
    }
    var param = role.equals(Roles.ADMIN)
            ? "\nДополнительные параметры:\n        "
            + this.EMAIL_PARAM + " - показывает пользователя с данным email(пример: "
            + this.EMAIL_PARAM + "test@mail.ru),\n        "
            + this.ID_PARAM + " - показывает пользователя с данным айди(пример: "
            + this.ID_PARAM + "2345)"
            : "";
    if (role.equals(Roles.USER) || role.equals(Roles.ADMIN)) {
      return this.COMMAND + " - Команда для просмотра данных о пользователе" + param;
    }
    return null;
  }
}
