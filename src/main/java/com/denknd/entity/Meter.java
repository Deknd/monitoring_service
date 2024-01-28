package com.denknd.entity;

import lombok.Builder;

import java.time.OffsetDateTime;

/**
 * Класс для хранения информации о счетчике
 */
@Builder
public class Meter {
    /**
     * Идентификатор сущности
     */
    private Long id;
    /**
     * серийный номер
     */
    private String serialNumber;
    /**
     * Дата установки
     */
    private OffsetDateTime installationDate;
    /**
     * Дата последней проверки
     */
    private OffsetDateTime lastCheckDate;
    /**
     * Модель счетчика
     */
    private String meterModel;
}
