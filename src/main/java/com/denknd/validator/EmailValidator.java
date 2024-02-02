package com.denknd.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Валидатор для проверки, является ли строка электронной почтой.
 */
public class EmailValidator implements Validator {

  /**
   * Паттерн для проверки электронной почты.
   */
  private final Pattern EMAIL_PATTERN
          = Pattern.compile(
          "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]"
                  + "+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

  /**
   * Возвращает тип валидации, к которому применяется данный валидатор.
   *
   * @return Название типа валидации.
   */
  @Override
  public String nameValidator() {
    return Validator.EMAIL_TYPE;
  }

  /**
   * Проверяет, является ли переданная строка валидным адресом электронной почты.
   *
   * @param email Строка для проверки.
   * @return true, если строка является валидным адресом электронной почты и не является null.
   */
  @Override
  public boolean isValid(String email) {
    if (email == null) {
      return false;
    }
    Matcher matcher = this.EMAIL_PATTERN.matcher(email);
    return matcher.matches();
  }
}
