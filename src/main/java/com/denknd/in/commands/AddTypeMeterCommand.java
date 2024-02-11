package com.denknd.in.commands;

import com.denknd.controllers.TypeMeterController;
import com.denknd.dto.TypeMeterDto;
import com.denknd.entity.Roles;
import com.denknd.exception.TypeMeterAdditionException;
import com.denknd.security.UserSecurity;
import com.denknd.validator.DataValidatorManager;
import com.denknd.validator.Validator;
import lombok.RequiredArgsConstructor;

/**
 * Класс представляющий команду консоли, при помощи которой подаются показания счетчиков.
 */
@RequiredArgsConstructor
public class AddTypeMeterCommand implements ConsoleCommand {
  /**
   * Команда, которая отвечает за работу этого класса.
   */
  private final String COMMAND_NAME = "add_type";
  /**
   * Валидатор принятых данных.
   */
  private final DataValidatorManager dataValidatorManager;

  /**
   * Контроллер для управления типами показаний
   */
  private final TypeMeterController typeMeterController;

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
    return "Добавляет новый тип показаний в БД";
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
      return null;
    }
    if (command.contains(this.COMMAND_NAME) && userActive.role().equals(Roles.ADMIN)) {
      var typeCode = this.dataValidatorManager.getValidInput(
              "Введите код консоли для нового типа: ",
              Validator.TITLE_TYPE,
              "Код обязателен, служит для добавления данных");
      var description = this.dataValidatorManager.getValidInput(
              "Введите описание кратко(будет видно при вызове подсказки): ",
              Validator.TITLE_TYPE,
              "Описание является обязательным"
      );
      var metric = this.dataValidatorManager.getValidInput(
              "Введите единицу измерения данного типа: ",
              Validator.TITLE_TYPE,
              "Единица измерения обязательна"
      );

      if (!this.dataValidatorManager.areAllValuesNotNullAndNotEmpty(typeCode, description, metric)) {
        return "Не введены обязательные поля";
      }
      var typeMeter = TypeMeterDto.builder()
              .typeCode(typeCode)
              .typeDescription(description)
              .metric(metric)
              .build();
      try {
        var newTypeMeter = this.typeMeterController.addNewType(typeMeter);
        return "Новый тип показаний добавлен: "
                + newTypeMeter.typeCode()
                + " - " + newTypeMeter.typeDescription();
      } catch (TypeMeterAdditionException e) {
        return "Данные введены не корректно. "+e.getMessage();
      }
    }
    return null;
  }

  /**
   * Подсказка для команды help.
   *
   * @param role роли доступные пользователю
   * @return возвращает сообщение с подсказкой по работе с данной командой
   */
  @Override
  public String getHelpCommand(Roles role) {
    if (role == null) {
      return null;
    }
    if (role.equals(Roles.ADMIN)) {
      return this.COMMAND_NAME + " служит для добавления новых типов показаний";
    }
    return null;
  }
}
