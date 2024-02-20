package com.denknd.util.functions;

import com.denknd.entity.TypeMeter;
import com.denknd.services.TypeMeterService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс для парсинга доступных типов параметров из входных данных запроса.
 */
@Component
@RequiredArgsConstructor
public class TypeMeterParametersParserFromRawParameters implements Converter<String, Set<Long>> {//implements Function<String, Set<Long>> {

  /**
   * Контроллер для работы с типами параметров.
   */
  private final TypeMeterService typeMeterService;

  /**
   * Извлекает доступные типы параметров из входных данных запроса.
   *
   * @param source Входные данные запроса.
   * @return Множество идентификаторов доступных типов параметров.
   */
  @Override
  public Set<Long> convert(String source) {
    try {
      var availableOptions = this.typeMeterService.getTypeMeter().stream()
              .map(TypeMeter::getTypeMeterId)
              .collect(Collectors.toSet());
      return Arrays.stream(source.split(","))
              .map(Long::parseLong)
              .filter(availableOptions::contains)
              .collect(Collectors.toSet());
    } catch (NumberFormatException e) {
      return Collections.emptySet();
    }
  }
}
