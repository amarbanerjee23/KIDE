import dagre from '@dagrejs/dagre';
import { 
  forceSimulation, 
  forceLink, 
  forceManyBody, 
  forceCenter, 
  forceCollide 
} from 'd3-force';
import { KnowledgeGraphNode, KnowledgeGraphEdge } from '../api/knowledge';
import { Node, Edge, MarkerType } from '@xyflow/react';

export type GraphLayoutType = 'dagre-lr' | 'dagre-tb' | 'force' | 'radial' | 'layered';
export type GraphDensityMode = 'standard' | 'compact';

export interface LayoutOptions {
  layoutType: GraphLayoutType;
  density: GraphDensityMode;
  filteredNodes: KnowledgeGraphNode[];
  filteredEdges: KnowledgeGraphEdge[];
}

export interface ComputedGraphLayout {
  nodes: Node[];
  edges: Edge[];
}

/**
 * Assigns optimal handles based on relative geometric angle between source and target nodes
 */
function getOptimalHandles(
  sx: number, 
  sy: number, 
  tx: number, 
  ty: number, 
  layoutType: GraphLayoutType
): { sourceHandle: string; targetHandle: string } {
  if (layoutType === 'dagre-lr' || layoutType === 'layered') {
    return { sourceHandle: 'right-out', targetHandle: 'left-in' };
  }
  if (layoutType === 'dagre-tb') {
    return { sourceHandle: 'bottom-out', targetHandle: 'top-in' };
  }

  // For force-directed and radial, dynamically pick nearest handles
  const dx = tx - sx;
  const dy = ty - sy;

  if (Math.abs(dx) >= Math.abs(dy)) {
    return dx >= 0
      ? { sourceHandle: 'right-out', targetHandle: 'left-in' }
      : { sourceHandle: 'left-in', targetHandle: 'right-out' };
  } else {
    return dy >= 0
      ? { sourceHandle: 'bottom-out', targetHandle: 'top-in' }
      : { sourceHandle: 'top-in', targetHandle: 'bottom-out' };
  }
}

/**
 * Computes standard graph layouts: Dagre (Hierarchical LR & TB), Force-Directed, Radial, and Metamodel Layered
 */
export function computeGraphLayout(options: LayoutOptions): ComputedGraphLayout {
  const { layoutType, density, filteredNodes, filteredEdges } = options;

  if (filteredNodes.length === 0) {
    return { nodes: [], edges: [] };
  }

  const nodeWidth = density === 'compact' ? 160 : 210;
  const nodeHeight = density === 'compact' ? 44 : 86;

  const positions: Record<string, { x: number; y: number }> = {};

  // 1. HIERARCHICAL LAYOUT (DAGRE LR & TB)
  if (layoutType === 'dagre-lr' || layoutType === 'dagre-tb') {
    const isLR = layoutType === 'dagre-lr';
    const g = new dagre.graphlib.Graph();
    g.setDefaultEdgeLabel(() => ({}));
    g.setGraph({
      rankdir: isLR ? 'LR' : 'TB',
      nodesep: isLR ? (density === 'compact' ? 35 : 45) : (density === 'compact' ? 40 : 55),
      ranksep: isLR ? (density === 'compact' ? 90 : 120) : (density === 'compact' ? 80 : 100),
      align: 'UL',
      marginx: 60,
      marginy: 60,
    });

    filteredNodes.forEach((node) => {
      g.setNode(node.id, { width: nodeWidth, height: nodeHeight });
    });

    filteredEdges.forEach((edge) => {
      g.setEdge(edge.source, edge.target);
    });

    dagre.layout(g);

    filteredNodes.forEach((node) => {
      const placed = g.node(node.id);
      positions[node.id] = {
        x: placed ? placed.x - nodeWidth / 2 : 0,
        y: placed ? placed.y - nodeHeight / 2 : 0,
      };
    });
  } 
  // 2. FORCE-DIRECTED PHYSICS LAYOUT (D3-FORCE)
  else if (layoutType === 'force') {
    const totalNodes = filteredNodes.length;
    const canvasSize = Math.max(1200, Math.sqrt(totalNodes) * 220);
    const centerX = canvasSize / 2;
    const centerY = canvasSize / 2;

    const simulationNodes = filteredNodes.map((n, i) => {
      // Seed positions on a circle for uniform repulsion start
      const angle = (2 * Math.PI * i) / totalNodes;
      const radius = 100 + Math.random() * 200;
      return {
        id: n.id,
        x: centerX + radius * Math.cos(angle),
        y: centerY + radius * Math.sin(angle),
      };
    });

    const simulationLinks = filteredEdges.map((e) => ({
      source: e.source,
      target: e.target,
    }));

    const simulation = forceSimulation(simulationNodes as any)
      .force(
        'link',
        forceLink(simulationLinks)
          .id((d: any) => d.id)
          .distance(density === 'compact' ? 120 : 160)
          .strength(0.7)
      )
      .force('charge', forceManyBody().strength(density === 'compact' ? -500 : -750))
      .force('center', forceCenter(centerX, centerY))
      .force('collide', forceCollide().radius(density === 'compact' ? 85 : 120))
      .stop();

    // Run 300 simulation ticks synchronously for crisp settled layout
    for (let i = 0; i < 300; ++i) {
      simulation.tick();
    }

    simulationNodes.forEach((sn) => {
      positions[sn.id] = {
        x: Math.round(sn.x - nodeWidth / 2),
        y: Math.round(sn.y - nodeHeight / 2),
      };
    });
  } 
  // 3. CONCENTRIC / RADIAL ORBITAL LAYOUT
  else if (layoutType === 'radial') {
    // Partition nodes into concentric rings based on ontology depth
    const ring0: KnowledgeGraphNode[] = []; // Center (R = 0)
    const ring1: KnowledgeGraphNode[] = []; // Orbit 1 (Devices, Interfaces)
    const ring2: KnowledgeGraphNode[] = []; // Orbit 2 (Capabilities, DataModels)
    const ring3: KnowledgeGraphNode[] = []; // Orbit 3 (Operations, Workflows, Activities)
    const ring4: KnowledgeGraphNode[] = []; // Orbit 4 (Commands, Events, Alarms, DataPoints, Params, States)

    filteredNodes.forEach((node) => {
      if (['domain', 'repository', 'project'].includes(node.type)) {
        ring0.push(node);
      } else if (['device', 'interface'].includes(node.type)) {
        ring1.push(node);
      } else if (['capability', 'datamodel'].includes(node.type)) {
        ring2.push(node);
      } else if (['operation', 'workflow', 'activity'].includes(node.type)) {
        ring3.push(node);
      } else {
        ring4.push(node);
      }
    });

    const centerX = 1200;
    const centerY = 1200;

    // Center nodes
    if (ring0.length === 1) {
      positions[ring0[0].id] = { x: centerX - nodeWidth / 2, y: centerY - nodeHeight / 2 };
    } else {
      ring0.forEach((n, idx) => {
        const theta = (2 * Math.PI * idx) / ring0.length;
        const r = 90;
        positions[n.id] = {
          x: Math.round(centerX + r * Math.cos(theta) - nodeWidth / 2),
          y: Math.round(centerY + r * Math.sin(theta) - nodeHeight / 2),
        };
      });
    }

    const orbits = [
      { nodes: ring1, radius: density === 'compact' ? 320 : 380 },
      { nodes: ring2, radius: density === 'compact' ? 580 : 680 },
      { nodes: ring3, radius: density === 'compact' ? 840 : 980 },
      { nodes: ring4, radius: density === 'compact' ? 1100 : 1280 },
    ];

    orbits.forEach(({ nodes: orbitNodes, radius }, oIdx) => {
      const count = orbitNodes.length;
      if (count === 0) return;
      const angleOffset = (oIdx * Math.PI) / 8; // Stagger orbits
      orbitNodes.forEach((n, idx) => {
        const theta = angleOffset + (2 * Math.PI * idx) / count;
        positions[n.id] = {
          x: Math.round(centerX + radius * Math.cos(theta) - nodeWidth / 2),
          y: Math.round(centerY + radius * Math.sin(theta) - nodeHeight / 2),
        };
      });
    });
  } 
  // 4. METAMODEL LAYERED PIPELINE
  else {
    const layer0 = filteredNodes.filter((n) => ['domain', 'repository', 'project'].includes(n.type));
    const layer1 = filteredNodes.filter((n) => ['device', 'interface'].includes(n.type));
    const layer2 = filteredNodes.filter((n) => ['capability', 'datamodel', 'operation'].includes(n.type));
    const layer3 = filteredNodes.filter((n) => ['workflow', 'activity', 'command', 'event'].includes(n.type));
    const layer4 = filteredNodes.filter((n) => ['operating_state', 'alarm', 'datapoint', 'parameter', 'supervisor'].includes(n.type));
    const layerOther = filteredNodes.filter(
      (n) =>
        ![
          'domain', 'repository', 'project', 'device', 'interface', 'capability', 
          'datamodel', 'operation', 'workflow', 'activity', 'command', 'event', 
          'operating_state', 'alarm', 'datapoint', 'parameter', 'supervisor'
        ].includes(n.type)
    );

    const layers = [layer0, layer1, layer2, layer3, layer4];
    if (layerOther.length > 0) layers.push(layerOther);

    const colSpacing = density === 'compact' ? 240 : 320;
    const rowSpacing = density === 'compact' ? 64 : 110;

    layers.forEach((layerNodes, colIdx) => {
      const x = 60 + colIdx * colSpacing;
      layerNodes.forEach((n, rowIdx) => {
        const y = 60 + rowIdx * rowSpacing;
        positions[n.id] = { x, y };
      });
    });
  }

  // Convert to ReactFlow Nodes
  const reactFlowNodes: Node[] = filteredNodes.map((n) => {
    const pos = positions[n.id] || { x: 0, y: 0 };
    return {
      id: n.id,
      type: 'knowledgeNode',
      position: pos,
      data: { 
        node: n,
        compact: density === 'compact'
      },
    };
  });

  // Convert to ReactFlow Edges with dynamic handle selection & edge curvature
  const filteredNodeIds = new Set(filteredNodes.map((n) => n.id));
  const edgeType = layoutType === 'force' ? 'default' : (layoutType === 'radial' ? 'default' : 'smoothstep');

  const reactFlowEdges: Edge[] = filteredEdges
    .filter((e) => filteredNodeIds.has(e.source) && filteredNodeIds.has(e.target))
    .map((e) => {
      const sPos = positions[e.source] || { x: 0, y: 0 };
      const tPos = positions[e.target] || { x: 0, y: 0 };
      const handles = getOptimalHandles(sPos.x, sPos.y, tPos.x, tPos.y, layoutType);

      return {
        id: e.id,
        source: e.source,
        target: e.target,
        sourceHandle: handles.sourceHandle,
        targetHandle: handles.targetHandle,
        type: edgeType,
        label: e.label,
        style: { stroke: '#475569', strokeWidth: 1.5 },
        labelStyle: { fill: '#94a3b8', fontSize: 10, fontFamily: 'monospace' },
        labelBgStyle: { fill: '#0f172a', fillOpacity: 0.85 },
        markerEnd: {
          type: MarkerType.ArrowClosed,
          color: '#64748b',
          width: 14,
          height: 14,
        },
      };
    });

  return { nodes: reactFlowNodes, edges: reactFlowEdges };
}

