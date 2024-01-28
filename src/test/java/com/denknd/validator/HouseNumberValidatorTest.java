package com.denknd.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class HouseNumberValidatorTest {
    private HouseNumberValidator validator;

    @BeforeEach
    void setUp() {
        this.validator = new HouseNumberValidator();
    }

    @Test
    @DisplayName("проверяет, что валидатор выдает ожидаемое имя")
    void nameValidator() {
        var name = IValidator.HOUSE_NUMBER_TYPE;

        var nameValidator = this.validator.nameValidator();

        assertThat(nameValidator).isEqualTo(name);
    }

    @Test
    @DisplayName("Проверяет, что цифра возвращает тру")
    void isValidation() {
        var num = "3";

        var validation = this.validator.isValidation(num);

        assertThat(validation).isTrue();

    }
    @Test
    @DisplayName("Проверяет, что цифра, тире и буква возвращают тру")
    void isValidation_charAndDash() {
        var num = "3-d";

        var validation = this.validator.isValidation(num);

        assertThat(validation).isTrue();

    }
    @Test
    @DisplayName("Проверяет, что цифра, дробь и буква возвращают тру")
    void isValidation_fraction() {
        var num = "3/3";

        var validation = this.validator.isValidation(num);

        assertThat(validation).isTrue();

    }

    @Test
    @DisplayName("Проверяет, что цифра и буква возвращают тру")
    void isValidation_char() {
        var num = "2s";

        var validation = this.validator.isValidation(num);

        assertThat(validation).isTrue();

    }
  @Test
  @DisplayName("Проверяет, буква возвращают false")
  void isValidation_failedChar() {
        var num = "sssddsd";

        var validation = this.validator.isValidation(num);

        assertThat(validation).isFalse();

    }
    @Test
    @DisplayName("Проверяет, что пустая строка возвращают false")
    void isValidation_failedEmpty() {
        var num = "";

        var validation = this.validator.isValidation(num);

        assertThat(validation).isFalse();

    }
    @Test
    @DisplayName("Проверяет, что null возвращают false")
    void isValidation_failedNull() {

        var validation = this.validator.isValidation(null);

        assertThat(validation).isFalse();

    }
}