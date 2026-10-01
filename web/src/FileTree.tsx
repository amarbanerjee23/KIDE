import { useMemo, useState } from "react";
import type { WorkspaceEntry } from "./types";

interface Props {
  entries: WorkspaceEntry[];
  selectedPath?: string;
  onSelect(entry: WorkspaceEntry): void;
}

interface FolderNode {
  name: string;
  path: string;
  folders: Map<string, FolderNode>;
  files: WorkspaceEntry[];
}

export function FileTree({ entries, selectedPath, onSelect }: Props) {
  const [collapsed, setCollapsed] = useState<Set<string>>(() => new Set());
  const root = useMemo(() => buildTree(entries), [entries]);

  function toggle(path: string) {
    setCollapsed((current) => {
      const next = new Set(current);
      if (next.has(path)) next.delete(path);
      else next.add(path);
      return next;
    });
  }

  return (
    <ul className="file-list file-tree" aria-label="Project files">
      {renderFolder(root, 0, collapsed, toggle, selectedPath, onSelect)}
    </ul>
  );
}

function renderFolder(
  folder: FolderNode,
  depth: number,
  collapsed: Set<string>,
  toggle: (path: string) => void,
  selectedPath: string | undefined,
  onSelect: (entry: WorkspaceEntry) => void
): React.ReactNode[] {
  const rows: React.ReactNode[] = [];

  const folders = [...folder.folders.values()].sort((a, b) =>
    a.name.localeCompare(b.name)
  );
  for (const child of folders) {
    const isCollapsed = collapsed.has(child.path);
    rows.push(
      <li className="file-tree-folder" key={`folder:${child.path}`}>
        <button
          type="button"
          className="file-tree-folder-row"
          style={{ paddingLeft: 6 + depth * 13 }}
          aria-expanded={!isCollapsed}
          onClick={() => toggle(child.path)}
          title={child.path}
        >
          <span className="tree-chevron" aria-hidden="true">
            {isCollapsed ? "›" : "⌄"}
          </span>
          <span className="folder-glyph" aria-hidden="true">▱</span>
          <span className="file-path">{child.name}</span>
        </button>
        {!isCollapsed && (
          <ul className="file-tree-children">
            {renderFolder(
              child,
              depth + 1,
              collapsed,
              toggle,
              selectedPath,
              onSelect
            )}
          </ul>
        )}
      </li>
    );
  }

  for (const entry of [...folder.files].sort((a, b) =>
    basename(a.path).localeCompare(basename(b.path))
  )) {
    rows.push(
      <li className="file-tree-file" key={entry.path}>
        <button
          type="button"
          className={entry.path === selectedPath ? "selected" : ""}
          style={{ paddingLeft: 22 + depth * 13 }}
          onClick={() => onSelect(entry)}
          title={entry.path}
        >
          <span className="file-icon" aria-hidden="true">
            {fileGlyph(entry.path)}
          </span>
          <span className="file-path">{basename(entry.path)}</span>
          {entry.dirty && <span className="tree-dirty" aria-label="Unsaved local change">●</span>}
        </button>
      </li>
    );
  }

  return rows;
}

function buildTree(entries: WorkspaceEntry[]): FolderNode {
  const root: FolderNode = {
    name: "",
    path: "",
    folders: new Map(),
    files: []
  };

  for (const entry of entries) {
    const parts = entry.path.split("/").filter(Boolean);
    if (!parts.length) continue;
    let folder = root;
    let path = "";
    for (const segment of parts.slice(0, -1)) {
      path = path ? `${path}/${segment}` : segment;
      let child = folder.folders.get(segment);
      if (!child) {
        child = {
          name: segment,
          path,
          folders: new Map(),
          files: []
        };
        folder.folders.set(segment, child);
      }
      folder = child;
    }
    folder.files.push(entry);
  }
  return root;
}

function basename(path: string): string {
  const slash = path.lastIndexOf("/");
  return slash < 0 ? path : path.slice(slash + 1);
}

function fileGlyph(path: string): string {
  const lower = path.toLowerCase();
  if (lower.endsWith(".activity")) return "A";
  if (lower.endsWith(".dml")) return "D";
  if (lower.endsWith(".cap")) return "C";
  if (lower.endsWith(".mncspec")) return "M";
  if (lower.endsWith(".op")) return "O";
  if (lower.endsWith(".krl")) return "K";
  if (lower.endsWith(".md")) return "#";
  if (lower.endsWith(".json")) return "{}";
  return "·";
}
