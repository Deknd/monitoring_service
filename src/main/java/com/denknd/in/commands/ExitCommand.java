package com.denknd.in.commands;

import com.denknd.entity.Roles;
import com.denknd.security.UserSecurity;
import lombok.RequiredArgsConstructor;

import java.util.Scanner;

/**
 * Класс представляющий команду консоли для завершения программы.
 */
@RequiredArgsConstructor
public class ExitCommand implements ConsoleCommand {
  /**
   * Команда, которая отвечает за работу этого класса.
   */
  private final String COMMAND_NAME = "exit";
  /**
   * Сканер консоли.
   */
  private final Scanner scanner;

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
    return "Выход из программы";
  }


  /**
   * Метод для завершения программы.
   *
   * @param command    команда полученная из консоли
   * @param userActive активный пользователь
   * @return строка с сообщением о результате работы
   */
  @Override
  public String run(String command, UserSecurity userActive) {
    if (command.equals(this.COMMAND_NAME)) {
      this.scanner.close();
    }
    return "Команда не поддерживается: " + command;
  }

  /**
   * Возвращает подсказку для команды help.
   *
   * @param role роль, доступная пользователю
   * @return строка с подсказкой по использованию данной команды
   */
  @Override
  public String getHelpCommand(Roles role) {
    return this.COMMAND_NAME + " - выход из приложения";
  }
}
