import React, { useMemo } from 'react';
import { ReactFlow, Background, Controls, MiniMap, Node, Edge, useNodesState, useEdgesState } from '@xyflow/react';
import '@xyflow/react/dist/style.css';
import { ActivityDiagram } from '../../types/models';
import { nodeTypes } from './FlowNodeTypes';
import { useActivityFile, useEditorStore } from '../../stores/editorStore';

interface Props {
  activity?: ActivityDiagram;
}

const emptyDiagram: ActivityDiagram = { name: 'Empty', activities: [] };

const generateLayout = (diagram: ActivityDiagram): { nodes: Node[], edges: Edge[] } => {
  const nodes: Node[] = [];
  const edges: Edge[] = [];
  
  let yPos = 50;
  let prevId = 'start';
  
  nodes.push({
    id: 'start',
    type: 'start',
    position: { x: 250, y: yPos },
    data: { label: 'Start' }
  });
  
  yPos += 100;
  
  (diagram.activities || []).forEach((act, index) => {
    const actId = `act-${index}`;
    
    if (act.condition) {
      nodes.push({
        id: actId,
        type: 'condition',
        position: { x: 200, y: yPos },
        data: { condition: act.condition, label: act.name }
      });
      
      edges.push({
        id: `e-${prevId}-${actId}`,
        source: prevId,
        target: actId,
        type: 'smoothstep'
      });
      
      // Simple layout: True goes right, False goes left
      if (act.condition.true_outcome) {
        const trueNodeId = `${actId}-true`;
        nodes.push({
          id: trueNodeId,
          type: 'activity',
          position: { x: 400, y: yPos + 150 },
          data: { activity: { name: act.condition.true_outcome.outcome }, label: act.condition.true_outcome.outcome }
        });
        edges.push({
          id: `e-${actId}-true`,
          source: actId,
          sourceHandle: 'true',
          target: trueNodeId,
          label: 'True',
          type: 'smoothstep'
        });
      }
      
      if (act.condition.false_outcome) {
        const falseNodeId = `${actId}-false`;
        nodes.push({
          id: falseNodeId,
          type: 'activity',
          position: { x: 50, y: yPos + 150 },
          data: { activity: { name: act.condition.false_outcome.outcome }, label: act.condition.false_outcome.outcome }
        });
        edges.push({
          id: `e-${actId}-false`,
          source: actId,
          sourceHandle: 'false',
          target: falseNodeId,
          label: 'False',
          type: 'smoothstep'
        });
      }
      
      yPos += 300;
      prevId = actId; // Not strictly correct for converging, but good enough for simple visual
    } else {
      nodes.push({
        id: actId,
        type: 'activity',
        position: { x: 200, y: yPos },
        data: { activity: act, label: act.name }
      });
      
      edges.push({
        id: `e-${prevId}-${actId}`,
        source: prevId,
        target: actId,
        type: 'smoothstep'
      });
      
      yPos += 150;
      prevId = actId;
    }
  });
  
  const endId = 'end';
  nodes.push({
    id: endId,
    type: 'end',
    position: { x: 250, y: yPos },
    data: { label: 'End' }
  });
  
  edges.push({
    id: `e-${prevId}-${endId}`,
    source: prevId,
    target: endId,
    type: 'smoothstep'
  });
  
  return { nodes, edges };
};

export const ActivityFlowEditor: React.FC<Props> = ({ activity: propActivity }) => {
  const storeActivity = useActivityFile();
  const { setSelectedNodeId } = useEditorStore();
  const activity = propActivity || storeActivity || emptyDiagram;
  const { nodes: initialNodes, edges: initialEdges } = useMemo(() => generateLayout(activity), [activity]);
  
  const [nodes, setNodes, onNodesChange] = useNodesState(initialNodes);
  const [edges, setEdges, onEdgesChange] = useEdgesState(initialEdges);

  React.useEffect(() => {
    const { nodes: newNodes, edges: newEdges } = generateLayout(activity);
    setNodes(newNodes);
    setEdges(newEdges);
  }, [activity, setNodes, setEdges]);

  const handleNodeClick = (_: any, node: Node) => {
    const name = (node.data as any)?.activity?.name || (node.data as any)?.label;
    if (name && name !== 'Start' && name !== 'End') {
      setSelectedNodeId(name);
    }
  };

  return (
    <div className="w-full h-full bg-[#0a0a1a]">
      <ReactFlow
        nodes={nodes}
        edges={edges}
        onNodesChange={onNodesChange}
        onEdgesChange={onEdgesChange}
        onNodeClick={handleNodeClick}
        onPaneClick={() => setSelectedNodeId(null)}
        nodeTypes={nodeTypes}
        fitView
        colorMode="dark"
      >
        <Background gap={16} color="#16213e" />
        <Controls />
        <MiniMap nodeColor={(n) => {
          if (n.type === 'start') return '#22c55e';
          if (n.type === 'end') return '#ef4444';
          if (n.type === 'condition') return '#eab308';
          return '#3b82f6';
        }} />
      </ReactFlow>
    </div>
  );
};
