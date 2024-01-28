package com.denknd.in.commands;

import com.denknd.entity.Role;
import com.denknd.entity.User;

import java.util.List;
/**
 * Класс представляющий команду консоли, при помощи которой выходят из своего аккаунта
 */
public class LogoutCommand implements ConsoleCommand<User>{
    /**
     * Команда, которая отвечает за работу этого класса
     */
    private final String COMMAND = "logout";
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
        return "Выход из системы";
    }
    /**
     * Основной метод класса
     * @param command команда полученная из консоли
     * @param userActive активный юзер
     * @return возвращает сообщение об результате работы
     */
    @Override
    public User run(String command, User userActive) {
        if(userActive == null){
            return null;
        }
        if(command.equals(this.COMMAND)){
            return new User();
        }
        System.out.println("Команда не поддерживается: " + command);
        return null;
    }
    /**
     * Подсказка для команды help
     * @param roles роли доступные пользователю
     * @return возвращает сообщение с подсказкой по работе с данной командой
     */
    @Override
    public String getHelpCommand(List<Role> roles) {
        if(roles != null && !roles.isEmpty()){
            return this.COMMAND+" - выход из системы";
        } else return null;
    }
}
