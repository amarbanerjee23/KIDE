package com.kide.enterprise.context;

import java.util.List;
import java.util.Optional;

/**
 * Runtime-neutral hosted-project discovery boundary shared by REST/LSP/GLSP.
 */
public interface HostedProjectRegistry {
    List<HostedProjectRegistration> list();

    Optional<HostedProjectRegistration> resolveProject(String projectId);

    Optional<HostedProjectRegistration> resolveWorkspace(String workspaceId);
}
