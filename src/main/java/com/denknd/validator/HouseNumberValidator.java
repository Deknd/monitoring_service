package com.denknd.validator;

import java.util.regex.Pattern;

/**
 * Валидатор для проверки вводимого номера дома.
 */
public class HouseNumberValidator implements Validator {
  /**
   * Строка для паттерна.
   */
  private static final String HOUSE_NUMBER_PATTERN = "^[\\d]+[a-zA-Z]{0,1}[-/\\w]*$";
  /**
   * Паттерн для валидации строки.
   */
  private static final Pattern pattern = Pattern.compile(HOUSE_NUMBER_PATTERN);

  /**
   * Возвращает тип валидации, к которому будет применен данный валидатор.
   *
   * @return Название типа валидации.
   */
  @Override
  public String nameValidator() {
    return Validator.HOUSE_NUMBER_TYPE;
  }

  /**
   * Проверяет, что строка не null, не пуста и соответствует паттерну для номера дома.
   *
   * @param value Строка для валидации.
   * @return true, если строка соответствует паттерну и не null.
   */
  @Override
  public boolean isValid(String value) {
    if (value == null || value.isEmpty()) {
      return false;
    }
    return pattern.matcher(value).matches();
  }
}
