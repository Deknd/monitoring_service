package com.denknd.adapter.repository;

import com.denknd.entity.Audit;
import com.denknd.port.AuditRepository;

import java.util.*;

public class InMemoryAuditRepository implements AuditRepository {

    private final Map<Long, Audit> auditMap = new HashMap<>();
    private final Map<Long, Set<Long>> userIdToListAuditMap = new HashMap<>();
    private final Random random = new Random();
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
        return Audit.builder()
                .auditId(auditId)
                .operation(audit.getOperation())
                .user(audit.getUser())
                .operationTime(audit.getOperationTime())
                .build();
    }
}
