package com.denknd.in.commands;

import com.denknd.entity.Role;
import com.denknd.entity.User;
import com.denknd.in.Console;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class HelpCommand implements ConsoleCommand<String> {

    private final String COMMAND = "help";
    private final Console console;


    @Override
    public String getCommand() {
        return this.COMMAND;
    }

    @Override
    public String getMakesAction() {
        return "Выводит подсказку по другим командам";
    }

    @Override
    public String run(String command, User userActive) {
        var commands = this.console.commands();
        var roles = (userActive != null) ? userActive.getRoles() : List.of();
        return commands.keySet()
                .stream()
                .map(keyCommand -> {
                    var consoleCommand = commands.get(keyCommand);
                    return consoleCommand.getHelpCommand(roles);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n"));

    }

    @Override
    public String getHelpCommand(List<Role> roles) {
        return this.COMMAND + " - выводит доступные команды";
    }
}
