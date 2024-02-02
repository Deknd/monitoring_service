package com.denknd.validator;

import java.util.regex.Pattern;

/**
 * Проверяет, что строка является почтовым индексом.
 */
public class PostalCodeValidator implements Validator {

  /**
   * Потерн, для валидации строки.
   */
  private static final Pattern pattern = Pattern.compile("^\\d{6}$");

  /**
   * Возвращает тип к которому будет применён данный валидатор.
   *
   * @return возвращает тип валидатора
   */
  @Override
  public String nameValidator() {
    return Validator.POSTAL_CODE_TYPE;
  }

  /**
   * Проверяет входящую строку на валидность.
   *
   * @param value строка для проверки
   * @return true если проверка прошла успешно
   */
  @Override
  public boolean isValid(String value) {
    return value != null && pattern.matcher(value).matches();
  }
}
