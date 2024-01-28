package com.denknd.in.commands.functions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DateParserFromRawParametersTest {

    private DateParserFromRawParameters dateParserFromRawParameters;
    @BeforeEach
    void setUp() {
        this.dateParserFromRawParameters = new DateParserFromRawParameters();
    }

    @Test
    @DisplayName("Парсит параметры и достает дату из них")
    void apply() {
        var command = "command";
        var parameter = "param=";
        var value = "12-1895";
        var commandAndParam = new String[]{command, parameter + value, "value3", "value4"};

        var apply = this.dateParserFromRawParameters.apply(commandAndParam, parameter);

        assertThat(apply).isNotNull();
        assertThat(apply.getYear()).isEqualTo(1895);
        assertThat(apply.getMonthValue()).isEqualTo(12);

    }
    @Test
    @DisplayName("Проверяет, что без параметра возвращает null")
    void apply_notParam() {
        var command = "command";
        var parameter = "param=";
        var value = "12-1895";
        var commandAndParam = new String[]{command, value, "value3", "value4"};

        var apply = this.dateParserFromRawParameters.apply(commandAndParam, parameter);

        assertThat(apply).isNull();
    }

    @Test
    @DisplayName("Проверяет, что c параметром и датой не в формате MM-yyyy возвращает null")
    void apply_notFormatter() {
        var command = "command";
        var parameter = "param=";
        var value = "1895-12";
        var commandAndParam = new String[]{command, parameter + value, "value3", "value4"};

        var apply = this.dateParserFromRawParameters.apply(commandAndParam, parameter);

        assertThat(apply).isNull();
    }
    @Test
    @DisplayName("Проверяет, что c параметром и датой не в формате MM-yyyy возвращает null")
    void apply_notDate() {
        var command = "command";
        var parameter = "param=";
        var value = "asfsf";
        var commandAndParam = new String[]{command, parameter + value, "value3", "value4"};

        var apply = this.dateParserFromRawParameters.apply(commandAndParam, parameter);

        assertThat(apply).isNull();
    }
}