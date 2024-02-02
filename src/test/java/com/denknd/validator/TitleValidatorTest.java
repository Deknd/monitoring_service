package com.denknd.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TitleValidatorTest {

    private TitleValidator titleValidator;
    @BeforeEach
    void setUp() {
        this.titleValidator = new TitleValidator();
    }

    @Test
    @DisplayName("Проверяет, что возвращает ожидаемое имя")
    void nameValidator() {
        var name = Validator.TITLE_TYPE;

        var nameValidator = this.titleValidator.nameValidator();

        assertThat(nameValidator).isEqualTo(name);
    }

    @Test
    @DisplayName("Проверяет, что название обычное название валидно")
    void isValidation() {
        var title = "title";

        var validation = this.titleValidator.isValid(title);

        assertThat(validation).isTrue();
    }

    @Test
    @DisplayName("Проверяет, что короткое название не валидно")
    void isValidation_short() {
        var title = "t";

        var validation = this.titleValidator.isValid(title);

        assertThat(validation).isFalse();
    }
    @Test
    @DisplayName("Проверяет, что null не валиден")
    void isValidation_null() {
        var validation = this.titleValidator.isValid(null);

        assertThat(validation).isFalse();
    }
}