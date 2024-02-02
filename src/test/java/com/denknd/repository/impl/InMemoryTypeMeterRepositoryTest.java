package com.denknd.repository.impl;

import com.denknd.entity.TypeMeter;
import com.denknd.repository.impl.InMemoryTypeMeterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class InMemoryTypeMeterRepositoryTest {

    private InMemoryTypeMeterRepository typeMeterRepository;
    @BeforeEach
    void setUp() {
        this.typeMeterRepository = new InMemoryTypeMeterRepository();
    }

    @Test
    @DisplayName("Проверяет, что по дефолту содержаться данные в репозитории")
    void findTypeMeter() {
        var typeMeter = this.typeMeterRepository.findTypeMeter();

        assertThat(typeMeter).isNotEmpty();
        assertThat(typeMeter.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Проверет, что объекту назначается айди и возвращается полностью собранный объект")
    void save(){
        var typeMeter = TypeMeter.builder()
                .typeDescription("description")
                .typeCode("test")
                .metric("ball").build();

        var save = this.typeMeterRepository.save(typeMeter);

        assertThat(save).hasNoNullFieldsOrProperties();
    }
    @Test
    @DisplayName("Проверяет, что если у объекта уже есть айди, он не сохраниться")
    void save_notNew(){
        var typeMeter = TypeMeter.builder()
                .typeMeterId(123L)
                .typeDescription("description")
                .typeCode("test")
                .metric("ball").build();

        var save = this.typeMeterRepository.save(typeMeter);

        assertThat(save).isNull();
    }

}