package com.denknd.in.commands;

import com.denknd.entity.Role;
import com.denknd.entity.User;
import com.denknd.port.IGivingAudit;

import java.util.List;

/**
 * Интерфейс представляющий команду консоли, при помощи которого добавляются новые команды для консоли
 */
public interface ConsoleCommand<T>  extends IGivingAudit {
    /**
     * Возвращает команду, которая запускает работу метода run
     * @return команда для работы класса
     */
    String getCommand();
    /**
     * Основной метод класса
     * @param command команда полученная из консоли
     * @param userActive активный юзер
     * @return возвращает результат работы метода
     */
    T run(String command, User userActive);
    /**
     * Подсказка для команды help
     * @param roles роли доступные пользователю
     * @return возвращает сообщение с подсказкой по работе с данной командой
     */
    String getHelpCommand(List<Role> roles);
}
