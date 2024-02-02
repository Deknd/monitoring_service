package com.denknd.in.commands.functions;

import lombok.RequiredArgsConstructor;

/**
 * Класс для парсинга параметров и извлечения из них числовых значений.
 */
@RequiredArgsConstructor
public class LongIdParserFromRawParameters extends AbstractMyFunctionParser<String[], Long> {

  /**
   * Извлекает числовое значение из параметров по указанному ключевому параметру.
   *
   * @param commandAndParam массив параметров
   * @param idParameter     ключевой параметр для поиска
   * @return возвращает Long или null, если введено не числовое значение
   */
  @Override
  public Long apply(String[] commandAndParam, String idParameter) {
    var idString = this.extractDataFromParameters(commandAndParam, idParameter);
    if (idString == null) {
      return null;
    }
    try {
      return Long.parseLong(idString);
    } catch (NumberFormatException e) {
      System.out.println("Id введен не верно: " + idString);
      return null;
    }
  }
}
