package com.denknd.in.commands;

import com.denknd.entity.Roles;
import com.denknd.security.SecurityService;
import com.denknd.security.UserSecurity;
import com.denknd.validator.DataValidatorManager;
import com.denknd.validator.Validator;
import lombok.RequiredArgsConstructor;

/**
 * Команда консоли для входа в систему.
 */
@RequiredArgsConstructor
public class LoginCommand implements ConsoleCommand {
  /**
   * Команда, отвечающая за выполнение данной команды.
   */
  private final String COMMAND_NAME = "login";

  /**
   * Валидатор принятых данных.
   */
  private final DataValidatorManager dataValidatorManager;
  /**
   * Сервис для авторизации пользователя
   */
  private final SecurityService securityService;

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
    return "Вход в систему";
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
    if (userActive != null) {
      return null;
    }
    if (command.equals(this.COMMAND_NAME)) {
      var email = this.dataValidatorManager.getValidInput(
              "Введите email: ",
              Validator.EMAIL_TYPE,
              "Email введен некорректно");
      var rawPassword = this.dataValidatorManager.getValidInput(
              "Введите пароль: ",
              Validator.PASSWORD_TYPE,
              "Пароль слишком короткий");
      if (!this.dataValidatorManager.areAllValuesNotNullAndNotEmpty(email, rawPassword)) {
        return "\nДанные введены некорректно";
      }
      var user = this.securityService.authentication(email, rawPassword);
      if (user == null) {
        return "Логин или пароль не верные";
      }
      return "Авторизация успешна";
    }
    return "Команда не поддерживается: " + command;
  }

  /**
   * Подсказка для команды help.
   *
   * @param role роль доступная пользователю
   * @return возвращает сообщение с подсказкой по работе с данной командой
   */
  @Override
  public String getHelpCommand(Roles role) {
    return role == null ? COMMAND_NAME + " - вход в систему" : null;
  }
}
