package com.denknd.in.commands;

import com.denknd.controllers.AddressController;
import com.denknd.dto.AddressDto;
import com.denknd.entity.Roles;
import com.denknd.exception.AddressDatabaseException;
import com.denknd.security.UserSecurity;
import com.denknd.validator.DataValidatorManager;
import com.denknd.validator.Validator;
import lombok.RequiredArgsConstructor;

/**
 * Команда консоли для добавления нового адреса.
 */
@RequiredArgsConstructor
public class AddAddressCommand implements ConsoleCommand {
  /**
   * Команда, которая отвечает за работу этого класса.
   */
  private final String COMMAND_NAME = "add_addr";
  /**
   * Контролер для работы с адресами
   */
  private final AddressController addressController;
  /**
   * Валидатор принятых данных.
   */
  private final DataValidatorManager dataValidatorManager;


  /**
   * Возвращает название команды.
   *
   * @return название команды
   */
  @Override
  public String getCommand() {
    return this.COMMAND_NAME;
  }

  /**
   * Возвращает пояснение к работе команды.
   *
   * @return пояснение к работе команды
   */
  @Override
  public String getAuditActionDescription() {
    return "Добавляет адрес к пользователю";
  }

  /**
   * Основной метод выполнения команды.
   *
   * @param command    команда из консоли
   * @param userActive активный пользователь
   * @return результат выполнения команды
   */
  @Override
  public String run(String command, UserSecurity userActive) {
    if (userActive == null) {
      return null;
    }
    if (command.equals(this.COMMAND_NAME) && userActive.role().equals(Roles.USER)) {
      var region = this.dataValidatorManager.getValidInput(
              "Введите ваш регион: ",
              Validator.REGION_TYPE,
              "Данного региона не найдено");
      var city = this.dataValidatorManager.getValidInput(
              "Введите ваш город: ",
              Validator.TITLE_TYPE,
              "Название города введено некорректно");
      var street = this.dataValidatorManager.getValidInput(
              "Введите улицу: ",
              Validator.TITLE_TYPE,
              "Название улицы введено некорректно");
      var house = this.dataValidatorManager.getValidInput(
              "Введите номер дома: ",
              Validator.HOUSE_NUMBER_TYPE,
              "Номер дома введен некорректно");
      var apartment = this.dataValidatorManager.getValidInput(
              "Введите норме квартиры(если номера квартиры нажмите Enter): ",
              "",
              "");
      var postalCodeString = this.dataValidatorManager.getValidInput(
              "Введите ваш почтовый индекс: ",
              Validator.POSTAL_CODE_TYPE,
              "Почтовый индекс должен состоять из 6 цифр");


      if (!this.dataValidatorManager.areAllValuesNotNullAndNotEmpty(region, city, street, house, postalCodeString)) {
        return "Данные введены некорректно";
      }
      var postalCode = Long.parseLong(postalCodeString);

      var address = AddressDto.builder()
              .region(region)
              .city(city)
              .street(street)
              .house(house)
              .apartment(apartment)
              .postalCode(postalCode).build();

      try {
        var addressByUser = this.addressController.addAddress(address, userActive.userId());
        return "Адрес добавлен (ID адреса: " + addressByUser + ")";
      } catch (AddressDatabaseException e) {
        return "Данные введены некорректно. " + e.getMessage();
      }


    } else {
      return "Команда не поддерживается: " + command;
    }
  }

  /**
   * Возвращает подсказку по использованию команды для команды help.
   *
   * @param role роль доступная пользователю
   * @return подсказка по использованию команды
   */
  @Override
  public String getHelpCommand(Roles role) {
    if (role == null) {
      return null;
    }
    if (role.equals(Roles.USER)) {
      return this.COMMAND_NAME + " - используется для добавления адреса подачи показаний";
    }
    return null;
  }
}
