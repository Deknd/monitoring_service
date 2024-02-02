  package com.denknd.in.commands.functions;

  import com.denknd.dto.MeterReadingResponseDto;
  import com.denknd.entity.MeterReading;

  import java.util.List;
  import java.util.function.Function;
  import java.util.stream.Collectors;

  /**
   * Класс для конвертации показаний счетчиков в строку.
   */
  public class DefaultMeterReadingsToStringConverter implements Function<List<MeterReadingResponseDto>, String> {
    /**
     * Принимает список показаний, группирует их по адресам и формирует строку для каждого адреса.
     *
     * @param meterReadings список показаний счетчиков
     * @return строка, сформированная из списка показаний
     */
    @Override
    public String apply(List<MeterReadingResponseDto> meterReadings) {
      var readingsByAddress = meterReadings.stream().collect(Collectors.groupingBy(MeterReadingResponseDto::addressId));
      return readingsByAddress.keySet().stream()
              .map(
                      address -> address.toString()
                              + readingsByAddress.get(address)
                              .stream()
                              .map(
                                      meterReading ->
                                              "\n" + meterReading.typeDescription()
                                                      + ", счетчик: "
                                                      + meterReading.meterValue() + " "
                                                      + meterReading.metric()
                                                      + ", месяц: "
                                                      + meterReading.submissionMonth())
                              .collect(Collectors.joining(", "))
              )
              .collect(Collectors.joining("\n"));

    }
  }
