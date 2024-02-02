package com.denknd.controllers;

import com.denknd.dto.TypeMeterDto;
import com.denknd.mappers.TypeMeterMapper;
import com.denknd.services.TypeMeterService;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * Контроллер для работы с типами показаний
 */
@RequiredArgsConstructor
public class TypeMeterController {
  /**
   * Сервис для управления типами показаний
   */
  private final TypeMeterService typeMeterService;
  /**
   * Маппер для преобразования типов показаний
   */
  private final TypeMeterMapper typeMeterMapper;

  /**
   * Добавляет новый тип показаний
   *
   * @param typeMeterDto тип показаний, который нужно добавить
   * @return возвращает добавленный тип показаний
   */
  public TypeMeterDto addNewType(TypeMeterDto typeMeterDto) {
    var typeMeter = this.typeMeterMapper.mapTypeMeterDtoToTypeMeter(typeMeterDto);
    var result = this.typeMeterService.addNewTypeMeter(typeMeter);
    return this.typeMeterMapper.typeMeterToTypeMeterDto(result);
  }

  /**
   * Выдает доступные на данный момент типы показаний
   *
   * @return доступные типы показаний
   */
  public Set<TypeMeterDto> getTypeMeterCodes() {
    var typeMeter = this.typeMeterService.getTypeMeter();
    return Set.copyOf(this.typeMeterMapper.typeMetersToTypeMetersDto(typeMeter));
  }

}
