package com.denknd.out.audit;

import com.denknd.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;

/**
 * Сервис для обработки аудита.
 */
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {
  /**
   * Репозиторий аудита.
   */
  private final AuditRepository auditRepository;

  /**
   * Сохраняет аудит в репозиторий.
   *
   * @param audit действия пользователя
   */
  @Override
  public void addAction(Audit audit) {
    try {
      this.auditRepository.save(audit);
    } catch (SQLException e) {
      log.error("Ошибка сохраения аудита");
    }
  }
}

