package com.denknd.in.commands.functions;

import com.denknd.dto.MeterReadingResponseDto;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Класс для конвертации списка показаний счетчиков в строку, сгруппированную по адресам и типам показаний.
 */
public class MeterReadingsHistoryToStringConverter implements Function<List<MeterReadingResponseDto>, String> {
  /**
   * Конвертирует список показаний в строку, группируя его по адресам и типам показаний.
   *
   * @param meterReadings список показаний
   * @return строку, содержащую все показания
   */
  @Override
  public String apply(List<MeterReadingResponseDto> meterReadings) {

    var groupedByAddressAndTypeMeter = meterReadings.stream()
            .collect(Collectors.groupingBy(MeterReadingResponseDto::addressId,
                    Collectors.groupingBy(
                            MeterReadingResponseDto::code)));

    return groupedByAddressAndTypeMeter.keySet().stream()
            .map(addressId -> {
              var typeMeterMap = groupedByAddressAndTypeMeter.get(addressId);
              return "Адрес id: " + addressId + "\n"
                      + convertReadingsSameAddress(typeMeterMap);
            }).collect(Collectors.joining("\n"));


  }

  /**
   * Группирует список показаний по адресам и типам.
   *
   * @param typeMeterMap список показаний
   * @return строку с показаниями для одного адреса
   */
  private String convertReadingsSameAddress(Map<String, List<MeterReadingResponseDto>> typeMeterMap) {
    return typeMeterMap.keySet().stream()
            .map(typeMeter -> {
              var readings = typeMeterMap.get(typeMeter);
              var meterReading = readings.stream().findFirst().orElse(null);
              var message = "  " + meterReading.typeDescription();
              readings.sort(Comparator.comparing(MeterReadingResponseDto::submissionMonth));
              return message + ": \n"
                      + convertReadingsSameType(readings);
            }).collect(Collectors.joining("\n"));
  }

  /**
   * Конвертирует список показаний в строку.
   *
   * @param readings список показаний
   * @return строку с показаниями для одного типа
   */
  private String convertReadingsSameType(List<MeterReadingResponseDto> readings) {
    return readings.stream().map(mr ->
                    "   показание: "
                            + mr.meterValue()
                            + " " + mr.metric()
                            + " период: " + mr.submissionMonth()
                            + " дата подачи: " + mr.timeSendMeter())
            .collect(Collectors.joining("\n"));
  }
}
