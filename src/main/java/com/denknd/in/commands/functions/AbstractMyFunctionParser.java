package com.denknd.in.commands.functions;

import java.util.Arrays;

/**
 * Абстракция для функций парсинга параметров, предоставляет один метод
 * @param <T> входящий тип данных
 * @param <R> исходящий тип данных
 */
public abstract class AbstractMyFunctionParser<T, R> implements MyFunction<T, R> {

    /**
     * Отделяет параметр от данных
     * @param commandAndParam параметры принятые из консоли
     * @param parameter параметр по которому нужно искать данные
     * @return возвращает данные, принятые с параметрами
     */
    protected String parserParameters(String[] commandAndParam, String parameter) {
        return Arrays.stream(commandAndParam)
                .filter(param -> param.contains(parameter))
                .map(param -> param.replace(parameter, ""))
                .findFirst()
                .orElse(null);
    }
}
