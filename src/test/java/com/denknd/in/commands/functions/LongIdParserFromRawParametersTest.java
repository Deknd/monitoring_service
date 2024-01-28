package com.denknd.in.commands.functions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LongIdParserFromRawParametersTest {

    private LongIdParserFromRawParameters parserFromRawParameters;
    @BeforeEach
    void setUp() {
        this.parserFromRawParameters = new LongIdParserFromRawParameters();
    }

    @Test
    @DisplayName("Проверят, что парсит из параметров цифру")
    void apply() {
        var command = "command";
        var parameter = "param=";
        var value = "1895";
        var commandAndParam = new String[]{command, parameter + value, "value3", "value4"};

        var apply = this.parserFromRawParameters.apply(commandAndParam, parameter);

        assertThat(apply).isEqualTo(1895L);
    }
    @Test
    @DisplayName("Проверят, что если нет параметра возвращает null")
    void apply_notParam() {
        var command = "command";
        var parameter = "param=";
        var value = "1895";
        var commandAndParam = new String[]{command, value, "value3", "value4"};

        var apply = this.parserFromRawParameters.apply(commandAndParam, parameter);

        assertThat(apply).isNull();
    }

    @Test
    @DisplayName("Проверят, что если с параметров введена не цифра возвращает null")
    void apply_notFailed() {
        var command = "command";
        var parameter = "param=";
        var value = "1sad";
        var commandAndParam = new String[]{command, parameter+value, "value3", "value4"};

        var apply = this.parserFromRawParameters.apply(commandAndParam, parameter);

        assertThat(apply).isNull();
    }
}