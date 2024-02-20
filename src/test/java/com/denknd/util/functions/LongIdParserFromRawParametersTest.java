package com.denknd.util.functions;

import com.denknd.util.functions.LongIdParserFromRawParameters;
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
    var value = "1895";

    var apply = this.parserFromRawParameters.apply(value);

    assertThat(apply).isEqualTo(1895L);
  }

  @Test
  @DisplayName("Проверят, что если передать не цифры, вернется null")
  void apply_notNumber() {
    var command = "command";

    var apply = this.parserFromRawParameters.apply(command);

    assertThat(apply).isNull();
  }

  @Test
  @DisplayName("Проверят, что если передать null, то вернется null")
  void apply_notFailed() {
    var apply = this.parserFromRawParameters.apply(null);

    assertThat(apply).isNull();
  }
}