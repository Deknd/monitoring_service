package com.denknd.in.commands.functions;

import com.denknd.entity.MeterReading;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultMeterReadingsToStringConverter implements Function<List<MeterReading>, String> {
    @Override
    public String apply(List<MeterReading> meterReadings) {
        var collect = meterReadings.stream().collect(Collectors.groupingBy(MeterReading::getAddress));
        return collect.keySet().stream()
                .map(
                        address -> address.toString()
                                + collect.get(address)
                                .stream().map(meterReading -> "\n"+meterReading.getTypeMeter().getTypeDescription()
                                        + ", счетчик: " + meterReading.getMeterValue() + " "
                                        + meterReading.getTypeMeter().getMetric() + ", месяц: "
                                        + meterReading.getSubmissionMonth()).collect(Collectors.joining(", "))
                )
                .collect(Collectors.joining("\n"));

    }
}
