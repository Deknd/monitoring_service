package com.denknd.in.commands.functions;

import com.denknd.entity.MeterReading;
import com.denknd.entity.TypeMeter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Конвертирует Лист показаний в строку, группирует по адресам и по типам показаний
 */
public class MeterReadingsHistoryToStringConverter implements Function<List<MeterReading>, String> {
    /**
     * Конвертирует лист показаний в строку, группируя его по адресам и типам показаний
     * @param meterReadings лист показаний
     * @return возвращает строку, со всеми показаниями
     */
    @Override
    public String apply(List<MeterReading> meterReadings) {

        var groupedByAddressAndTypeMeter = meterReadings.stream()
                .collect(Collectors.groupingBy(MeterReading::getAddress,
                        Collectors.groupingBy(meterReading -> meterReading.getTypeMeter().getTypeCode())));

        return groupedByAddressAndTypeMeter.keySet().stream()
                .map(address -> {
                    var typeMeterMap = groupedByAddressAndTypeMeter.get(address);
                    return address.getAddressId() + " - " + address.getRegion() + ", н.п. " + address.getCity() + " д. " + address.getHouse() + ", кв. " + address.getApartment()+"\nИстория подачи \n" +
                            convertingReadingsSameAddress(typeMeterMap);
                }).collect(Collectors.joining("\n"));


    }

    /**
     * Групирует список показаний по адресам
     * @param typeMeterMap список показаний
     * @return строку с показаниями по одному типу
     */
    private String convertingReadingsSameAddress(Map<String, List<MeterReading>> typeMeterMap) {
        return typeMeterMap.keySet().stream()
                .map(typeMeter -> {
                    var readings = typeMeterMap.get(typeMeter);
                    var meterReading = readings.stream().findFirst().orElse(null);
                    TypeMeter type = null;
                    if (meterReading != null) {
                        type = meterReading.getTypeMeter();
                    }
                    var message = type != null ? type.getTypeDescription() : "";
                    readings.sort(Comparator.comparing(MeterReading::getSubmissionMonth));
                    return  message + ": \n" +
                            convertingReadingsSameType(readings);
                }).collect(Collectors.joining("\n"));
    }

    /**
     * конвертирует показания в строку
     * @param readings список показаний
     * @return возвращает список показаний
     */
    private String convertingReadingsSameType(List<MeterReading> readings) {
        return readings.stream().map(mr ->
                        "показание: " + mr.getMeterValue() + " " + mr.getTypeMeter().getMetric() + " период: " + mr.getSubmissionMonth() + " дата подачи: " + mr.getTimeSendMeter())
                .collect(Collectors.joining("\n"));
    }
}
