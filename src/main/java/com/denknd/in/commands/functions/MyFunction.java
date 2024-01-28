package com.denknd.in.commands.functions;

/**
 * Функциональный интерфейс
 * @param <T> принимаемый тип
 * @param <R> возвращаемый тип
 */
public interface MyFunction<T, R> {

    R apply(T t, String parameter);


}
