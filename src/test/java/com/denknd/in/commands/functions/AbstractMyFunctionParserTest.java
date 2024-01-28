package com.denknd.in.commands.functions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractMyFunctionParserTest {

    private AbstractMyFunctionParser functionParser;

    @BeforeEach
    void setUp() {
        this.functionParser = new AbstractMyFunctionParser() {
            @Override
            public Object apply(Object o, String parameter) {
                return null;
            }
        };
    }

    @Test
    @DisplayName("Проверят, что достает из параметров нужное значение")
    void parserParameters() {
        var command = "command";
        var parameter = "param=";
        var value = "value";
        var commandAndParam = new String[]{command, parameter + value, "value3", "value4"};

        String s = this.functionParser.parserParameters(commandAndParam, parameter);

        assertThat(s).isEqualTo(value);
    }

    @Test
    @DisplayName("Проверят, что достает из параметров нужное значение")
    void parserParameters_notParam() {
        var command = "command";
        var parameter = "param=";
        var value = "value";
        var commandAndParam = new String[]{command, value, "value3", "value4"};

        String s = this.functionParser.parserParameters(commandAndParam, parameter);

        assertThat(s).isNull();
    }
}