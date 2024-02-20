package com.denknd.util.functions;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.Function;

/**
 * Класс для парсинга года и месяца из параметров.
 */
@RequiredArgsConstructor
@Slf4j
public class DateParserFromRawParameters implements Function<String, YearMonth> {

  /**
   * Извлекает год и месяц из массива параметров запроса.
   *
   * @param param параметр из запроса
   * @return объект YearMonth или null, если данные введены не по шаблону: yyyy-MM
   */
  @Override
  public YearMonth apply(String param) {
    if (param == null) {
      return null;
    }
    try {
      return YearMonth.parse(param, DateTimeFormatter.ofPattern("yyyy-MM"));
    } catch (DateTimeParseException e) {
      return null;
    }
  }
}
