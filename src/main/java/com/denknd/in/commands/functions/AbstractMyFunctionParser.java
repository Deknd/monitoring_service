package com.denknd.in.commands.functions;

import java.util.Arrays;

public abstract class AbstractMyFunctionParser<T, R> implements MyFunction<T, R> {

    protected String parserParameters(String[] commandAndParam, String parameter) {
        return Arrays.stream(commandAndParam)
                .filter(param -> param.contains(parameter))
                .map(param -> param.replace(parameter, ""))
                .findFirst()
                .orElse(null);
    }
}
