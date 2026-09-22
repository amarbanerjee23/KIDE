package com.kide.synthesis;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;

import activityDiagramModel.ActivityDiagram;
import com.kide.knowledge.KnowledgeDataset;
import com.kide.synthesis.activity2mnc.GenerateMnCDesignFromActivityDiagram;
import mncModel.ControlNode;
import mncModel.InterfaceDescription;
import mncModel.Model;

public final class DeterministicSynthesisService {
    private final ActivityRequirementExtractor extractor = new ActivityRequirementExtractor();
    private final DeterministicCapabilityMatcher matcher = new DeterministicCapabilityMatcher();
    private final DesignContractEngine contracts = new DesignContractEngine();
    private final KnowledgeResourceCatalogue catalogue = new KnowledgeResourceCatalogue();

    public SynthesisResult synthesize(ActivityDiagram diagram, KnowledgeDataset knowledge) {
        Objects.requireNonNull(diagram, "diagram");
        Objects.requireNonNull(knowledge, "knowledge");
        List<CapabilityRequirement> requirements = extractor.extract(diagram);
        List<SynthesisResource> resources = catalogue.resources(knowledge);
        SynthesisPlan plan = matcher.match(requirements, resources);

        if (!plan.feasible()) {
            return new SynthesisResult(
                    SynthesisResult.VERSION,
                    SynthesisStatus.NO_SOLUTION,
                    fingerprint(requirements, resources, plan, null),
                    plan,
                    plan.diagnostics(),
                    null);
        }

        DesignContractReport report = contracts.validate(
                diagram, requirements, plan, resources);
        if (!report.valid()) {
            return new SynthesisResult(
                    SynthesisResult.VERSION,
                    SynthesisStatus.CONTRACT_VIOLATION,
                    fingerprint(requirements, resources, plan, null),
                    plan,
                    report.diagnostics(),
                    null);
        }

        Model controller = new GenerateMnCDesignFromActivityDiagram()
                .parseActivityDiagramToGenerateAnMncModel(diagram);
        return new SynthesisResult(
                SynthesisResult.VERSION,
                SynthesisStatus.SUCCESS,
                fingerprint(requirements, resources, plan, controller),
                plan,
                report.diagnostics(),
                controller);
    }

    private static String fingerprint(
            List<CapabilityRequirement> requirements,
            List<SynthesisResource> resources,
            SynthesisPlan plan,
            Model controller) {
        StringBuilder canonical = new StringBuilder("kide-synthesis-v1\n");
        requirements.forEach(r -> canonical.append("req|")
                .append(r.id()).append('|').append(r.capabilityName())
                .append('|').append(r.requiredInterfaces())
                .append('|').append(r.requiredOperations()).append('\n'));
        resources.forEach(r -> canonical.append("res|")
                .append(r.id()).append('|').append(r.capabilities())
                .append('|').append(r.interfaces()).append('|').append(r.operations())
                .append('|').append(r.available()).append('|').append(r.priority())
                .append('|').append(r.latencyMillis()).append('|')
                .append(r.energyMilliJoules()).append('|').append(r.maxBindings())
                .append('|').append(r.conflictsWith()).append('\n'));
        plan.selections().forEach(s -> canonical.append("sel|")
                .append(s.requirementId()).append('|').append(s.resourceId()).append('\n'));
        if (controller != null) {
            canonical.append("controller|").append(controller.getName()).append('|')
                    .append(controller.getSystems().size()).append('\n');
            if (!controller.getSystems().isEmpty()
                    && controller.getSystems().get(0) instanceof InterfaceDescription descriptor) {
                canonical.append("interface|").append(descriptor.getName()).append('|')
                        .append(descriptor.getCommands().size()).append('|')
                        .append(descriptor.getEvents().size()).append('|')
                        .append(descriptor.getAlarms().size()).append('\n');
            }
            if (controller.getSystems().size() > 1
                    && controller.getSystems().get(1) instanceof ControlNode control) {
                canonical.append("control|").append(control.getName()).append('|')
                        .append(control.getCommandResponseBlocks().size()).append('|')
                        .append(control.getEventBlocks().size()).append('|')
                        .append(control.getAlarmBlocks().size()).append('\n');
            }
        }
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256")
                            .digest(canonical.toString().getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
