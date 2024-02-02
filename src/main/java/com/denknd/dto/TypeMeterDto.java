package com.denknd.dto;

import lombok.Builder;

/**
 * Объект для передачи типов показаний
 * @param typeCode тип(код) показаний
 * @param typeDescription описания показаний
 * @param metric единица измерения показаний
 */
@Builder
public record TypeMeterDto(String typeCode,
                           String typeDescription,
                           String metric) {
}
