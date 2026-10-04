import { describe, expect, it } from "vitest";
import {
  EXPECTED_RUNTIME_COMPATIBILITY,
  runtimeCompatibilityIssues
} from "../src/runtimeCompatibility";
import type { RuntimeVersion } from "../src/types";

const compatible: RuntimeVersion = {
  apiVersion: "v1",
  engineeringCompatibilityLevel: 1,
  projectSchemaVersion: 1,
  sharedKernelSchemaVersion: 1,
  productLine: "1.0",
  productVersion: "1.0.0.test",
  buildId: "test-build"
};

describe("runtime compatibility", () => {
  it("accepts the declared runtime contract", () => {
    expect(runtimeCompatibilityIssues(compatible)).toEqual([]);
    expect(EXPECTED_RUNTIME_COMPATIBILITY).toEqual({
      apiVersion: "v1",
      engineeringCompatibilityLevel: 1,
      projectSchemaVersion: 1,
      sharedKernelSchemaVersion: 1
    });
  });

  it("reports every incompatible engineering boundary", () => {
    expect(runtimeCompatibilityIssues({
      ...compatible,
      apiVersion: "v2",
      engineeringCompatibilityLevel: 2,
      projectSchemaVersion: 3,
      sharedKernelSchemaVersion: 4
    })).toEqual([
      "API v2 != v1",
      "engineering level 2 != 1",
      "project schema 3 != 1",
      "shared kernel schema 4 != 1"
    ]);
  });

  it("does not reject a different diagnostic build identity", () => {
    expect(runtimeCompatibilityIssues({
      ...compatible,
      productVersion: "1.0.99",
      buildId: "different-compatible-build"
    })).toEqual([]);
  });
});
