package com.denknd.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RegionValidatorTest {

    private RegionValidator regionValidator;
    @BeforeEach
    void setUp() {
        this.regionValidator = new RegionValidator();
    }

    @Test
    @DisplayName("Проверяет, что имя рано ожидаемому")
    void nameValidator() {
        var name = IValidator.REGION_TYPE;

        var nameValidator = this.regionValidator.nameValidator();

        assertThat(nameValidator).isEqualTo(name);
    }

    @Test
    @DisplayName("Проверяет, что регион соответствует название регионов в россии")
    void isValidation() {
        var name = "Москва";

        var validation = this.regionValidator.isValidation(name);

        assertThat(validation).isTrue();
    }
    @Test
    @DisplayName("Проверяет, что если введен не регион, то возвращает false")
    void isValidation_failedRegion() {
        var name = "Москвasdа";

        var validation = this.regionValidator.isValidation(name);

        assertThat(validation).isFalse();
    }
    @Test
    @DisplayName("Проверяет, что если введен null, то возвращает false")
    void isValidation_null() {

        var validation = this.regionValidator.isValidation(null);

        assertThat(validation).isFalse();
    }
}