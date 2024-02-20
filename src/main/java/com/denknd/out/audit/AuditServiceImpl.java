package com.denknd.out.audit;

import com.denknd.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * Реализация интерфейса {@link AuditService}, предназначенная для обработки аудита.
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class AuditServiceImpl implements AuditService {
  /**
   * Репозиторий аудита.
   */
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

