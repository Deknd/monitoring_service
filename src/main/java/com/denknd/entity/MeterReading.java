package com.denknd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeterReading {
    private Long meterId;
    private Address address;
    private TypeMeter typeMeter;
    private double meterValue;
    private YearMonth submissionMonth;
    private Meter meter;
    private OffsetDateTime timeSendMeter;

    @Override
    public String toString() {
        return "Адрес: " + address +
                ",\n" + typeMeter.getTypeDescription() +
                " счетчик: " + meterValue + " "
                + typeMeter.getMetric() +
                " период:" + submissionMonth +
                " дата подачи: " + timeSendMeter.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

}
