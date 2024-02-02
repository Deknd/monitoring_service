package com.denknd.validator;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Реализация класса управления валидаторами данных.
 */
@RequiredArgsConstructor
public class DataValidatorManagerImpl implements DataValidatorManager {
  /**
   * Хранит списки валидаторов по их типам.
   */
  private final Map<String, List<Validator>> validators = new HashMap<>();
  private final Scanner scanner;

  /**
   * Добавляет валидаторы для использования.
   *
   * @param validators массив валидаторов
   */
  @Override
  public void addValidators(Validator... validators) {
    for (Validator validator : validators) {
      var validatorType = validator.nameValidator();
      this.validators.computeIfAbsent(validatorType, key -> new ArrayList<>()).add(validator);
    }

  }

  /**
   * Получает данные от пользователя и валидирует их. Если ввод неверен три раза, возвращает null.
   *
   * @param prompt        подсказка пользователю о необходимых данных
   * @param validatorType тип валидатора, который следует использовать
   * @param errorMessage  сообщение, отображаемое при ошибке ввода
   * @return валидная строка
   */
  @Override
  public String getValidInput(String prompt, String validatorType, String errorMessage) {
    String value;
    var valid = true;
    var attempts = 0L;
    do {
      attempts++;
      if (attempts > 3) {
        return null;
      }
      System.out.print(prompt);
      value = this.scanner.nextLine();
      if (this.validators.containsKey(validatorType)) {
        final var currentValue = value;
        var iValidators = this.validators.get(validatorType);
        valid = iValidators.stream().allMatch(iValidator -> iValidator.isValid(currentValue));
      }
      if (!valid) {
        System.out.println(errorMessage);
      }
    } while (!valid);
    return value;
  }

  /**
   * Проверяет массив строк на отсутствие значения null и пустоту.
   *
   * @param values массив строк
   * @return true, если все строки не null и не пусты
   */
  @Override
  public boolean areAllValuesNotNullAndNotEmpty(String... values) {
    return Arrays.stream(values).noneMatch(value -> value == null || value.isEmpty());

  }
}
