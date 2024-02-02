package com.denknd.in.commands;

import com.denknd.entity.Roles;
import com.denknd.security.SecurityService;
import com.denknd.security.UserSecurity;
import lombok.RequiredArgsConstructor;

/**
 * Команда консоли для выхода из системы.
 */
@RequiredArgsConstructor
public class LogoutCommand implements ConsoleCommand {
  /**
   * Команда, отвечающая за выполнение данной команды.
   */
  private final String COMMAND_NAME = "logout";
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
    return "Выход из системы";
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
    if (command.equals(this.COMMAND_NAME)) {
      this.securityService.logout();
      return "До свидания!";
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
    return (role != null) ? COMMAND_NAME + " - выход из системы" : null;
  }
}
