package com.kide.synthesis.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import CapabilityDescription.Capability;
import CapabilityDescription.CapabilityDescriptionFactory;
import CapabilityDescription.ControlCapabilities;
import activityDiagramModel.Activity;
import activityDiagramModel.ActivityDiagram;
import activityDiagramModel.ActivityDiagramModelFactory;
import com.kide.knowledge.KnowledgeDataset;
import com.kide.knowledge.KnowledgeProvenance;
import com.kide.knowledge.KnowledgeTerm;
import com.kide.knowledge.KnowledgeTriple;
import com.kide.knowledge.KnowledgeVocabulary;
import com.kide.synthesis.CapabilityRequirement;
import com.kide.synthesis.DeterministicCapabilityMatcher;
import com.kide.synthesis.DeterministicSynthesisService;
import com.kide.synthesis.SynthesisDiagnosticSeverity;
import com.kide.synthesis.SynthesisResource;
import com.kide.synthesis.SynthesisStatus;
import com.kide.synthesis.SynthesisVocabulary;
import mncModel.Event;
import mncModel.InterfaceDescription;
import mncModel.MncModelFactory;

public class DeterministicSynthesisTest {
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-09-22T08:30:00Z"), ZoneOffset.UTC);

    @Test
    public void boundCapabilityProducesRepeatableControllerComposition() {
        ActivityDiagram diagram = diagramWithBoundCapability("Observe", "Sensor");
        KnowledgeDataset knowledge = knowledge(
                device("urn:kide:device:camera-a", "Camera A", "Observe", "Sensor", 10, 8, 40));

        DeterministicSynthesisService service = new DeterministicSynthesisService();
        var first = service.synthesize(diagram, knowledge);
        var second = service.synthesize(diagram, knowledge);

        assertEquals(SynthesisStatus.SUCCESS, first.status());
        assertEquals(first.fingerprint(), second.fingerprint());
        assertEquals(first.plan().selections(), second.plan().selections());
        assertEquals("urn:kide:device:camera-a", first.plan().selections().get(0).resourceId());
        assertNotNull(first.controllerModel());
        assertEquals("Workflow", first.controllerModel().getName());
        assertEquals(2, first.controllerModel().getSystems().size());
    }

    @Test
    public void declaredRankingAndIdTieBreakAreDeterministicAndExplained() {
        CapabilityRequirement requirement = new CapabilityRequirement(
                "activity:Observe", "Observe", "Observe", Set.of(), Set.of(), true);
        SynthesisResource z = new SynthesisResource(
                "urn:resource:z", "Z", Set.of("Observe"), Set.of(), Set.of(), Set.of(),
                true, 5, 10, 100, 1);
        SynthesisResource a = new SynthesisResource(
                "urn:resource:a", "A", Set.of("Observe"), Set.of(), Set.of(), Set.of(),
                true, 5, 10, 100, 1);

        var plan = new DeterministicCapabilityMatcher().match(
                List.of(requirement), List.of(z, a));

        assertTrue(plan.feasible());
        assertEquals("urn:resource:a", plan.selections().get(0).resourceId());
        assertTrue(plan.diagnostics().stream()
                .anyMatch(d -> "AMBIGUOUS_MATCH".equals(d.code())
                        && d.severity() == SynthesisDiagnosticSeverity.WARNING));
    }

    @Test
    public void missingCapabilityReturnsNoSolutionWithoutControllerMutation() {
        ActivityDiagram diagram = diagramWithBoundCapability("Observe", "Sensor");
        KnowledgeDataset knowledge = knowledge(
                device("urn:kide:device:lamp", "Lamp", "Illuminate", "Light", 1, 1, 1));

        var result = new DeterministicSynthesisService().synthesize(diagram, knowledge);

        assertEquals(SynthesisStatus.NO_SOLUTION, result.status());
        assertTrue(result.plan().selections().isEmpty());
        assertTrue(result.diagnostics().stream()
                .anyMatch(d -> "NO_FEASIBLE_RESOURCE".equals(d.code())));
        assertEquals(null, result.controllerModel());
    }

    @Test
    public void textualCapabilityCanMatchButCannotInventControlSemantics() {
        ActivityDiagram diagram = ActivityDiagramModelFactory.eINSTANCE.createActivityDiagram();
        diagram.setName("Workflow");
        Activity activity = ActivityDiagramModelFactory.eINSTANCE.createActivity();
        activity.setName("Observe");
        activity.setRequiredCapability("Observe");
        diagram.getActivities().add(activity);

        KnowledgeDataset knowledge = knowledge(
                device("urn:kide:device:camera-a", "Camera A", "Observe", "", 10, 8, 40));

        var result = new DeterministicSynthesisService().synthesize(diagram, knowledge);

        assertEquals(SynthesisStatus.CONTRACT_VIOLATION, result.status());
        assertFalse(result.plan().selections().isEmpty());
        assertTrue(result.diagnostics().stream()
                .anyMatch(d -> "UNBOUND_CAPABILITY_MODEL".equals(d.code())));
        assertEquals(null, result.controllerModel());
    }

    @Test
    public void eventCapabilityWithoutDeclaredInitProcessStillSynthesizesSafely() {
        InterfaceDescription descriptor = MncModelFactory.eINSTANCE.createInterfaceDescription();
        descriptor.setName("Sensor");
        Event ready = MncModelFactory.eINSTANCE.createEvent();
        ready.setName("Ready");
        descriptor.getEvents().add(ready);

        Capability capability = CapabilityDescriptionFactory.eINSTANCE.createCapability();
        capability.setName("Observe");
        capability.getComponentInterface().add(descriptor);
        ControlCapabilities control =
                CapabilityDescriptionFactory.eINSTANCE.createControlCapabilities();
        control.getEvents().add(ready);
        capability.setProvidesControlCapabilities(control);

        Activity activity = ActivityDiagramModelFactory.eINSTANCE.createActivity();
        activity.setName("Observe");
        activity.setBindCapability(capability);
        activity.getUseControlCapabilities().add(ready);

        ActivityDiagram diagram = ActivityDiagramModelFactory.eINSTANCE.createActivityDiagram();
        diagram.setName("Workflow");
        diagram.getActivities().add(activity);

        KnowledgeDataset knowledge = knowledge(
                device("urn:kide:device:camera-a", "Camera A", "Observe", "Sensor", 10, 8, 40));

        var result = new DeterministicSynthesisService().synthesize(diagram, knowledge);

        assertEquals(SynthesisStatus.SUCCESS, result.status());
        assertNotNull(result.controllerModel());
        InterfaceDescription generated =
                (InterfaceDescription) result.controllerModel().getSystems().get(0);
        assertTrue(generated.getSubscribedItems().getSubscribedEvents().stream()
                .anyMatch(event -> "Ready".equals(event.getName())));
    }

    @Test
    public void unavailableResourcesAreNeverSelected() {
        CapabilityRequirement requirement = new CapabilityRequirement(
                "activity:Observe", "Observe", "Observe", Set.of(), Set.of(), true);
        SynthesisResource unavailable = new SynthesisResource(
                "urn:resource:a", "A", Set.of("Observe"), Set.of(), Set.of(), Set.of(),
                false, 100, 0, 0, 100);

        var plan = new DeterministicCapabilityMatcher().match(
                List.of(requirement), List.of(unavailable));

        assertFalse(plan.feasible());
        assertTrue(plan.selections().isEmpty());
    }

    private static ActivityDiagram diagramWithBoundCapability(
            String capabilityName, String interfaceName) {
        InterfaceDescription descriptor = MncModelFactory.eINSTANCE.createInterfaceDescription();
        descriptor.setName(interfaceName);
        Capability capability = CapabilityDescriptionFactory.eINSTANCE.createCapability();
        capability.setName(capabilityName);
        capability.getComponentInterface().add(descriptor);

        Activity activity = ActivityDiagramModelFactory.eINSTANCE.createActivity();
        activity.setName("Observe");
        activity.setBindCapability(capability);

        ActivityDiagram diagram = ActivityDiagramModelFactory.eINSTANCE.createActivityDiagram();
        diagram.setName("Workflow");
        diagram.getActivities().add(activity);
        return diagram;
    }

    private static KnowledgeDataset knowledge(List<KnowledgeTriple> triples) {
        return new KnowledgeDataset(
                "1", "synthesis-fixture", "PROJECT", "p1",
                new KnowledgeProvenance(
                        "urn:test:synthesis", "test-authority", "tester",
                        CLOCK.millis(), "INTERNAL"),
                triples);
    }

    private static List<KnowledgeTriple> device(
            String id,
            String label,
            String capability,
            String interfaceName,
            int priority,
            long latency,
            long energy) {
        java.util.ArrayList<KnowledgeTriple> triples = new java.util.ArrayList<>();
        triples.add(new KnowledgeTriple(
                id, KnowledgeVocabulary.RDF_TYPE, KnowledgeTerm.iri(KnowledgeVocabulary.DEVICE)));
        triples.add(new KnowledgeTriple(
                id, KnowledgeVocabulary.LABEL, KnowledgeTerm.literal(label)));
        triples.add(new KnowledgeTriple(
                id, SynthesisVocabulary.PROVIDES_CAPABILITY,
                KnowledgeTerm.iri("urn:kide:capability:" + capability)));
        if (!interfaceName.isBlank()) {
            triples.add(new KnowledgeTriple(
                    id, SynthesisVocabulary.PROVIDES_INTERFACE,
                    KnowledgeTerm.iri("urn:kide:interface:" + interfaceName)));
        }
        triples.add(new KnowledgeTriple(
                id, SynthesisVocabulary.PRIORITY, KnowledgeTerm.literal(Integer.toString(priority))));
        triples.add(new KnowledgeTriple(
                id, SynthesisVocabulary.LATENCY_MILLIS, KnowledgeTerm.literal(Long.toString(latency))));
        triples.add(new KnowledgeTriple(
                id, SynthesisVocabulary.ENERGY_MILLIJOULES, KnowledgeTerm.literal(Long.toString(energy))));
        return triples;
    }
}
