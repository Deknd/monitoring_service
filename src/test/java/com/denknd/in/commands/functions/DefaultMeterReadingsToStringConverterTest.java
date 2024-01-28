package com.denknd.in.commands.functions;

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
        var address1 = Address.builder().addressId(0L).build();
        var address2 = Address.builder().addressId(1L).build();
        var coldS = "Холодная вода";
        var date = "Отопление";
        var meterValue = 123123.234;
        var meterValue1 = 12768.234;
        var meterValue2 = 123763.234;


        var cold = TypeMeter.builder().typeDescription(coldS).metric("м3").build();
        var typeMeter = TypeMeter.builder().typeDescription(date).metric("Гкал").build();
        var meterReading = MeterReading.builder().meterValue(meterValue).address(address1).typeMeter(cold).submissionMonth(YearMonth.now()).build();
        var meterReading1 = MeterReading.builder().meterValue(meterValue1).address(address1).typeMeter(typeMeter).submissionMonth(YearMonth.now()).build();
        var meterReading2 = MeterReading.builder().meterValue(meterValue2).address(address2).typeMeter(cold).submissionMonth(YearMonth.now()).build();

        var apply = this.converter.apply(List.of(meterReading, meterReading1, meterReading2));
        
        assertThat(apply).isNotNull()
                .contains(coldS).contains(date)
                .contains(String.valueOf(meterValue))
                .contains(String.valueOf(meterValue1))
                .contains(String.valueOf(meterValue2));

    }
}