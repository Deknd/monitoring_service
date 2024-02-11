package com.denknd.in.commands;

import com.denknd.controllers.UserController;
import com.denknd.dto.UserCreateDto;
import com.denknd.entity.Roles;
import com.denknd.exception.InvalidUserDataException;
import com.denknd.exception.UserAlreadyExistsException;
import com.denknd.security.UserSecurity;
import com.denknd.validator.DataValidatorManager;
import com.denknd.validator.Validator;
import lombok.RequiredArgsConstructor;

import java.security.NoSuchAlgorithmException;

/**
 * Класс представляющий команду консоли для регистрации нового пользователя.
 */
@RequiredArgsConstructor
public class SignupCommand implements ConsoleCommand {
  /**
   * Контролер для работы с пользователями.
   */
  private final UserController userController;
  /**
   * Валидатор принятых данных.
   */
  private final DataValidatorManager dataValidatorManager;


  /**
   * Команда, которая отвечает за работу этого класса.
   */
  private final String COMMAND_NAME = "signup";

  /**
   * Возвращает команду, которая запускает работу метода run.
   *
   * @return команда для работы класса
   */
  @Override
  public String getCommand() {
    return this.COMMAND_NAME;
  }

  /**
   * Возвращает пояснение работы класса.
   *
   * @return пояснение, что делает класс, для аудита
   */
  @Override
  public String getAuditActionDescription() {
    return "Регистрация пользователя";
  }

  /**
   * Основной метод класса.
   *
   * @param command    команда полученная из консоли
   * @param userActive активный юзер
   * @return возвращает сообщение об результате работы
   */
  @Override
  public String run(String command, UserSecurity userActive) {
    if (userActive == null) {

      if (command.equals(this.COMMAND_NAME)) {
        var email = this.dataValidatorManager.getValidInput(
                "Введите email: ",
                Validator.EMAIL_TYPE,
                "Email веден не верно, повторите");
        var password = this.dataValidatorManager.areAllValuesNotNullAndNotEmpty(email)
                ? this.dataValidatorManager.getValidInput(
                "Введите пароль: ",
                Validator.PASSWORD_TYPE,
                "Пароль слишком короткий, введите новый")
                : null;
        var lastName = this.dataValidatorManager.areAllValuesNotNullAndNotEmpty(email, password)
                ? this.dataValidatorManager.getValidInput(
                "Введите Вашу фамилию: ",
                Validator.NAME_TYPE,
                "Фамилия введена не верна. Фамилия должна содержать только буквы")
                : null;
        var name = this.dataValidatorManager.areAllValuesNotNullAndNotEmpty(email, password, lastName)
                ? this.dataValidatorManager.getValidInput(
                "Введите Ваше имя: ",
                Validator.NAME_TYPE,
                "Имя введено не верно. Имя должно содержать только буквы")
                : null;

        if (!this.dataValidatorManager.areAllValuesNotNullAndNotEmpty(email, password, lastName, name)) {
          return "Пользователь не создан, все поля должны быть заполнены";
        }
        var newUser = UserCreateDto.builder()
                .email(email)
                .password(password)
                .lastName(lastName)
                .firstName(name).build();
        try {
          var user = this.userController.createUser(newUser);
          return "Пользователь создан " + user;
        } catch (UserAlreadyExistsException | NoSuchAlgorithmException | InvalidUserDataException e) {
          return e.getMessage();
        }
      }
    }
    return "Команда не поддерживается: " + command;
  }

  /**
   * Подсказка для команды help.
   *
   * @param role роль доступная пользователю
   * @return возвращает сообщение с подсказкой по работе с данной командой
   */
  @Override
  public String getHelpCommand(Roles role) {
    if (role == null) {
      return this.COMMAND_NAME + " - регистрация пользователя";
    } else {
      return null;
    }
  }
}
