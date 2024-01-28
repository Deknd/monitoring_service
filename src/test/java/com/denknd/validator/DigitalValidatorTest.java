package com.denknd.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DigitalValidatorTest {

    private DigitalValidator digitalValidator;
    @BeforeEach
    void setUp() {
        this.digitalValidator = new DigitalValidator();
    }

    @Test
    @DisplayName("Проверяет, что ожидаемое имя совпадает")
    void nameValidator() {
        var name =  "numeric";

        var nameValidator = this.digitalValidator.nameValidator();

        assertThat(nameValidator).isEqualTo(name);
    }

    @Test
    @DisplayName("Проверяет, что это цифры")
    void isValidation() {
        var testValues = "2133";

        var validation = this.digitalValidator.isValidation(testValues);

        assertThat(validation).isTrue();
    }
    @Test
    @DisplayName("Проверяет, что это цифры")
    void isValidation_fail() {
        var testValues = "2133asd";

        var validation = this.digitalValidator.isValidation(testValues);

        assertThat(validation).isFalse();
    }
}