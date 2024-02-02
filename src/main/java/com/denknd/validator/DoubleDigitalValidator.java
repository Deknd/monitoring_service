package com.denknd.validator;

/**
 * Валидатор, проверяющий, что строка является числом или числом с дробью.
 */
public class DoubleDigitalValidator implements Validator {
  /**
   * Возвращает тип валидации, к которому применяется данный валидатор.
   *
   * @return Название типа валидации.
   */
  @Override
  public String nameValidator() {
    return Validator.DOUBLE_TYPE;
  }

  /**
   * Проверяет, является ли переданная строка числом или числом с дробью.
   *
   * @param value Строка для проверки.
   * @return true, если строка является числом или числом с дробью.
   */
  @Override
  public boolean isValid(String value) {
    return value.matches("\\d+(\\.\\d+)?");

  }
}
