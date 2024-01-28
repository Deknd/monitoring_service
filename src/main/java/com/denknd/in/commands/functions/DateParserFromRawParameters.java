package com.denknd.in.commands.functions;

import lombok.RequiredArgsConstructor;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RequiredArgsConstructor
public class DateParserFromRawParameters extends AbstractMyFunctionParser<String[], YearMonth> {


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
