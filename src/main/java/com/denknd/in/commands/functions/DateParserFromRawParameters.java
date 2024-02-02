package com.denknd.in.commands.functions;

import lombok.RequiredArgsConstructor;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Класс для парсинга года и месяца из параметров.
 */
@RequiredArgsConstructor
public class DateParserFromRawParameters extends AbstractMyFunctionParser<String[], YearMonth> {

  /**
   * Извлекает год и месяц из массива параметров, полученных из консоли.
   *
   * @param commandAndParam массив параметров
   * @param parameter       ключевой параметр для поиска
   * @return объект YearMonth или null, если данные введены не по шаблону: MM-yyyy
   */
  @Override
  public YearMonth apply(String[] commandAndParam, String parameter) {
    var dateYearMonth = this.extractDataFromParameters(commandAndParam, parameter);
    if (dateYearMonth == null) {
      return null;
    }
    try {
      return YearMonth.parse(dateYearMonth, DateTimeFormatter.ofPattern("MM-yyyy"));
    } catch (DateTimeParseException e) {
      System.out.println("Дата введена не правильно: "
              + dateYearMonth + ". Паттерн для ввода: MM-yyyy");
      return null;
    }
  }
}
