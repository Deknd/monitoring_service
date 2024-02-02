package com.denknd.in.commands.functions;

import java.util.Arrays;

/**
 * Абстракция для функций парсинга параметров, предоставляет один метод.
 *
 * @param <T> входящий тип данных
 * @param <R> исходящий тип данных
 */
public abstract class AbstractMyFunctionParser<T, R> implements MyFunction<T, R> {

  /**
   * Извлекает данные из параметров команды.
   *
   * @param commandAndParam массив параметров, полученных из консоли
   * @param parameter       ключевой параметр, по которому нужно извлечь данные
   * @return данные, соответствующие указанному параметру, или null, если параметр не найден
   */
  protected String extractDataFromParameters(String[] commandAndParam, String parameter) {
    return Arrays.stream(commandAndParam)
            .filter(param -> param.contains(parameter))
            .map(param -> param.replace(parameter, ""))
            .findFirst()
            .orElse(null);
  }
}
