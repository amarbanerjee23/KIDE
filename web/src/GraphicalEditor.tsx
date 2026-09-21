import { useEffect, useMemo, useRef, useState } from "react";
import type { PointerEvent as ReactPointerEvent } from "react";
import type {
  EdgeTypeHint,
  GModelElementSchema,
  GModelRootSchema,
  Marker,
  ShapeTypeHint
} from "@eclipse-glsp/protocol";
import {
  KideGlspClient,
  diagramTypeFor,
  type GlspState
} from "./glspClient";

interface Props {
  gatewayOrigin: string;
  accessToken: string;
  workspaceId: string;
  path: string;
  onStatus(message: string): void;
  onSaved(): void;
}

interface RenderElement extends GModelElementSchema {
  children?: GModelElementSchema[];
  position?: { x: number; y: number };
  size?: { width: number; height: number };
  sourceId?: string;
  targetId?: string;
  text?: string;
}

interface RenderNode {
  id: string;
  type: string;
  x: number;
  y: number;
  width: number;
  height: number;
  label: string;
}

interface RenderEdge {
  id: string;
  type: string;
  sourceId: string;
  targetId: string;
}

interface DragState {
  id: string;
  dx: number;
  dy: number;
  width: number;
  height: number;
}

const CANVAS_WIDTH = 1400;
const CANVAS_HEIGHT = 820;

export function GraphicalEditor({
  gatewayOrigin,
  accessToken,
  workspaceId,
  path,
  onStatus,
  onSaved
}: Props) {
  const diagramType = diagramTypeFor(path);
  const clientRef = useRef<KideGlspClient>();
  const svgRef = useRef<SVGSVGElement>(null);
  const saveRequested = useRef(false);
  const [snapshot, setSnapshot] = useState<GlspState>({
    shapeHints: [],
    edgeHints: [],
    markers: [],
    dirty: false
  });
  const [selectedId, setSelectedId] = useState<string>();
  const [edgeType, setEdgeType] = useState<string>();
  const [edgeSource, setEdgeSource] = useState<string>();
  const [renameText, setRenameText] = useState("");
  const [drag, setDrag] = useState<DragState>();
  const [transientPositions, setTransientPositions] = useState<
    Record<string, { x: number; y: number }>
  >({});

  useEffect(() => {
    if (!diagramType) return;
    let disposed = false;
    const client = new KideGlspClient(
      gatewayOrigin,
      accessToken,
      workspaceId,
      diagramType,
      path
    );
    clientRef.current = client;
    const unsubscribe = client.subscribe((state) => {
      if (disposed) return;
      setSnapshot(state);
      if (saveRequested.current && !state.dirty) {
        saveRequested.current = false;
        onStatus("Graphical model saved through the shared revision-safe repository.");
        onSaved();
      }
    });

    onStatus("Connecting GLSP graphical model…");
    void client.connect()
      .then(() => {
        if (!disposed) onStatus("Connected · Eclipse GLSP graphical model");
      })
      .catch((error) => {
        if (!disposed) {
          onStatus(error instanceof Error ? error.message : "Graphical service connection failed.");
        }
      });

    return () => {
      disposed = true;
      unsubscribe();
      clientRef.current = undefined;
      void client.dispose();
    };
  }, [accessToken, diagramType, gatewayOrigin, path, workspaceId]);

  useEffect(() => {
    setTransientPositions({});
  }, [snapshot.model]);

  const graph = useMemo(() => collectGraph(snapshot.model), [snapshot.model]);
  const nodes = graph.nodes.map((node) => {
    const position = transientPositions[node.id];
    return position ? { ...node, ...position } : node;
  });
  const selectedNode = nodes.find((node) => node.id === selectedId);
  const selectedMarker = snapshot.markers.filter(
    (marker) => marker.elementId === selectedId
  );

  useEffect(() => {
    setRenameText(selectedNode?.label ?? "");
  }, [selectedNode?.id, selectedNode?.label]);

  if (!diagramType) {
    return <div className="empty-state">This file type has no GLSP diagram.</div>;
  }

  function addNode(hint: ShapeTypeHint) {
    const index = nodes.length;
    clientRef.current?.createNode(hint.elementTypeId, {
      x: 70 + (index % 4) * 220,
      y: 70 + Math.floor(index / 4) * 130
    });
  }

  function chooseEdge(hint: EdgeTypeHint) {
    setEdgeType(hint.elementTypeId);
    setEdgeSource(undefined);
    onStatus(`Select the source and target for ${typeLabel(hint.elementTypeId)}.`);
  }

  function selectNode(node: RenderNode) {
    if (!edgeType) {
      setSelectedId(node.id);
      return;
    }
    if (!edgeSource) {
      setEdgeSource(node.id);
      setSelectedId(node.id);
      onStatus("Source selected. Select a target node.");
      return;
    }
    if (edgeSource === node.id) {
      onStatus("Choose a different target node.");
      return;
    }
    clientRef.current?.createEdge(edgeType, edgeSource, node.id);
    setEdgeType(undefined);
    setEdgeSource(undefined);
    setSelectedId(node.id);
    onStatus("Relationship operation sent to the shared EMF model.");
  }

  function save() {
    saveRequested.current = true;
    clientRef.current?.save();
    onStatus("Saving graphical and semantic changes…");
  }

  function rename() {
    if (!selectedNode || !renameText.trim()) return;
    clientRef.current?.rename(`${selectedNode.id}_label`, renameText.trim());
  }

  function removeSelected() {
    if (!selectedId) return;
    clientRef.current?.deleteElements([selectedId]);
    setSelectedId(undefined);
  }

  function startDrag(event: ReactPointerEvent<SVGGElement>, node: RenderNode) {
    if (edgeType || !shapeHint(snapshot.shapeHints, node.type)?.repositionable) return;
    const point = svgPoint(event);
    setDrag({
      id: node.id,
      dx: point.x - node.x,
      dy: point.y - node.y,
      width: node.width,
      height: node.height
    });
    event.currentTarget.setPointerCapture(event.pointerId);
  }

  function moveDrag(event: ReactPointerEvent<SVGGElement>) {
    if (!drag) return;
    const point = svgPoint(event);
    setTransientPositions((current) => ({
      ...current,
      [drag.id]: {
        x: clamp(point.x - drag.dx, 0, CANVAS_WIDTH - drag.width),
        y: clamp(point.y - drag.dy, 0, CANVAS_HEIGHT - drag.height)
      }
    }));
  }

  function finishDrag() {
    if (!drag) return;
    const position = transientPositions[drag.id];
    if (position) {
      clientRef.current?.changeBounds(
        drag.id,
        position,
        { width: drag.width, height: drag.height }
      );
    }
    setDrag(undefined);
  }

  function svgPoint(event: ReactPointerEvent<SVGElement>) {
    const svg = svgRef.current;
    if (!svg) return { x: 0, y: 0 };
    const rect = svg.getBoundingClientRect();
    return {
      x: (event.clientX - rect.left) * (CANVAS_WIDTH / rect.width),
      y: (event.clientY - rect.top) * (CANVAS_HEIGHT / rect.height)
    };
  }

  return (
    <div className="graphical-shell" data-testid="glsp-editor">
      <div className="graphical-toolbar" aria-label="Diagram tools">
        <div className="tool-group">
          <strong>Create</strong>
          {snapshot.shapeHints.map((hint) => (
            <button
              key={hint.elementTypeId}
              onClick={() => addNode(hint)}
            >
              {typeLabel(hint.elementTypeId)}
            </button>
          ))}
        </div>
        <div className="tool-group">
          <strong>Connect</strong>
          {snapshot.edgeHints.map((hint) => (
            <button
              key={hint.elementTypeId}
              className={edgeType === hint.elementTypeId ? "active-tool" : ""}
              onClick={() => chooseEdge(hint)}
            >
              {typeLabel(hint.elementTypeId)}
            </button>
          ))}
          {edgeType && (
            <button onClick={() => {
              setEdgeType(undefined);
              setEdgeSource(undefined);
            }}>
              Cancel
            </button>
          )}
        </div>
        <div className="tool-group">
          <button onClick={() => clientRef.current?.undo()}>Undo</button>
          <button onClick={() => clientRef.current?.redo()}>Redo</button>
          <button onClick={save} disabled={!snapshot.dirty}>Save diagram</button>
        </div>
      </div>

      <div className="graphical-body">
        <svg
          ref={svgRef}
          className="diagram-canvas"
          viewBox={`0 0 ${CANVAS_WIDTH} ${CANVAS_HEIGHT}`}
          role="img"
          aria-label="KIDE graphical model"
        >
          <defs>
            <marker
              id="diagram-arrow"
              markerWidth="10"
              markerHeight="10"
              refX="9"
              refY="3"
              orient="auto"
              markerUnits="strokeWidth"
            >
              <path d="M0,0 L0,6 L9,3 z" className="diagram-arrow" />
            </marker>
          </defs>
          {graph.edges.map((edge) => {
            const source = nodes.find((node) => node.id === edge.sourceId);
            const target = nodes.find((node) => node.id === edge.targetId);
            if (!source || !target) return null;
            return (
              <g key={edge.id}>
                <line
                  className={selectedId === edge.id ? "diagram-edge selected-edge" : "diagram-edge"}
                  x1={source.x + source.width / 2}
                  y1={source.y + source.height / 2}
                  x2={target.x + target.width / 2}
                  y2={target.y + target.height / 2}
                  markerEnd="url(#diagram-arrow)"
                  onClick={() => setSelectedId(edge.id)}
                />
                <title>{typeLabel(edge.type)}</title>
              </g>
            );
          })}
          {nodes.map((node) => {
            const hasError = snapshot.markers.some(
              (marker) => marker.elementId === node.id && marker.kind === "error"
            );
            return (
              <g
                key={node.id}
                className={[
                  "diagram-node",
                  selectedId === node.id ? "selected-node" : "",
                  hasError ? "error-node" : "",
                  edgeSource === node.id ? "edge-source" : ""
                ].filter(Boolean).join(" ")}
                transform={`translate(${node.x} ${node.y})`}
                onClick={() => selectNode(node)}
                onPointerDown={(event) => startDrag(event, node)}
                onPointerMove={moveDrag}
                onPointerUp={finishDrag}
                onPointerCancel={finishDrag}
              >
                <rect width={node.width} height={node.height} rx="10" />
                <text x={node.width / 2} y={node.height / 2 + 5} textAnchor="middle">
                  {node.label}
                </text>
                <title>{node.label} · {typeLabel(node.type)}</title>
              </g>
            );
          })}
        </svg>

        <aside className="diagram-inspector">
          <h3>Selection</h3>
          {selectedNode ? (
            <>
              <p className="muted">{typeLabel(selectedNode.type)}</p>
              <label>
                Name
                <input
                  aria-label="Diagram element name"
                  value={renameText}
                  onChange={(event) => setRenameText(event.target.value)}
                />
              </label>
              <button disabled={!renameText.trim()} onClick={rename}>Apply name</button>
              <button onClick={removeSelected}>Delete element</button>
            </>
          ) : selectedId ? (
            <>
              <p className="muted">Relationship selected.</p>
              <button onClick={removeSelected}>Delete relationship</button>
            </>
          ) : (
            <p className="muted">Select a node or relationship.</p>
          )}

          <h3>Validation</h3>
          {selectedMarker.length ? (
            <ul className="diagram-markers">
              {selectedMarker.map((marker, index) => (
                <li key={marker.label + index} className={`marker-${marker.kind}`}>
                  <strong>{marker.label}</strong>
                  <span>{marker.description}</span>
                </li>
              ))}
            </ul>
          ) : (
            <p className="muted">
              {snapshot.markers.length
                ? `${snapshot.markers.length} marker(s) elsewhere in the diagram.`
                : "No validation markers."}
            </p>
          )}
          <div className={snapshot.dirty ? "diagram-dirty dirty" : "diagram-dirty"}>
            {snapshot.dirty ? "Unsaved graphical changes" : "Saved"}
          </div>
        </aside>
      </div>
    </div>
  );
}

function collectGraph(root?: GModelRootSchema): {
  nodes: RenderNode[];
  edges: RenderEdge[];
} {
  const nodes: RenderNode[] = [];
  const edges: RenderEdge[] = [];
  let fallback = 0;

  function visit(element: GModelElementSchema) {
    const value = element as RenderElement;
    if (typeof value.sourceId === "string" && typeof value.targetId === "string") {
      edges.push({
        id: value.id,
        type: value.type,
        sourceId: value.sourceId,
        targetId: value.targetId
      });
    } else if (value.position && value.id !== root?.id && typeof value.text !== "string") {
      const size = value.size ?? { width: 180, height: 72 };
      nodes.push({
        id: value.id,
        type: value.type,
        x: value.position.x ?? 50 + (fallback % 4) * 220,
        y: value.position.y ?? 50 + Math.floor(fallback / 4) * 120,
        width: size.width || 180,
        height: size.height || 72,
        label: labelFor(value),
      });
      fallback += 1;
    }
    for (const child of value.children ?? []) visit(child);
  }

  if (root) visit(root);
  return { nodes, edges };
}

function labelFor(element: RenderElement): string {
  const label = (element.children ?? [])
    .map((child) => child as RenderElement)
    .find((child) => typeof child.text === "string");
  return label?.text?.trim() || typeLabel(element.type);
}

function shapeHint(
  hints: ShapeTypeHint[],
  type: string
): ShapeTypeHint | undefined {
  return hints.find((hint) => hint.elementTypeId === type);
}

function typeLabel(type: string): string {
  const raw = type.includes(":") ? type.slice(type.indexOf(":") + 1) : type;
  return raw
    .split(/[-_.]+/)
    .filter(Boolean)
    .map((word) => word.toLowerCase() === "mnc"
      ? "MNC"
      : word.charAt(0).toUpperCase() + word.slice(1))
    .join(" ");
}

function clamp(value: number, minimum: number, maximum: number): number {
  return Math.max(minimum, Math.min(maximum, value));
}

export const __test = { collectGraph, typeLabel };
