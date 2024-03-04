package com.denknd.audit.repository;


import com.denknd.audit.entity.Audit;

import java.sql.SQLException;

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
  Audit save(Audit audit) throws SQLException;
}
