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

@RequiredArgsConstructor
public class LoginCommand implements ConsoleCommand<User> {
    private final String COMMAND = "login";

    private final UserService userService;
    private final RoleService roleService;
    private final Validators validators;
    private final Scanner scanner;


    @Override
    public String getCommand() {
        return this.COMMAND;
    }

    @Override
    public String getMakesAction() {
        return "Вход в систему";
    }

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

    @Override
    public String getHelpCommand(List<Role> roles) {
        if (roles.isEmpty()) {
            return this.COMMAND + " - вход в систему";
        } else {
            return null;
        }
    }
}
