package com.denknd.in.commands.functions;

public interface MyFunction<T, R> {

    R apply(T t, String parameter);


}
