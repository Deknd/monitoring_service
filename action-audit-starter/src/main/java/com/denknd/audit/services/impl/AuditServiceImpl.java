package com.denknd.audit.services.impl;

import com.denknd.audit.entity.Audit;
import com.denknd.audit.repository.AuditRepository;
import com.denknd.audit.services.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;

/**
 * Реализация интерфейса {@link AuditService}, предназначенная для обработки аудита.
 */
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {
  private final AuditRepository auditRepository;

  /**
   * Сохраняет запись аудита в репозиторий.
   *
   * @param audit объект аудита, содержащий информацию о совершенном действии пользователя
   */
  @Override
  public void addAction(Audit audit) {
    try {
      this.auditRepository.save(audit);
    } catch (SQLException e) {
      log.error("Ошибка сохранения аудита", e);
    }
  }
}

