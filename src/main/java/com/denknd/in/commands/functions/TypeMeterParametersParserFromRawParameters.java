package com.denknd.in.commands.functions;

import com.denknd.controllers.TypeMeterController;
import com.denknd.dto.TypeMeterDto;
import com.denknd.entity.TypeMeter;
import com.denknd.services.TypeMeterService;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Класс для парсинга доступных типов параметров из входных данных консоли.
 */
@RequiredArgsConstructor
public class TypeMeterParametersParserFromRawParameters implements Function<String[], Set<String>> {

  /**
   * Контроллер для работы с типами параметров.
   */
  private final TypeMeterController typeMeterController;

  /**
   * Извлекает доступные типы параметров из входных данных консоли.
   *
   * @param commandAndParam параметры из консоли
   * @return множество доступных типов параметров
   */
  @Override
  public Set<String> apply(String[] commandAndParam) {
    var availableOptions = this.typeMeterController.getTypeMeterCodes().stream()
            .map(TypeMeterDto::typeCode)
            .collect(Collectors.toSet());
    return Arrays.stream(commandAndParam)
            .filter(availableOptions::contains)
            .collect(Collectors.toSet());
  }
}
