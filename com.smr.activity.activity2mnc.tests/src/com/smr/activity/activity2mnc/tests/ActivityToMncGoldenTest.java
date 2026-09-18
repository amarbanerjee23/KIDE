package com.smr.activity.activity2mnc.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;

import activityDiagramModel.ActivityDiagram;
import activityDiagramModel.ActivityDiagramModelFactory;
import com.smr.activity.activity2mnc.handlers.GenerateMnCDesignFromActivityDiagram;
import mncModel.Alarm;
import mncModel.Command;
import mncModel.ControlNode;
import mncModel.Event;
import mncModel.InterfaceDescription;
import mncModel.Model;
import mncModel.OperatingState;

public class ActivityToMncGoldenTest {

    @Test
    public void minimalActivityDiagramMatchesGoldenMncSnapshot() throws Exception {
        ActivityDiagram activity = ActivityDiagramModelFactory.eINSTANCE.createActivityDiagram();
        activity.setName("GoldenWorkflow");

        Model model = new GenerateMnCDesignFromActivityDiagram()
                .parseActivityDiagramToGenerateAnMncModel(activity);

        assertNotNull(model);
        assertEquals(loadGolden(), snapshot(model));
    }

    private static String snapshot(Model model) {
        InterfaceDescription descriptor = (InterfaceDescription) model.getSystems().get(0);
        ControlNode control = (ControlNode) model.getSystems().get(1);

        StringBuilder out = new StringBuilder();
        line(out, "model.name", model.getName());
        line(out, "systems.count", Integer.toString(model.getSystems().size()));
        line(out, "interface.name", descriptor.getName());
        line(out, "interface.commands", names(descriptor.getCommands(), Command::getName));
        line(out, "interface.events", names(descriptor.getEvents(), Event::getName));
        line(out, "interface.alarms", names(descriptor.getAlarms(), Alarm::getName));
        line(out, "interface.states",
                names(descriptor.getOperatingStatesUtility().getOperatingStates(), OperatingState::getName));
        line(out, "control.name", control.getName());
        line(out, "control.interface", control.getInterfaceDescription().getName());
        line(out, "control.commandBlocks.count", Integer.toString(control.getCommandResponseBlocks().size()));
        line(out, "control.eventBlocks.count", Integer.toString(control.getEventBlocks().size()));
        line(out, "control.alarmBlocks.count", Integer.toString(control.getAlarmBlocks().size()));
        line(out, "control.dataPointBlocks.count", Integer.toString(control.getDataPointBlocks().size()));
        return out.toString();
    }

    private static <T> String names(List<T> values, Function<T, String> name) {
        return values.stream()
                .map(name)
                .sorted()
                .collect(Collectors.joining(","));
    }

    private static void line(StringBuilder out, String key, String value) {
        out.append(key).append('=').append(value).append('\n');
    }

    private static String loadGolden() throws IOException {
        InputStream stream = ActivityToMncGoldenTest.class
                .getResourceAsStream("/golden/activity-to-mnc.snapshot");
        assertNotNull("golden snapshot must be packaged in the test bundle", stream);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n")) + "\n";
        }
    }
}
