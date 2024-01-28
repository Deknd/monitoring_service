package com.denknd.in.commands.functions;

import com.denknd.entity.TypeMeter;
import com.denknd.services.TypeMeterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TypeMeterParametersParserFromRawParametersTest {

    private TypeMeterParametersParserFromRawParameters parserFromRawParameters;
    private TypeMeterService typeMeterService;

    @BeforeEach
    void setUp() {
        this.typeMeterService = mock(TypeMeterService.class);
        this.parserFromRawParameters = new TypeMeterParametersParserFromRawParameters(this.typeMeterService);
    }

    @Test
    @DisplayName("Проверяет вытаскивает из массива строк, нужные параметры")
    void apply() {
        var test1 = "test1";
        var test2 = "test2";
        var test3 = "test3";
        var typeMeters = List.of(
                TypeMeter.builder().typeCode(test1).build(),
                TypeMeter.builder().typeCode(test2).build(),
                TypeMeter.builder().typeCode(test3).build()
        );
        var commandAndParam = new String[]{test1, test2, "sdfsdf", "fsdfsdf","sdfsdf"};
        when(this.typeMeterService.getTypeMeter()).thenReturn(typeMeters);

        var stringSet = this.parserFromRawParameters.apply(commandAndParam);

        assertThat(stringSet).contains(test1, test2).doesNotContain(test3);
        verify(this.typeMeterService, times(1)).getTypeMeter();
    }
}