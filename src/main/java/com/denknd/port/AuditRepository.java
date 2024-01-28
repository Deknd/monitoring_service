package com.denknd.port;

import com.denknd.entity.Audit;

public interface AuditRepository {
    Audit save(Audit audit);
}
