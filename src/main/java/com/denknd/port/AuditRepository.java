package com.denknd.port;

import com.denknd.entity.Audit;

/**
 * Интерфейс для хранения и получения объектов аудита
 */
public interface AuditRepository {
    /**
     * Сохраняет аудит в подходящее место
     * @param audit полностью заполненный объект без айди
     * @return объект аудита с айди
     */
    Audit save(Audit audit);
}
