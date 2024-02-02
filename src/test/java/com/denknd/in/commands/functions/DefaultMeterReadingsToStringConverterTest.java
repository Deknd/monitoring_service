package com.denknd.in.commands.functions;

import com.denknd.dto.MeterReadingResponseDto;
import com.denknd.entity.Address;
import com.denknd.entity.MeterReading;
import com.denknd.entity.TypeMeter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultMeterReadingsToStringConverterTest {

    private DefaultMeterReadingsToStringConverter converter;
    @BeforeEach
    void setUp() {
        this.converter = new DefaultMeterReadingsToStringConverter();
    }

    @Test
    @DisplayName("Проверяет, что список с показаниями парсится в строку")
    void apply() {
        var coldS = "Холодная вода";
        var date = "Отопление";
        var meterValue = 123123.234;
        var meterValue1 = 12768.234;
        var meterValue2 = 123763.234;
        var meterReading = MeterReadingResponseDto.builder()
                .meterValue(meterValue)
                .addressId(0L)
                .typeDescription(coldS)
                .code("code")
                .metric("m3")
                .submissionMonth(YearMonth.now())
                .build();
        var meterReading1 = MeterReadingResponseDto.builder()
                .meterValue(meterValue1)
                .addressId(0L)
                .typeDescription(date)
                .code("code1")
                .metric("kw")
                .submissionMonth(YearMonth.now().minusMonths(1))
                .build();
        var meterReading2 = MeterReadingResponseDto.builder()
                .meterValue(meterValue2)
                .addressId(1L)
                .typeDescription(date)
                .code("code1")
                .metric("kw")
                .submissionMonth(YearMonth.now())
                .build();

        var apply = this.converter.apply(List.of(meterReading, meterReading1, meterReading2));
        System.out.println(apply);
        assertThat(apply).isNotNull()
                .contains(coldS).contains(date)
                .contains(String.valueOf(meterValue))
                .contains(String.valueOf(meterValue1))
                .contains(String.valueOf(meterValue2));

    }
}