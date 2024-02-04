package com.denknd.config;

import com.denknd.repository.TestContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.assertj.core.api.Assertions.*;


class ManualConfigTest extends TestContainer {

    @Test
    @DisplayName("Проверят, что конфигурация не вызывает ошибки")
    void initialisation() {

        assertThatCode(() -> new ManualConfig("src/test/resources/application-testConfig.yaml", postgresContainer.getDataBaseConnection())).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Проверяет, что конфигурация возвращает полный объект")
    void console() throws FileNotFoundException, ClassNotFoundException {
        var context = new ManualConfig("src/test/resources/application-testConfig.yaml", postgresContainer.getDataBaseConnection());

        var console = context.console();

        assertThat(console).isNotNull();
    }
}