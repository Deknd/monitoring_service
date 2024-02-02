package com.denknd.validator;

/**
 * Валидация заголовков.
 */
public class TitleValidator implements Validator {
  /**
   * Минимальная длина заголовки.
   */
  private static final int MIN_LENGTH = 2;

  /**
   * Возвращает тип к которому будет применён данный валидатор.
   *
   * @return возвращает тип валидатора
   */
  @Override
  public String nameValidator() {
    return Validator.TITLE_TYPE;
  }

  /**
   * Проверяет входящую строку на валидность.
   *
   * @param value строка для проверки
   * @return true если проверка прошла успешно
   */
  @Override
  public boolean isValid(String value) {
    return value != null && value.length() >= MIN_LENGTH;

  }
}
