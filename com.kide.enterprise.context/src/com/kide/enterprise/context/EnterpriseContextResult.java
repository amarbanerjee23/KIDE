package com.kide.enterprise.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** Non-throwing load/provision result used at Eclipse runtime boundaries. */
public final class EnterpriseContextResult {
    private final ContextStatus status;
    private final EnterpriseContext context;
    private final List<ContextDiagnostic> diagnostics;

    EnterpriseContextResult(ContextStatus status, EnterpriseContext context, List<ContextDiagnostic> diagnostics) {
        this.status = status;
        this.context = context;
        this.diagnostics = Collections.unmodifiableList(new ArrayList<>(diagnostics));
    }

    public ContextStatus status() {
        return status;
    }

    public boolean isReady() {
        return status == ContextStatus.READY && context != null;
    }

    public Optional<EnterpriseContext> context() {
        return Optional.ofNullable(context);
    }

    public List<ContextDiagnostic> diagnostics() {
        return diagnostics;
    }

    public String summary() {
        if (diagnostics.isEmpty()) {
            return status.name();
        }
        return diagnostics.get(0).message();
    }
}
