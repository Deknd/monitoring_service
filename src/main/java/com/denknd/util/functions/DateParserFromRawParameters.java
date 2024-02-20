package com.denknd.util.functions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Класс для парсинга года и месяца из параметров.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DateParserFromRawParameters implements Converter<String, YearMonth> {

  /**
   * Извлекает год и месяц из строки параметров запроса.
   *
   * @param source Строка параметра из запроса.
   * @return Объект YearMonth или null, если данные введены не по шаблону: yyyy-MM.
   */
  @Override
  public YearMonth convert(String source) {
    try {
      return YearMonth.parse(source, DateTimeFormatter.ofPattern("yyyy-MM"));
    } catch (DateTimeParseException e) {
      return null;
    }
  }


}
