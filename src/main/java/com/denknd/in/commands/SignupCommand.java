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

@RequiredArgsConstructor
public class SignupCommand implements ConsoleCommand<String> {

    private final UserService userService;
    private final RoleService roleService;

    private final Validators validators;
    private final Scanner scanner;



    private final String COMMAND = "signup";

    @Override
    public String getCommand() {
        return this.COMMAND;
    }

    @Override
    public String getMakesAction() {
        return "Регистрация пользователя";
    }

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

    @Override
    public String getHelpCommand(List<Role> roles) {
        if (roles.isEmpty()) {
            return this.COMMAND + " - регистрация пользователя";
        } else {
            return null;
        }
    }
}
