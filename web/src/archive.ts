import { strFromU8, strToU8, unzipSync, zipSync } from "fflate";
import type { WorkspaceEntry } from "./types";

export const MAX_ARCHIVE_BYTES = 10 * 1024 * 1024;
export const MAX_EXPANDED_BYTES = 20 * 1024 * 1024;
export const MAX_FILE_BYTES = 2 * 1024 * 1024;
export const MAX_FILES = 1000;

const TEXT_EXTENSIONS = new Set([
  "activity", "cap", "dml", "json", "krl", "md", "mncspec", "operation",
  "properties", "txt", "xml", "yaml", "yml"
]);

export function importProjectArchive(bytes: Uint8Array): WorkspaceEntry[] {
  if (bytes.byteLength > MAX_ARCHIVE_BYTES) {
    throw new Error("Archive exceeds the 10 MiB compressed upload limit.");
  }

  let expanded = 0;
  let count = 0;
  const unpacked = unzipSync(bytes, {
    filter(file) {
      if (file.name.endsWith("/")) return false;
      count += 1;
      if (count > MAX_FILES) throw new Error("Archive contains too many files.");
      if (file.originalSize > MAX_FILE_BYTES) {
        throw new Error(`Archive file is too large: ${file.name}`);
      }
      expanded += file.originalSize;
      if (expanded > MAX_EXPANDED_BYTES) {
        throw new Error("Archive expands beyond the 20 MiB workspace limit.");
      }
      return true;
    }
  });

  return Object.entries(unpacked)
    .map(([rawPath, value]) => {
      const path = normalizeProjectPath(rawPath);
      return {
        path,
        bytes: value,
        mediaType: mediaTypeFor(path),
        source: "archive" as const,
        dirty: false
      };
    })
    .sort((a, b) => a.path.localeCompare(b.path));
}

export function exportProjectArchive(entries: WorkspaceEntry[]): Uint8Array {
  const archive: Record<string, Uint8Array> = {};
  for (const entry of [...entries].sort((a, b) => a.path.localeCompare(b.path))) {
    archive[normalizeProjectPath(entry.path)] = entry.bytes;
  }
  return zipSync(archive, { level: 6 });
}

export function normalizeProjectPath(raw: string): string {
  if (!raw || raw.length > 512 || raw.includes("\0") || raw.includes("\\")) {
    throw new Error("Archive contains an invalid project path.");
  }
  if (raw.startsWith("/") || /^[A-Za-z]:/.test(raw)) {
    throw new Error("Archive contains an absolute path.");
  }
  const parts = raw.split("/");
  if (parts.some((part) => !part || part === "." || part === "..")) {
    throw new Error("Archive contains a path traversal or empty path segment.");
  }
  if (parts[0] === ".metadata") {
    throw new Error("Eclipse workspace metadata must not be imported as project content.");
  }
  return parts.join("/");
}

export function editableText(entry: WorkspaceEntry): string | null {
  if (!TEXT_EXTENSIONS.has(extensionOf(entry.path))) return null;
  try {
    return strFromU8(entry.bytes);
  } catch {
    return null;
  }
}

export function withText(entry: WorkspaceEntry, text: string): WorkspaceEntry {
  return { ...entry, bytes: strToU8(text), dirty: true };
}

export function mediaTypeFor(path: string): string {
  switch (extensionOf(path)) {
    case "dml": return "text/x-kide-dml";
    case "cap": return "text/x-kide-capability";
    case "mncspec": return "text/x-kide-mnc";
    case "operation": return "text/x-kide-operation";
    case "activity": return "text/x-kide-activity";
    case "krl": return "text/x-kide-krl";
    case "json": return "application/json";
    default: return "text/plain";
  }
}

function extensionOf(path: string): string {
  const name = path.slice(path.lastIndexOf("/") + 1);
  const dot = name.lastIndexOf(".");
  return dot < 0 ? "" : name.slice(dot + 1).toLowerCase();
}
