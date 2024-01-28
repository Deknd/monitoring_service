package com.denknd.in.commands.functions;

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
        var address1 = Address.builder().addressId(0L).build();
        var address2 = Address.builder().addressId(1L).build();
        var coldS = "Холодная вода";
        var date = "Отопление";
        var meterCold = 123.234;
        var meterCold2 = 1231.234;
        var meterCold3 = 123123.234;
        var meterCold4 = 12312.234;
        var meterValue1 = 12768.234;
        var meterValue2 = 123763.234;


        var cold = TypeMeter.builder().typeDescription(coldS).metric("м3").typeCode("test").build();
        var typeMeter = TypeMeter.builder().typeDescription(date).metric("Гкал").typeCode("hot").build();
        var meterColdReading1 = MeterReading.builder().meterValue(meterCold).address(address1).typeMeter(cold).submissionMonth(YearMonth.now().minusMonths(3)).build();
        var meterColdReading2 = MeterReading.builder().meterValue(meterCold2).address(address1).typeMeter(cold).submissionMonth(YearMonth.now().minusMonths(2)).build();
        var meterColdReading3 = MeterReading.builder().meterValue(meterCold3).address(address1).typeMeter(cold).submissionMonth(YearMonth.now()).build();
        var meterColdReading4 = MeterReading.builder().meterValue(meterCold4).address(address1).typeMeter(cold).submissionMonth(YearMonth.now().minusMonths(1)).build();
        var meterReading1 = MeterReading.builder().meterValue(meterValue1).address(address1).typeMeter(typeMeter).submissionMonth(YearMonth.now()).build();
        var meterReading2 = MeterReading.builder().meterValue(meterValue2).address(address2).typeMeter(cold).submissionMonth(YearMonth.now()).build();

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