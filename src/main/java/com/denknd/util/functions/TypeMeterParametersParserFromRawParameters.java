package com.denknd.util.functions;

import com.denknd.controllers.TypeMeterController;
import com.denknd.dto.TypeMeterDto;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Класс для парсинга доступных типов параметров из входных данных запроса.
 */
@RequiredArgsConstructor
public class TypeMeterParametersParserFromRawParameters implements Function<String, Set<Long>> {

  /**
   * Контроллер для работы с типами параметров.
   */
  private final TypeMeterController typeMeterController;

  /**
   * Извлекает доступные типы параметров из входных данных консоли.
   *
   * @param param параметры из запроса
   * @return множество доступных идентификаторов типов параметров
   */
  @Override
  public Set<Long> apply(String param) {
    if (param == null) {
      return Collections.emptySet();
    }
    try {
      var availableOptions = this.typeMeterController.getTypeMeterCodes().stream()
              .map(TypeMeterDto::typeMeterId)
              .collect(Collectors.toSet());
      return Arrays.stream(param.split(","))
              .map(Long::parseLong)
              .filter(availableOptions::contains)
              .collect(Collectors.toSet());
    } catch (NumberFormatException e) {
      return Collections.emptySet();
    }
  }
}
