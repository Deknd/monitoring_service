package com.denknd.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DoubleDigitalValidatorTest {

    private DoubleDigitalValidator doubleDigitalValidator;
    @BeforeEach
    void setUp() {
        this.doubleDigitalValidator = new DoubleDigitalValidator();
    }

    @Test
    @DisplayName("Проверяет, что это цифры")
    void nameValidator() {
        var name =  "double";

        var nameValidator = this.doubleDigitalValidator.nameValidator();

        assertThat(nameValidator).isEqualTo(name);
    }

    @Test
    @DisplayName("Проверяет валидацию числа с точкой")
    void isValidation() {
        var dou = "123123.1233";

        var validation = this.doubleDigitalValidator.isValidation(dou);

        assertThat(validation).isTrue();
    }
    @Test
    @DisplayName("Проверяет валидацию числа")
    void isValidation_noDouble() {
        var dou = "123123";

        var validation = this.doubleDigitalValidator.isValidation(dou);

        assertThat(validation).isTrue();
    }
    @Test
    @DisplayName("Проверяет валидацию не числа")
    void isValidation_noNumeric() {
        var dou = "1231asf23";

        var validation = this.doubleDigitalValidator.isValidation(dou);

        assertThat(validation).isFalse();
    }
}