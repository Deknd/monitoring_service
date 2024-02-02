package com.denknd.in.commands;

import com.denknd.entity.Roles;
import com.denknd.in.Console;
import com.denknd.security.UserSecurity;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Команда консоли для предоставления подсказки по всем доступным командам.
 */
@RequiredArgsConstructor
public class HelpCommand implements ConsoleCommand {
  /**
   * Команда, отвечающая за выполнение данной команды.
   */
  private final String COMMAND_NAME = "help";
  /**
   * Консоль, из которой получается список доступных команд.
   */
  private final Console console;

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
    return "Выводит подсказку по другим командам";
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
    var commands = this.console.commands();
    Roles role = (userActive != null) ? userActive.role() : null;
    return commands.keySet()
            .stream()
            .map(keyCommand -> {
              var consoleCommand = commands.get(keyCommand);
              return consoleCommand.getHelpCommand(role);
            })
            .filter(Objects::nonNull)
            .collect(Collectors.joining("\n"));

  }

  /**
   * Подсказка для команды help.
   *
   * @param role роли, доступные пользователю
   * @return строка с подсказкой по работе с данной командой
   */
  @Override
  public String getHelpCommand(Roles role) {
    return this.COMMAND_NAME + " - выводит доступные команды";
  }
}
