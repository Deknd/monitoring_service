package com.denknd.in.commands;

import com.denknd.entity.Role;
import com.denknd.entity.User;
import com.denknd.exception.UserAlreadyExistsException;
import com.denknd.services.RoleService;
import com.denknd.services.UserService;
import com.denknd.validator.IValidator;
import com.denknd.validator.Validators;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Scanner;

/**
 * Класс представляющий команду консоли, при помощи которой регистрируется новый пользователь
 */
@RequiredArgsConstructor
public class SignupCommand implements ConsoleCommand<String> {
    /**
     * Сервис для работы с пользователями
     */
    private final UserService userService;
    /**
     * Сервис для работы с ролями пользователя
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
     * Команда, которая отвечает за работу этого класса
     */
    private final String COMMAND = "signup";
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
        return "Регистрация пользователя";
    }
    /**
     * Основной метод класса
     * @param command команда полученная из консоли
     * @param userActive активный юзер
     * @return возвращает сообщение об результате работы
     */
    @Override
    public String run(String command, User userActive) {
        if (userActive == null) {

            if (command.equals(this.COMMAND)) {
                var email = this.validators.isValid(
                        "Введите email: ",
                        IValidator.EMAIL_TYPE,
                        "Email веден не верно, повторите",
                        this.scanner);
                var password = this.validators.notNullValue(email) ?
                        this.validators.isValid(
                                "Введите пароль: ",
                                IValidator.PASSWORD_TYPE,
                                "Пароль слишком короткий, введите новый",
                                this.scanner) :
                        null;
                var lastName = this.validators.notNullValue(email, password) ?
                        this.validators.isValid(
                                "Введите Вашу фамилию: ",
                                IValidator.NAME_TYPE,
                                "Фамилия введена не верна. Фамилия должна содержать только буквы",
                                this.scanner) :
                        null;
                var name = this.validators.notNullValue(email, password, lastName) ?
                        this.validators.isValid(
                                "Введите Ваше имя: ",
                                IValidator.NAME_TYPE,
                                "Имя введено не верно. Имя должно содержать только буквы",
                                this.scanner) :
                        null;

                if (!this.validators.notNullValue(email, password, lastName, name)) {
                    return "Пользователь не создан, все поля должны быть заполнены";
                }
                var newUser = User.builder()
                        .email(email)
                        .password(password)
                        .lastName(lastName)
                        .firstName(name).build();
                try {
                    var user = this.userService.registrationUser(newUser);
                    this.roleService.addRoles(user.getUserId(), Role.builder().roleName("USER").build());

                    return "Пользователь создан";
                } catch (UserAlreadyExistsException e) {
                    return e.getMessage();
                }
            }
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
        if (roles.isEmpty()) {
            return this.COMMAND + " - регистрация пользователя";
        } else {
            return null;
        }
    }
}
