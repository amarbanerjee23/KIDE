package com.kide.enterprise.audit;

import java.util.List;

public interface AuditLedger {
    AuditEvent append(AuditEventDraft draft);

    List<AuditEvent> snapshot();

    boolean verify();
}
