package com.denknd.validator;

/**
 * Проверяет имя, чтоб имя было валидна.
 */
public class NameValidator implements Validator {
  /**
   * Минимальная длина имени.
   */
  private static final int MIN_LENGTH = 2;

  /**
   * Возвращает тип к которому будет применён данный валидатор.
   *
   * @return тип валидации
   */
  @Override
  public String nameValidator() {
    return Validator.NAME_TYPE;
  }

  /**
   * Проверяет входящую строку на валидность.
   *
   * @param name строка для проверки
   * @return true если проверка прошла успешно
   */
  @Override
  public boolean isValid(String name) {
    return (name != null) && (name.length() >= MIN_LENGTH) && name.matches("[a-zA-Z\\-\\s]+");
  }
}
