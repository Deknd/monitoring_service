package com.denknd.util.functions;

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
    var value = "1895-12";

    var apply = this.dateParserFromRawParameters.apply(value);

    assertThat(apply).isNotNull();
    assertThat(apply.getYear()).isEqualTo(1895);
    assertThat(apply.getMonthValue()).isEqualTo(12);

  }

  @Test
  @DisplayName("Проверяет, что сли передать не цифры, вернется null")
  void apply_notNumber() {
    var command = "command";

    var apply = this.dateParserFromRawParameters.apply(command);

    assertThat(apply).isNull();
  }

  @Test
  @DisplayName("Проверяет, что если передать null, вернется null")
  void apply_null() {
    var apply = this.dateParserFromRawParameters.apply(null);

    assertThat(apply).isNull();
  }

}