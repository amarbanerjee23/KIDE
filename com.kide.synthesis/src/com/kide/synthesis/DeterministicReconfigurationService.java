package com.kide.synthesis;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import activityDiagramModel.ActivityDiagram;
import com.kide.knowledge.KnowledgeDataset;

public final class DeterministicReconfigurationService {
    private final ActivityRequirementExtractor extractor = new ActivityRequirementExtractor();
    private final KnowledgeResourceCatalogue catalogue = new KnowledgeResourceCatalogue();
    private final DeterministicReconfigurationPlanner planner =
            new DeterministicReconfigurationPlanner();
    private final DesignContractEngine contracts = new DesignContractEngine();

    public ReconfigurationResult reconfigure(
            ActivityDiagram diagram,
            KnowledgeDataset knowledge,
            SynthesisPlan previousPlan,
            ReconfigurationCause cause) {
        Objects.requireNonNull(diagram, "diagram");
        Objects.requireNonNull(knowledge, "knowledge");
        Objects.requireNonNull(previousPlan, "previousPlan");
        Objects.requireNonNull(cause, "cause");

        List<CapabilityRequirement> requirements = extractor.extract(diagram);
        List<SynthesisResource> resources = catalogue.resources(knowledge);
        ReconfigurationResult result =
                planner.reconfigure(previousPlan, requirements, resources, cause);

        if (!result.plan().feasible()) {
            return result;
        }

        DesignContractReport contract =
                contracts.validate(diagram, requirements, result.plan(), resources);
        if (contract.valid()) {
            return result;
        }

        List<SynthesisDiagnostic> diagnostics = new ArrayList<>(result.diagnostics());
        for (SynthesisDiagnostic diagnostic : contract.diagnostics()) {
            if (!diagnostics.contains(diagnostic)) diagnostics.add(diagnostic);
        }
        SynthesisPlan failedPlan =
                new SynthesisPlan(false, result.plan().selections(), diagnostics);
        List<StateMigrationInstruction> fallback = result.migrations().stream()
                .map(migration -> migration.policy() == StateMigrationPolicy.RETIRE
                        ? migration
                        : new StateMigrationInstruction(
                                migration.requirementId(),
                                StateMigrationPolicy.SAFE_FALLBACK,
                                migration.fromResourceId(),
                                migration.toResourceId(),
                                "Current design contracts are invalid; do not apply automatic reconfiguration."))
                .toList();

        return new ReconfigurationResult(
                result.serviceVersion(),
                ReconfigurationStatus.NO_SOLUTION,
                result.cause(),
                result.fingerprint(),
                failedPlan,
                fallback,
                diagnostics);
    }
}
