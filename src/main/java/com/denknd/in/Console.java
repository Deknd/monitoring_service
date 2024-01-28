package com.denknd.in;

import com.denknd.entity.User;
import com.denknd.in.commands.ConsoleCommand;
import com.denknd.services.AuditService;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Console {

    private final Map<String, ConsoleCommand> consoleCommandMap;
    private final Scanner scanner;
    private final AuditService auditService;
    private static final String MESSAGE = "monitoring_service: ";

    private User activeUser;

    public Console(Scanner scanner, AuditService auditService) {
        this.consoleCommandMap = new HashMap<>();
        this.scanner = scanner;
        this.auditService = auditService;
        this.activeUser = null;
    }


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


    public void addCommand(ConsoleCommand command) {
        this.consoleCommandMap.put(command.getCommand(), command);
    }

    public void addCommand(ConsoleCommand... command) {
        for (ConsoleCommand consoleCommand : command) {
            this.consoleCommandMap.put(consoleCommand.getCommand(), consoleCommand);
        }
    }

    public Map<String, ConsoleCommand> commands() {
        return this.consoleCommandMap;
    }

}
