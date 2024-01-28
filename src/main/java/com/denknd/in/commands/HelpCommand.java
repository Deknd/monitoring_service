package com.denknd.in.commands;

import com.denknd.entity.Role;
import com.denknd.entity.User;
import com.denknd.in.Console;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Класс представляющий команду консоли, при помощи которой предоставляется подсказка по всем командам
 */
@RequiredArgsConstructor
public class HelpCommand implements ConsoleCommand<String> {
    /**
     * Команда, которая отвечает за работу этого класса
     */
    private final String COMMAND = "help";
    /**
     * Консоль, из нее получается список доступных команд
     */
    private final Console console;
    /**
     * Возвращает команду, которая запускает работу метода run
     * @return команда для работы класса
     */
    @Override
    public String getCommand() {
        return this.COMMAND;
    }
    /**
     * Возвращает пояснение работы класса
     * @return пояснение, что делает класс, для аудита
     */
    @Override
    public String getMakesAction() {
        return "Выводит подсказку по другим командам";
    }
    /**
     * Основной метод класса
     * @param command команда полученная из консоли
     * @param userActive активный юзер
     * @return возвращает сообщение об результате работы
     */
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
    /**
     * Подсказка для команды help
     * @param roles роли доступные пользователю
     * @return возвращает сообщение с подсказкой по работе с данной командой
     */
    @Override
    public String getHelpCommand(List<Role> roles) {
        return this.COMMAND + " - выводит доступные команды";
    }
}
