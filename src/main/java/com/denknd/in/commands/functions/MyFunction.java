package com.denknd.in.commands.functions;

/**
 * Функциональный интерфейс для применения функции к входному объекту с учетом дополнительного параметра.
 *
 * @param <T> тип входного объекта
 * @param <R> тип возвращаемого результата
 */
public interface MyFunction<T, R> {
  /**
   * Применяет функцию к входному объекту с учетом дополнительного параметра.
   *
   * @param input     входной объект
   * @param parameter дополнительный параметр
   * @return результат применения функции
   */
  R apply(T input, String parameter);


}
