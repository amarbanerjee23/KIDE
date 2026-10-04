import type { RuntimeVersion } from "./types";

export const EXPECTED_RUNTIME_COMPATIBILITY = {
  apiVersion: "v1",
  engineeringCompatibilityLevel: 1,
  projectSchemaVersion: 1,
  sharedKernelSchemaVersion: 1
} as const;

export function runtimeCompatibilityIssues(runtime: RuntimeVersion): string[] {
  const issues: string[] = [];
  if (runtime.apiVersion !== EXPECTED_RUNTIME_COMPATIBILITY.apiVersion) {
    issues.push(
      `API ${runtime.apiVersion} != ${EXPECTED_RUNTIME_COMPATIBILITY.apiVersion}`
    );
  }
  if (
    runtime.engineeringCompatibilityLevel !==
    EXPECTED_RUNTIME_COMPATIBILITY.engineeringCompatibilityLevel
  ) {
    issues.push(
      `engineering level ${runtime.engineeringCompatibilityLevel} != ${EXPECTED_RUNTIME_COMPATIBILITY.engineeringCompatibilityLevel}`
    );
  }
  if (
    runtime.projectSchemaVersion !==
    EXPECTED_RUNTIME_COMPATIBILITY.projectSchemaVersion
  ) {
    issues.push(
      `project schema ${runtime.projectSchemaVersion} != ${EXPECTED_RUNTIME_COMPATIBILITY.projectSchemaVersion}`
    );
  }
  if (
    runtime.sharedKernelSchemaVersion !==
    EXPECTED_RUNTIME_COMPATIBILITY.sharedKernelSchemaVersion
  ) {
    issues.push(
      `shared kernel schema ${runtime.sharedKernelSchemaVersion} != ${EXPECTED_RUNTIME_COMPATIBILITY.sharedKernelSchemaVersion}`
    );
  }
  return issues;
}
