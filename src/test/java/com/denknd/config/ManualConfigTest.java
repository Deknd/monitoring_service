package com.denknd.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;


class ManualConfigTest {

    @Test
    @DisplayName("Проверят, что конфигурация не вызывает ошибки")
    void initialisation() {

        assertThatCode(ManualConfig::new).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Проверяет, что конфигурация возвращает полный объект")
    void console(){
        var context = new ManualConfig();

        var console = context.console();

        assertThat(console).isNotNull();
    }
}