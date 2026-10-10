package com.kide.enterprise.server;

import com.kide.enterprise.authorization.ServerAuthorizationGate;
import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.modelrepo.ModelRepository;
import com.kide.knowledge.ProjectKnowledgeService;
import com.kide.synthesis.ProjectSynthesisService;
import com.kide.codegen.ProjectGenerationService;

/** All state for exactly one registered canonical engineering project. */
record HostedApiProjectRuntime(
        EnterpriseContext context,
        ServerAuthorizationGate authorization,
        ModelRepository models,
        ProjectCollaborationService collaboration,
        ProjectKnowledgeService knowledge,
        ProjectSynthesisService synthesis,
        ProjectGenerationService generation) { }
