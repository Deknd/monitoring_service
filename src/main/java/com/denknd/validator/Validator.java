package com.denknd.validator;

/**
 * Интерфейс для валидаторов, взаимодействующих с классом {@link DataValidatorManager}.
 */
public interface Validator {
  /**
   * Тип валидатора для проверки email.
   */
  String EMAIL_TYPE = "email";
  /**
   * Тип валидатора для проверки имен.
   */
  String NAME_TYPE = "firstName";
  /**
   * Тип валидатора для проверки паролей.
   */
  String PASSWORD_TYPE = "password";
  /**
   * Тип валидатора для проверки регионов.
   */
  String REGION_TYPE = "region";
  /**
   * Тип валидатора для проверки заголовков.
   */
  String TITLE_TYPE = "title";
  /**
   * Тип валидатора для проверки номера дома.
   */
  String HOUSE_NUMBER_TYPE = "house_number";
  /**
   * Тип валидатора для проверки почтового индекса.
   */
  String POSTAL_CODE_TYPE = "postal_code";
  /**
   * Тип валидатора для проверки числовых значений.
   */
  String DIGITAL_TYPE = "numeric";
  /**
   * Тип валидатора для проверки чисел с плавающей точкой.
   */
  String DOUBLE_TYPE = "double";

  /**
   * Возвращает тип, к которому применяется данный валидатор.
   *
   * @return тип валидатора
   */
  String nameValidator();

  /**
   * Проверяет входную строку и возвращает true, если проверка успешна.
   *
   * @param value строка для проверки
   * @return true, если проверка успешна
   */
  boolean isValid(String value);
}
