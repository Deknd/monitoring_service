package com.denknd.out.audit;

import com.denknd.repository.AuditRepository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Реализация репозитория аудита в памяти.
 */
public class InMemoryAuditRepository implements AuditRepository {
  /**
   * Хранит связь айди -> объект аудита.
   */
  private final Map<Long, Audit> auditMap = new HashMap<>();
  /**
   * Хранит связь юзер айди -> список айдишников Аудита.
   */
  private final Map<Long, Set<Long>> userIdToAuditIdListMap = new HashMap<>();
  /**
   * Для генерации айди.
   */
  private final Random random = new Random();

  /**
   * Сохраняет объект аудита в память.
   *
   * @param audit заполненный объект, айди не должно быть
   * @return возвращает копию объекта, сохраненного в памяти, с присвоенным айди
   */
  @Override
  public Audit save(Audit audit) {
    long auditId;

    if (audit.getAuditId() == null) {

      do {
        auditId = Math.abs(this.random.nextLong());

      } while (this.auditMap.containsKey(auditId));
    } else {
      return null;
    }
    audit.setAuditId(auditId);
    this.auditMap.put(auditId, audit);
    if (this.userIdToAuditIdListMap.containsKey(audit.getUser().userId())) {
      var auditIdList = this.userIdToAuditIdListMap.get(audit.getUser().userId());
      auditIdList.add(auditId);
    } else {
      var auditIdList = new HashSet<Long>();
      auditIdList.add(auditId);
      this.userIdToAuditIdListMap.put(audit.getUser().userId(), auditIdList);
    }
    return buildAuditCopy(audit);
  }

  /**
   * Конструирует объект аудита на основе существующего.
   *
   * @param audit заполненный объект
   * @return копия принятого объекта
   */
  private Audit buildAuditCopy(Audit audit) {
    return Audit.builder()
            .auditId(audit.getAuditId())
            .operation(audit.getOperation())
            .user(audit.getUser())
            .operationTime(audit.getOperationTime())
            .build();
  }
}
