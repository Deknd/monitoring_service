package com.denknd.in;

import com.denknd.entity.User;
import com.denknd.in.commands.ConsoleCommand;
import com.denknd.services.AuditService;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Консоль, которая служит для взаимодействия с пользователем
 */
@RequiredArgsConstructor
public class Console {
    /**
     * Объект для хранения команд взаимодействие с пользователем. Ключ - команда, значение - класс для ее обработки.
     */
    private final Map<String, ConsoleCommand> consoleCommandMap = new HashMap<>();
    /**
     * Сканер для получения данных от пользователя
     */
    private final Scanner scanner;
    /**
     * Сервис для сохранения аудита
     */
    private final AuditService auditService;
    /**
     * Сообщения консоли
     */
    private static final String MESSAGE = "monitoring_service: ";
    /**
     * Активный юзер, при заходе в систему
     */
    private User activeUser = null;


    /**
     * циклическая обработка команд пользователя, пока активен сканер
     */
    public void run() {
        try {
            System.out.println("Получить информацию по доступным командам можно с помощью команды \"help\"");
            while (true) {
                String message;
                if(this.activeUser == null){
                    message= this.MESSAGE;
                } else {
                    message = this.activeUser.getFirstName() + "@" + this.MESSAGE;
                }
                System.out.print(message);
                String commandAndParams;
                try {
                    commandAndParams = this.scanner.nextLine();
                } catch (IllegalStateException e) {
                    System.out.println("Приложение закрывается");
                    break;
                }

                var command = commandAndParams.split(" ")[0];

                if (this.consoleCommandMap.containsKey(command)) {
                    var consoleCommand = this.consoleCommandMap.get(command);
                    var result = consoleCommand.run(
                            commandAndParams,
                            activeUser);
                    if (result instanceof String) {
                        System.out.println(result);
                    }
                    if (result instanceof User newUser) {
                        if(newUser.getUserId() == null){
                            this.activeUser = null;
                        }else {
                            this.activeUser = newUser;
                        }
                    }
                    this.auditService.addAction(this.consoleCommandMap, commandAndParams, activeUser);

                } else {
                    System.out.println("Данной команды не существует");
                }
            }
        } finally {
            if (scanner != null) {
                this.scanner.close();
            }
        }

    }

    /**
     * Метод для добавления команд консоли
     * @param command добавляет новые команды для обработки
     */
    public void addCommand(ConsoleCommand... command) {
        for (ConsoleCommand consoleCommand : command) {
            this.consoleCommandMap.put(consoleCommand.getCommand(), consoleCommand);
        }
    }

    /**
     * Выдает мапу с доступными командами консоли
     * @return мапа с текущими командами консоли
     */
    public Map<String, ConsoleCommand> commands() {
        return this.consoleCommandMap;
    }

}
