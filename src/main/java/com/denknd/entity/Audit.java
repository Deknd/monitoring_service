package com.denknd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Сущность для хранения аудита
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Audit {
    /**
     * Идентификатор
     */
    private Long auditId;
    /**
     * Пользователь, который совершил действие
     */
    private User user;
    /**
     * Описание операции, которую он совершил
     */
    private String operation;
    /**
     * дата операции
     */
    private OffsetDateTime operationTime;
}
