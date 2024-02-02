package com.denknd.in.commands.functions;

import com.denknd.controllers.TypeMeterController;
import com.denknd.dto.TypeMeterDto;
import com.denknd.entity.TypeMeter;
import com.denknd.services.TypeMeterService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.Mapper;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TypeMeterParametersParserFromRawParametersTest {

    private TypeMeterParametersParserFromRawParameters parserFromRawParameters;
    @Mock
    private TypeMeterController typeMeterController;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        this.closeable = MockitoAnnotations.openMocks(this);
        this.parserFromRawParameters = new TypeMeterParametersParserFromRawParameters(this.typeMeterController);
    }
    @AfterEach
    void tearDown() throws Exception {
        this.closeable.close();
    }
    @Test
    @DisplayName("Проверяет вытаскивает из массива строк, нужные параметры")
    void apply() {
        var test1 = "test1";
        var test2 = "test2";
        var test3 = "test3";
        var typeMeters = Set.of(
                TypeMeterDto.builder().typeCode(test1).build(),
                TypeMeterDto.builder().typeCode(test2).build(),
                TypeMeterDto.builder().typeCode(test3).build()
        );
        var commandAndParam = new String[]{test1, test2, "sdfsdf", "fsdfsdf","sdfsdf"};
        when(this.typeMeterController.getTypeMeterCodes()).thenReturn(typeMeters);

        var stringSet = this.parserFromRawParameters.apply(commandAndParam);

        assertThat(stringSet).contains(test1, test2).doesNotContain(test3);
        verify(this.typeMeterController, times(1)).getTypeMeterCodes();
    }
}