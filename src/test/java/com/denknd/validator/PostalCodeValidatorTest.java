package com.denknd.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PostalCodeValidatorTest {
    private PostalCodeValidator postalCodeValidator;

    @BeforeEach
    void setUp() {
        this.postalCodeValidator= new PostalCodeValidator();
    }

    @Test
    @DisplayName("Проверяет, что имя ожидаемое")
    void nameValidator() {
        var name = IValidator.POSTAL_CODE_TYPE;

        var nameValidator = this.postalCodeValidator.nameValidator();

        assertThat(nameValidator).isEqualTo(name);
    }

    @Test
    @DisplayName("Проверяет, что при цифр, валидация успешна")
    void isValidation() {
        var postCod = "123456";

        var validation = this.postalCodeValidator.isValidation(postCod);

        assertThat(validation).isTrue();
    }
    @Test
    @DisplayName("Проверяет, что при меньшем количестве цифр, валидация не успешна")
    void isValidation_fewerNumbers() {
        var postCod = "12345";

        var validation = this.postalCodeValidator.isValidation(postCod);

        assertThat(validation).isFalse();
    }
    @Test
    @DisplayName("Проверяет, что при с буквой, валидация не успешна")
    void isValidation_char() {
        var postCod = "12345в";

        var validation = this.postalCodeValidator.isValidation(postCod);

        assertThat(validation).isFalse();
    }
    @Test
    @DisplayName("Проверяет, что при с большим количеством цифр, валидация не успешна")
    void isValidation_moreNumbers() {
        var postCod = "1234523";

        var validation = this.postalCodeValidator.isValidation(postCod);

        assertThat(validation).isFalse();
    }
    @Test
    @DisplayName("Проверяет, что при отправки null, валидация не успешна")
    void isValidation_null() {


        var validation = this.postalCodeValidator.isValidation(null);

        assertThat(validation).isFalse();
    }
}