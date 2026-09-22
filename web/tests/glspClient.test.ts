import { describe, expect, it } from "vitest";
import {
  diagramTypeFor,
  glspGatewayUrl,
  workspaceUri
} from "../src/glspClient";

describe("GLSP browser boundary", () => {
  it("maps only graphical KIDE semantic file types", () => {
    expect(diagramTypeFor("systems/workflow.activity")).toBe("kide-activity-diagram");
    expect(diagramTypeFor("control/device.mncspec")).toBe("kide-mnc-diagram");
    expect(diagramTypeFor("data/model.dml")).toBeUndefined();
  });

  it("uses browser-safe workspace URIs without filesystem paths", () => {
    expect(workspaceUri("models/main.activity"))
      .toBe("kide-workspace:/models/main.activity");
    expect(() => workspaceUri("../secret.activity")).toThrow();
    expect(() => workspaceUri("/tmp/secret.activity")).toThrow();
  });

  it("builds the authenticated GLSP endpoint independently from LSP", () => {
    expect(glspGatewayUrl("https://kide.example", "W04-001"))
      .toBe("wss://kide.example/glsp?workspaceId=W04-001");
  });
});
