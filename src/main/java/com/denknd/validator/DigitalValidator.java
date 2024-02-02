package com.denknd.validator;

/**
 * Валидатор, проверяющий, что строка является числом.
 */
public class DigitalValidator implements Validator {
  /**
   * Возвращает тип валидации, к которому применяется данный валидатор.
   *
   * @return Название типа валидации.
   */
  @Override
  public String nameValidator() {
    return Validator.DIGITAL_TYPE;
  }

  /**
   * Проверяет, является ли переданная строка числом.
   *
   * @param value Строка для проверки.
   * @return true, если строка является числом.
   */
  @Override
  public boolean isValid(String value) {
    return value.matches("\\d+");
  }
}
