package com.denknd.in;

import com.denknd.in.commands.ConsoleCommand;
import com.denknd.out.audit.AuditService;
import com.denknd.security.SecurityService;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Консоль, которая служит для взаимодействия с пользователем.
 */
@RequiredArgsConstructor
public class Console {
  /**
   * Объект для хранения команд взаимодействие с пользователем.
   * Ключ - команда, значение - класс для ее обработки.
   */
  private final Map<String, ConsoleCommand> consoleCommandMap = new HashMap<>();
  /**
   * Сканер для получения данных от пользователя.
   */
  private final Scanner scanner;
  /**
   * Сервис для сохранения аудита.
   */
  private final AuditService auditService;
  /**
   * Сообщения консоли.
   */
  private static final String MESSAGE = "monitoring_service: ";
  /**
   *
   */
  private final SecurityService securityService;


  /**
   * циклическая обработка команд пользователя, пока активен сканер.
   */
  public void run() {
    try {
      System.out.println("Получить информацию по доступным"
              + " командам можно с помощью команды \"help\"");
      while (true) {
        printConsolePrompt();
        String commandAndParams;
        try {
          commandAndParams = this.scanner.nextLine();
        } catch (IllegalStateException e) {
          System.out.println("Приложение закрывается");
          break;
        }
        handleCommand(commandAndParams);
      }
    } finally {
      if (scanner != null) {
        this.scanner.close();
      }
    }
  }

  /**
   * Начальное сообщение в консоли
   */
  private void printConsolePrompt() {
    String message = (this.securityService.isAuthentication())
            ? this.securityService.getUserSecurity().firstName() + "@" + MESSAGE
            : MESSAGE;
    System.out.print(message);
  }

  /**
   * Выполнение доступных команд
   * @param commandAndParams переданные в консоль команды
   */
  private void handleCommand(String commandAndParams) {
    var command = commandAndParams.split(" ")[0];
    if (this.consoleCommandMap.containsKey(command)) {
      var consoleCommand = this.consoleCommandMap.get(command);
      var result = consoleCommand.run(commandAndParams, this.securityService.getUserSecurity());
      System.out.println(result);
      this.auditService.addAction(this.consoleCommandMap, commandAndParams, this.securityService.getUserSecurity());
    } else {
      System.out.println("Данной команды не существует");
    }
  }

  /**
   * Метод для добавления команд консоли.
   *
   * @param command добавляет новые команды для обработки
   */
  public void addCommand(ConsoleCommand... command) {
    for (ConsoleCommand consoleCommand : command) {
      this.consoleCommandMap.put(consoleCommand.getCommand(), consoleCommand);
    }
  }

  /**
   * Выдает мапу с доступными командами консоли.
   *
   * @return мапа с текущими командами консоли
   */
  public Map<String, ConsoleCommand> commands() {
    return this.consoleCommandMap;
  }

}
