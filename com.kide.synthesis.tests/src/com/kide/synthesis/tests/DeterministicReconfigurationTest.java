package com.kide.synthesis.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.kide.synthesis.CapabilityRequirement;
import com.kide.synthesis.DeterministicCapabilityMatcher;
import com.kide.synthesis.DeterministicReconfigurationPlanner;
import com.kide.synthesis.ReconfigurationCause;
import com.kide.synthesis.ReconfigurationStatus;
import com.kide.synthesis.ResourceSelection;
import com.kide.synthesis.StateMigrationPolicy;
import com.kide.synthesis.SynthesisPlan;
import com.kide.synthesis.SynthesisResource;

public class DeterministicReconfigurationTest {
    private final DeterministicCapabilityMatcher matcher =
            new DeterministicCapabilityMatcher();
    private final DeterministicReconfigurationPlanner planner =
            new DeterministicReconfigurationPlanner();

    @Test
    public void vehicleGatePrimaryLossRebindsOnlyAffectedRequirement() {
        List<CapabilityRequirement> requirements = List.of(
                requirement("DetectVehicle", "Sensor"),
                requirement("RaiseGate", "Gate"));
        List<SynthesisResource> initialResources = List.of(
                resource("camera-a", "DetectVehicle", "Sensor", true, 20, 1),
                resource("camera-b", "DetectVehicle", "Sensor", true, 10, 1),
                resource("gate-a", "RaiseGate", "Gate", true, 20, 1));

        SynthesisPlan initial = matcher.match(requirements, initialResources);
        assertTrue(initial.feasible());
        assertEquals("urn:kide:device:camera-a", selected(initial, "activity:DetectVehicle"));
        assertEquals("urn:kide:device:gate-a", selected(initial, "activity:RaiseGate"));

        List<SynthesisResource> afterLoss = List.of(
                resource("camera-a", "DetectVehicle", "Sensor", false, 20, 1),
                resource("camera-b", "DetectVehicle", "Sensor", true, 10, 1),
                resource("gate-a", "RaiseGate", "Gate", true, 20, 1));

        var result = planner.reconfigure(
                initial, requirements, afterLoss, ReconfigurationCause.RESOURCE_LOSS);

        assertEquals(ReconfigurationStatus.RECONFIGURED, result.status());
        assertEquals("urn:kide:device:camera-b",
                selected(result.plan(), "activity:DetectVehicle"));
        assertEquals("urn:kide:device:gate-a",
                selected(result.plan(), "activity:RaiseGate"));
        assertEquals(StateMigrationPolicy.MIGRATE,
                migration(result, "activity:DetectVehicle"));
        assertEquals(StateMigrationPolicy.PRESERVE,
                migration(result, "activity:RaiseGate"));
    }

    @Test
    public void roboticsCapabilityChangeRequiresStateReset() {
        CapabilityRequirement original =
                requirement("MoveArm", "RobotArm");
        SynthesisResource robot = resource(
                "robot-a", "MoveArm", "RobotArm", true, 20, 1);
        SynthesisPlan initial = matcher.match(List.of(original), List.of(robot));

        CapabilityRequirement changed = new CapabilityRequirement(
                "activity:MoveArm",
                "MoveArm",
                "MoveArmPrecision",
                Set.of("RobotArm"),
                Set.of(),
                true);
        SynthesisResource upgraded = resource(
                "robot-a", "MoveArmPrecision", "RobotArm", true, 20, 1);

        var result = planner.reconfigure(
                initial,
                List.of(changed),
                List.of(upgraded),
                ReconfigurationCause.CAPABILITY_CHANGE);

        assertEquals(ReconfigurationStatus.RECONFIGURED, result.status());
        assertEquals("urn:kide:device:robot-a",
                selected(result.plan(), "activity:MoveArm"));
        assertEquals(StateMigrationPolicy.RESET,
                migration(result, "activity:MoveArm"));
    }

    @Test
    public void smartRoomNoReplacementFailsClosedWithSafeFallback() {
        CapabilityRequirement occupancy =
                requirement("Occupancy", "OccupancySensor");
        SynthesisResource sensor = resource(
                "sensor-a", "Occupancy", "OccupancySensor", true, 20, 1);
        SynthesisPlan initial = matcher.match(List.of(occupancy), List.of(sensor));

        var result = planner.reconfigure(
                initial,
                List.of(occupancy),
                List.of(resource(
                        "sensor-a", "Occupancy", "OccupancySensor", false, 20, 1)),
                ReconfigurationCause.RESOURCE_LOSS);

        assertEquals(ReconfigurationStatus.NO_SOLUTION, result.status());
        assertFalse(result.plan().feasible());
        assertTrue(result.plan().selections().isEmpty());
        assertEquals(StateMigrationPolicy.SAFE_FALLBACK,
                migration(result, "activity:Occupancy"));
        assertTrue(result.diagnostics().stream()
                .anyMatch(d -> "NO_FEASIBLE_RESOURCE".equals(d.code())));
    }

    @Test
    public void unchangedResourcesPreservePlanAndFingerprintDeterministically() {
        List<CapabilityRequirement> requirements = List.of(
                requirement("Observe", "Sensor"),
                requirement("Control", "Controller"));
        List<SynthesisResource> resources = List.of(
                resource("camera-a", "Observe", "Sensor", true, 10, 1),
                resource("controller-a", "Control", "Controller", true, 10, 1));
        SynthesisPlan initial = matcher.match(requirements, resources);

        var first = planner.reconfigure(
                initial, requirements, resources, ReconfigurationCause.MANUAL_REPLAN);
        var second = planner.reconfigure(
                initial, requirements, resources, ReconfigurationCause.MANUAL_REPLAN);

        assertEquals(ReconfigurationStatus.UNCHANGED, first.status());
        assertEquals(first.fingerprint(), second.fingerprint());
        assertEquals(first.plan().selections(), second.plan().selections());
        assertTrue(first.migrations().stream()
                .allMatch(m -> m.policy() == StateMigrationPolicy.PRESERVE));
    }

    @Test
    public void capacityReductionPreservesLexicographicallyFirstBinding() {
        CapabilityRequirement a = new CapabilityRequirement(
                "activity:A", "A", "Observe", Set.of("Sensor"), Set.of(), true);
        CapabilityRequirement b = new CapabilityRequirement(
                "activity:B", "B", "Observe", Set.of("Sensor"), Set.of(), true);
        SynthesisResource shared = resource(
                "shared", "Observe", "Sensor", true, 10, 2);
        SynthesisPlan initial = matcher.match(List.of(a, b), List.of(shared));
        assertEquals(2, initial.selections().size());

        SynthesisResource reduced = resource(
                "shared", "Observe", "Sensor", true, 10, 1);
        SynthesisResource replacement = resource(
                "replacement", "Observe", "Sensor", true, 5, 1);

        var result = planner.reconfigure(
                initial,
                List.of(a, b),
                List.of(reduced, replacement),
                ReconfigurationCause.AVAILABILITY_CHANGE);

        assertEquals("urn:kide:device:shared",
                selected(result.plan(), "activity:A"));
        assertEquals("urn:kide:device:replacement",
                selected(result.plan(), "activity:B"));
    }

    private static CapabilityRequirement requirement(
            String activity, String requiredInterface) {
        return new CapabilityRequirement(
                "activity:" + activity,
                activity,
                activity,
                Set.of(requiredInterface),
                Set.of(),
                true);
    }

    private static SynthesisResource resource(
            String id,
            String capability,
            String requiredInterface,
            boolean available,
            int priority,
            int maxBindings) {
        return new SynthesisResource(
                "urn:kide:device:" + id,
                id,
                Set.of(capability),
                Set.of(requiredInterface),
                Set.of(),
                Set.of(),
                available,
                priority,
                5,
                10,
                maxBindings);
    }

    private static String selected(SynthesisPlan plan, String requirementId) {
        return plan.selections().stream()
                .filter(s -> requirementId.equals(s.requirementId()))
                .map(ResourceSelection::resourceId)
                .findFirst()
                .orElseThrow();
    }

    private static StateMigrationPolicy migration(
            com.kide.synthesis.ReconfigurationResult result,
            String requirementId) {
        return result.migrations().stream()
                .filter(m -> requirementId.equals(m.requirementId()))
                .map(m -> m.policy())
                .findFirst()
                .orElseThrow();
    }
}
