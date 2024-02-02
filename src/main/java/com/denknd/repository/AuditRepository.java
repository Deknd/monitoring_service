package com.denknd.repository;

import com.denknd.out.audit.Audit;

/**
 * Интерфейс репозитория для хранения и получения объектов аудита.
 */
public interface AuditRepository {
  /**
   * Сохраняет объект аудита в репозиторий.
   *
   * @param audit Объект аудита, не содержащий идентификатор.
   * @return Объект аудита с присвоенным идентификатором.
   */
  Audit save(Audit audit);
}
