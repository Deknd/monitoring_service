package com.denknd.adapter.repository;

import com.denknd.entity.Audit;
import com.denknd.port.AuditRepository;

import java.util.*;

/**
 * Для хранения аудита
 */
public class InMemoryAuditRepository implements AuditRepository {
    /**
     * Хранит связь айди -> объект аудита
     */
    private final Map<Long, Audit> auditMap = new HashMap<>();
    /**
     * Хранит связь юзер айди -> список айдишников Аудита
     */
    private final Map<Long, Set<Long>> userIdToListAuditMap = new HashMap<>();
    /**
     * Для генерации айди
     */
    private final Random random = new Random();

    /**
     * Сохраняет объект аудита в память
     * @param audit заполненный объект, айди не должно быть
     * @return возвращает копию объекта сохраненного в памяти, с айди
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
        if(this.userIdToListAuditMap.containsKey(audit.getUser().getUserId())){
            var auditIdList = this.userIdToListAuditMap.get(audit.getUser().getUserId());
            auditIdList.add(auditId);
        } else {
            var auditIdList = new HashSet<Long>();
            auditIdList.add(auditId);
            this.userIdToListAuditMap.put(audit.getUser().getUserId(), auditIdList);
        }
        return buildAudit(audit);
    }

    /**
     * Строит объект, на основе старого
     * @param audit заполненный объект
     * @return копия принятого объекта
     */
    private Audit buildAudit(Audit audit) {
        return Audit.builder()
                .auditId(audit.getAuditId())
                .operation(audit.getOperation())
                .user(audit.getUser())
                .operationTime(audit.getOperationTime())
                .build();
    }
}
