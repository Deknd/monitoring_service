package com.denknd.in.commands;

import com.denknd.entity.Role;
import com.denknd.entity.User;
import com.denknd.services.RoleService;
import com.denknd.services.UserService;
import com.denknd.validator.IValidator;
import com.denknd.validator.Validators;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Scanner;
/**
 * Класс представляющий команду консоли, при помощи которой заходят на свой аккаунт
 */
@RequiredArgsConstructor
public class LoginCommand implements ConsoleCommand<User> {
    /**
     * Команда, которая отвечает за работу этого класса
     */
    private final String COMMAND = "login";
    /**
     * Сервис для работы с пользователями
     */
    private final UserService userService;
    /**
     * Сервис для работы с ролями
     */
    private final RoleService roleService;
    /**
     * Валидатор принятых данных
     */
    private final Validators validators;
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
        return "Вход в систему";
    }
    /**
     * Основной метод класса
     * @param command команда полученная из консоли
     * @param userActive активный юзер
     * @return возвращает сообщение об результате работы
     */
    @Override
    public User run(String command, User userActive) {
        if (userActive != null) {
            return null;
        }
        if (command.equals(this.COMMAND)) {
            var email = this.validators.isValid("Введите email: ", IValidator.EMAIL_TYPE, "Email введен не корректно", this.scanner);
            var rawPassword = this.validators.isValid("Введите пароль: ", IValidator.PASSWORD_TYPE, "Пароль слишком короткий", this.scanner);
            if (!this.validators.notNullValue(email, rawPassword)) {
                System.out.println("\nДанные введены не корректно");
                return null;
            }
            var user = this.userService.loginUser(email, rawPassword);
            if (user == null) {
                System.out.println("Логин или пароль не верный");
                return null;
            }
            var roles = this.roleService.getRoles(user.getUserId());
            user.setRoles(roles);
            System.out.println("Авторизация успешна");
            return user;
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
        if (roles.isEmpty()) {
            return this.COMMAND + " - вход в систему";
        } else {
            return null;
        }
    }
}
