package com.denknd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс для хранения типа показаний
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypeMeter {
    /**
     * Идентификатор объекта
     */
    private Long typeMeterId;
    /**
     * Код, который используется для подачи показаний
     */
    private String typeCode;
    /**
     * Описание данного типа
     */
    private String typeDescription;
    /**
     * Единица измерения
     */
    private String metric;
}
