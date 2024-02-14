package com.denknd.util.impl;

import com.denknd.exception.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.stream.Collectors;

/**
 * Валидатор для валидации входных данных
 */
public class Validators {
  /**
   * Валидатор полученный из фабрики валидаторов от хибернейта
   */
  private Validator validator;
  /**
   * Фабрика валидаторов
   */
  private ValidatorFactory validatorFactory;

  public Validators(ValidatorFactory validatorFactory) {
    if (validatorFactory != null) {
      this.validatorFactory = validatorFactory;
    }
  }

  /**
   * Возвращает валидатор созданный фабрикой
   *
   * @return валидатор для валидации входных данных
   */
  private Validator getValidator() {
    if (this.validator == null) {
      if (this.validatorFactory == null) {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
      }
      var factory = this.validatorFactory;
      this.validator = factory.getValidator();
    }
    return this.validator;
  }

  /**
   * Функция для валидации объектов, которые помечены ограничительными аннотациями от джакарты
   *
   * @param object объект для валидации
   * @param <T>    тип объекта для валидации
   * @throws ConstraintViolationException ошибка, при не прохождении валидации
   */
  public <T> void validate(T object) throws ConstraintViolationException {
    var validate = getValidator().validate(object);
    if (!validate.isEmpty()) {
      String errorMessage = validate.stream()
              .map(violation -> String.format("%s %s", violation.getPropertyPath(), violation.getMessage()))
              .collect(Collectors.joining("; "));
      throw new ConstraintViolationException(errorMessage);
    }
  }

}
