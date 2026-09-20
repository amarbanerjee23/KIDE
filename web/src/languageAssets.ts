import raw from "./generated/language-assets.json";

export interface LanguageAsset {
  id: string;
  display_name: string;
  extension: string;
  language_id: string;
  scope_name: string;
  keywords: string[];
}

export const LANGUAGE_ASSETS = raw.languages as LanguageAsset[];

const byExtension = new Map(
  LANGUAGE_ASSETS.map((asset) => [asset.extension, asset])
);
const byLanguageId = new Map(
  LANGUAGE_ASSETS.map((asset) => [asset.language_id, asset])
);

export function languageForPath(path: string): LanguageAsset | undefined {
  const name = path.slice(path.lastIndexOf("/") + 1);
  const dot = name.lastIndexOf(".");
  if (dot < 0) return undefined;
  return byExtension.get(name.slice(dot + 1).toLowerCase());
}

export function languageById(languageId: string): LanguageAsset | undefined {
  return byLanguageId.get(languageId);
}

export function workspaceUri(path: string): string {
  const normalized = normalizeWorkspacePath(path);
  return "kide-workspace:/" + normalized
    .split("/")
    .map((segment) => encodeURIComponent(segment))
    .join("/");
}

export function pathFromWorkspaceUri(uri: string): string {
  const parsed = new URL(uri);
  if (parsed.protocol !== "kide-workspace:" || parsed.host) {
    throw new Error("The language server returned an unsupported workspace URI.");
  }
  const decoded = parsed.pathname
    .split("/")
    .filter(Boolean)
    .map((segment) => decodeURIComponent(segment))
    .join("/");
  return normalizeWorkspacePath(decoded);
}

export function normalizeWorkspacePath(path: string): string {
  if (!path || path.startsWith("/") || path.includes("\\") || path.includes("\0")) {
    throw new Error("Workspace path is invalid.");
  }
  const parts = path.split("/");
  if (parts.some((part) => !part || part === "." || part === "..")) {
    throw new Error("Workspace path contains an unsafe segment.");
  }
  return parts.join("/");
}
