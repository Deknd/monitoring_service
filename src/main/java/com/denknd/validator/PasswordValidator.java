package com.denknd.validator;

/**
 * Производит валидацию строки, как пароля.
 */
public class PasswordValidator implements Validator {
  /**
   * Минимальная длина пароля.
   */
  private static final int MIN_LENGTH = 2;

  /**
   * Возвращает тип к которому будет применён данный валидатор.
   *
   * @return возвращает тип валидатора
   */
  @Override
  public String nameValidator() {
    return Validator.PASSWORD_TYPE;
  }

  /**
   * Проверяет входящую строку на валидность.
   *
   * @param password строка для проверки
   * @return true если проверка прошла успешно
   */
  @Override
  public boolean isValid(String password) {
    return password != null && password.length() > MIN_LENGTH;
  }
}
