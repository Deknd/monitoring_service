package com.denknd.validator;

/**
 * Интерфейс для управления и использования валидаторами данных.
 */
public interface DataValidatorManager {
  /**
   * Получает проверенные данные после ввода пользователя.
   *
   * @param prompt        подсказка пользователю о необходимых данных
   * @param validatorType тип валидатора, который следует использовать
   * @param errorMessage  сообщение, которое будет отображаться при ошибке ввода
   * @return валидная строка, полученная от пользователя
   */
  String getValidInput(String prompt, String validatorType, String errorMessage);

  /**
   * Добавляет валидаторы для использования.
   *
   * @param validators массив валидаторов
   */
  void addValidators(Validator... validators);

  /**
   * Проверяет строки на отсутствие значения null.
   *
   * @param values массив строк
   * @return true, если ни одна строка из массива не имеет значения null
   */
  boolean areAllValuesNotNullAndNotEmpty(String... values);
}
