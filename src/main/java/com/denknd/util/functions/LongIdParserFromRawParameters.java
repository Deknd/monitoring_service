package com.denknd.util.functions;

import lombok.RequiredArgsConstructor;

import java.util.function.Function;

/**
 * Класс для парсинга параметров и извлечения из них числовых значений.
 */
@RequiredArgsConstructor
public class LongIdParserFromRawParameters implements Function<String, Long> {

  /**
   * Извлекает числовое значение из параметров.
   *
   * @param param     параметр из запроса
   * @return возвращает Long или null, если введено не числовое значение
   */
  @Override
  public Long apply(String param) {
    if (param == null) {
      return null;
    }
    try {
      return Long.parseLong(param);
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
