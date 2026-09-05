import React, { useEffect, useMemo, useCallback } from 'react';
import {
  ReactFlow,
  MiniMap,
  Controls,
  Background,
  useNodesState,
  useEdgesState,
  MarkerType,
  BackgroundVariant
} from '@xyflow/react';
import '@xyflow/react/dist/style.css';
import { useEditorStore } from '../../stores/editorStore';
import { nodeTypes } from './FlowNodeTypes';

const ActivityFlowEditor = () => {
  const activityJson = useEditorStore(state => state.activityJson);
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);

  useEffect(() => {
    try {
      const data = JSON.parse(activityJson);
      if (!data.activities) return;

      const newNodes: any[] = [];
      const newEdges: any[] = [];
      let y = 100;

      newNodes.push({ id: 'start', type: 'start', position: { x: 250, y: 0 }, data: { label: 'Start' } });

      data.activities.forEach((act: any, idx: number) => {
        newNodes.push({
          id: act.name,
          type: 'activity',
          position: { x: 250, y },
          data: { ...act }
        });
        
        if (idx === 0) {
          newEdges.push({
            id: `e-start-${act.name}`,
            source: 'start',
            target: act.name,
            markerEnd: { type: MarkerType.ArrowClosed }
          });
        }
        
        act.transitions?.forEach((t: any) => {
          newEdges.push({
            id: `e-${t.from}-${t.to}`,
            source: t.from,
            target: t.to,
            label: t.condition || '',
            markerEnd: { type: MarkerType.ArrowClosed }
          });
        });
        y += 150;
      });

      newNodes.push({ id: 'end', type: 'end', position: { x: 250, y }, data: { label: 'End' } });
      
      // Auto-connect last nodes without outgoing transitions to 'end'
      const nodesWithOutgoing = new Set(newEdges.map(e => e.source));
      data.activities.forEach((act: any) => {
        if (!nodesWithOutgoing.has(act.name)) {
          newEdges.push({
            id: `e-${act.name}-end`,
            source: act.name,
            target: 'end',
            markerEnd: { type: MarkerType.ArrowClosed }
          });
        }
      });

      setNodes(newNodes);
      setEdges(newEdges);
    } catch (e) {
      // JSON is invalid, keep existing flow
    }
  }, [activityJson, setNodes, setEdges]);

  return (
    <div className="w-full h-full bg-[#0a0a1a]">
      <ReactFlow
        nodes={nodes}
        edges={edges}
        onNodesChange={onNodesChange}
        onEdgesChange={onEdgesChange}
        nodeTypes={nodeTypes}
        fitView
        colorMode="dark"
      >
        <Background variant={BackgroundVariant.Dots} gap={12} size={1} color="#333" />
        <Controls className="bg-surface border-accent fill-white" />
        <MiniMap nodeStrokeColor="#16c79a" nodeColor="#16213e" maskColor="rgba(0,0,0,0.5)" className="bg-surface" />
      </ReactFlow>
    </div>
  );
};

export default ActivityFlowEditor;

