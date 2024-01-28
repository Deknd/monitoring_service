package com.denknd.in.commands;

import com.denknd.entity.Role;
import com.denknd.entity.User;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Scanner;
/**
 * Класс представляющий команду консоли, при помощи которой происходит закрытие программы
 */
@RequiredArgsConstructor
public class ExitCommand implements ConsoleCommand<String> {
    /**
     * Команда, которая отвечает за работу этого класса
     */
    private final String COMMAND = "exit";
    /**
     * Сканер консоли
     */
    private final Scanner scanner;
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
        return "Выход из программы";
    }
    /**
     * Основной метод класса
     * @param command команда полученная из консоли
     * @param userActive активный юзер
     * @return возвращает сообщение об результате работы
     */
    @Override
    public String run(String command, User userActive) {
        if (command.equals(this.COMMAND)) {
            this.scanner.close();
        }
        return "Команда не поддерживается: " + command;
    }
    /**
     * Подсказка для команды help
     * @param roles роли доступные пользователю
     * @return возвращает сообщение с подсказкой по работе с данной командой
     */
    @Override
    public String getHelpCommand(List<Role> roles) {
        return this.COMMAND + " - выход из приложения";
    }
}
