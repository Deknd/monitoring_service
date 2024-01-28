package com.denknd.in.commands;

import com.denknd.entity.Role;
import com.denknd.entity.User;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
public class ExitCommand implements ConsoleCommand<String> {

    private final String COMMAND = "exit";
    private final Scanner scanner;

    @Override
    public String getCommand() {
        return this.COMMAND;
    }

    @Override
    public String getMakesAction() {
        return "Выход из программы";
    }

    @Override
    public String run(String command, User userActive) {
        if (command.equals(this.COMMAND)) {
            this.scanner.close();
        }
        return "Команда не поддерживается: " + command;
    }

    @Override
    public String getHelpCommand(List<Role> roles) {
        return this.COMMAND + " - выход из приложения";
    }
}
