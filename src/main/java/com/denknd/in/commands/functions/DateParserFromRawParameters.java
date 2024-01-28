package com.denknd.in.commands.functions;

import lombok.RequiredArgsConstructor;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Парсит из параметров год и месяц
 */
@RequiredArgsConstructor
public class DateParserFromRawParameters extends AbstractMyFunctionParser<String[], YearMonth> {

    /**
     * Парсит из параметров принятых в консоле год и месяц
     * @param commandAndParam массив параметров
     * @param parameter параметр по которому нужно искать
     * @return возвращает объект YearMonth или null, если данные введены не по шаблону: MM-yyyy
     */
    @Override
    public YearMonth apply(String[] commandAndParam, String parameter) {
        var dateYearMonth = this.parserParameters(commandAndParam, parameter);
        if (dateYearMonth == null) {
            return null;
        }
        try {
            return YearMonth.parse(dateYearMonth, DateTimeFormatter.ofPattern("MM-yyyy"));
        } catch (DateTimeParseException e) {
            System.out.println("Дата введена не правильно: "+dateYearMonth+". Паттерн для ввода: MM-yyyy");
            return null;
        }
    }
}
