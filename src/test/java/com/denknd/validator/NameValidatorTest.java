package com.denknd.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NameValidatorTest {

    private NameValidator nameValidator;
    @BeforeEach
    void setUp() {
        this.nameValidator = new NameValidator();
    }

    @Test
    @DisplayName("Проверяет тип валидации")
    void nameValidator() {
        var name = Validator.NAME_TYPE;

        var nameValidator = this.nameValidator.nameValidator();

        assertThat(nameValidator).isEqualTo(name);
    }


        @Test
    @DisplayName("проверяет, что валидное имя возвращает true")
    void isValidation() {
        var name = "Name";

        var validName = this.nameValidator.isValid(name);

        assertThat(validName).isTrue();
    }

    @Test
    @DisplayName("проверяет, что не валидное(короткое) имя возвращает false")
    void isValidation_noValidShort() {
        var name = "N";

        var noValidName = this.nameValidator.isValid(name);

        assertThat(noValidName).isFalse();
    }
    @Test
    @DisplayName("проверяет, что не валидное(используются символы) имя возвращает false")
    void isValidation_noValid() {
        var name = "N%dsfsd";

        var noValidName = this.nameValidator.isValid(name);

        assertThat(noValidName).isFalse();
    }
    @Test
    @DisplayName("проверяет, что имя равное null возвращает false")
    void isValidation_null() {
        var noValidName = this.nameValidator.isValid(null);

        assertThat(noValidName).isFalse();
    }
}