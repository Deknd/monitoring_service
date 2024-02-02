package com.denknd.in.commands.functions;

import com.denknd.dto.MeterReadingResponseDto;
import com.denknd.entity.Address;
import com.denknd.entity.MeterReading;
import com.denknd.entity.TypeMeter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MeterReadingsHistoryToStringConverterTest {

    private MeterReadingsHistoryToStringConverter converter;
    @BeforeEach
    void setUp() {
        this.converter = new MeterReadingsHistoryToStringConverter();
    }

    @Test
    void apply() {
        var coldS = "Холодная вода";
        var date = "Отопление";
        var meterCold = 123.234;
        var meterCold2 = 1231.234;
        var meterCold3 = 123123.234;
        var meterCold4 = 12312.234;
        var meterValue1 = 12768.234;
        var meterValue2 = 123763.234;
        var meterColdReading1 = MeterReadingResponseDto.builder()
                .addressId(0L)
                .typeDescription(coldS)
                .meterValue(meterCold)
                .metric("м3")
                .code("test")
                .submissionMonth(YearMonth.now().minusMonths(3))
                .build();
        var meterColdReading2 = MeterReadingResponseDto.builder()
                .addressId(0L)
                .typeDescription(coldS)
                .meterValue(meterCold2)
                .metric("м3")
                .code("test")
                .submissionMonth(YearMonth.now().minusMonths(2))
                .build();
        var meterColdReading3 = MeterReadingResponseDto.builder()
                .addressId(0L)
                .typeDescription(coldS)
                .meterValue(meterCold3)
                .metric("м3")
                .code("test")
                .submissionMonth(YearMonth.now())
                .build();
        var meterColdReading4 = MeterReadingResponseDto.builder()
                .addressId(0L)
                .typeDescription(coldS)
                .meterValue(meterCold4)
                .metric("м3")
                .code("test")
                .submissionMonth(YearMonth.now().minusMonths(1))
                .build();
        var meterReading1 = MeterReadingResponseDto.builder()
                .addressId(0L)
                .typeDescription(date)
                .meterValue(meterValue1)
                .metric("Гкал")
                .code("hot")
                .submissionMonth(YearMonth.now())
                .build();
        var meterReading2 = MeterReadingResponseDto.builder()
                .addressId(1L)
                .typeDescription(coldS)
                .meterValue(meterValue2)
                .metric("м3")
                .code("test")
                .submissionMonth(YearMonth.now())
                .build();

       var apply = this.converter.apply(List.of(meterColdReading1, meterColdReading2, meterColdReading3, meterColdReading4, meterReading1, meterReading2));

       assertThat(apply).isNotNull().contains(coldS).contains(date)
                .contains(String.valueOf(meterCold ))
                .contains(String.valueOf(meterCold2 ))
                .contains(String.valueOf(meterCold3 ))
                .contains(String.valueOf(meterCold4 ))
                .contains(String.valueOf(meterValue1 ))
                .contains(String.valueOf(meterValue2 ));
    }
}