import React, { useMemo, useState } from 'react';
import { ReactFlow, Background, Controls, Node, Edge } from '@xyflow/react';
import { MncModel, Action } from '../../types/models';
import { nodeTypes } from './FlowNodeTypes';

interface Props {
  model: MncModel;
}

export const MncFlowViewer: React.FC<Props> = ({ model }) => {
  const [view, setView] = useState<'state' | 'block'>('state');

  const stateNodesEdges = useMemo(() => {
    if (!model.interface_description?.operating_states) return { nodes: [], edges: [] };
    const states = model.interface_description.operating_states;
    
    const nodes: Node[] = [];
    const edges: Edge[] = [];
    
    let x = 100;
    (states.states || []).forEach((state) => {
      const isStart = (states.start_states || []).includes(state.name);
      const isEnd = (states.end_states || []).includes(state.name);
      
      nodes.push({
        id: `state-${state.name}`,
        type: 'operatingState',
        position: { x, y: 200 },
        data: { label: state.name, isStart, isEnd }
      });
      x += 200;
    });

    // Extract transitions from control node actions
    const allActions: Action[] = [];
    model.control_node?.command_response_blocks?.forEach(b => {
      if (b.action) allActions.push(b.action);
      b.response_blocks?.forEach(rb => {
        if (rb.action) allActions.push(rb.action);
      });
    });
    
    allActions.forEach((action, idx) => {
      action.transition_states?.forEach(t => {
        const [source, target] = t.split('=>').map(s => s.trim());
        if (source && target) {
          edges.push({
            id: `trans-${idx}-${source}-${target}`,
            source: `state-${source}`,
            target: `state-${target}`,
            label: 'Transition',
            type: 'smoothstep'
          });
        }
      });
    });
    
    return { nodes, edges };
  }, [model]);

  const blockNodesEdges = useMemo(() => {
    const nodes: Node[] = [];
    const edges: Edge[] = [];
    
    nodes.push({
      id: 'interface',
      type: 'interface',
      position: { x: 300, y: 50 },
      data: { label: model.interface_description.name }
    });

    nodes.push({
      id: 'control-main',
      type: 'controlNode',
      position: { x: 300, y: 250 },
      data: { label: model.control_node.name }
    });

    edges.push({
      id: 'e-iface-ctrl',
      source: 'interface',
      target: 'control-main',
      type: 'step'
    });

    if (model.control_node.child_nodes) {
      let x = 50;
      model.control_node.child_nodes.forEach(child => {
        nodes.push({
          id: `child-${child}`,
          type: 'controlNode',
          position: { x, y: 450 },
          data: { label: child }
        });
        edges.push({
          id: `e-ctrl-child-${child}`,
          source: 'control-main',
          target: `child-${child}`,
          type: 'step'
        });
        x += 250;
      });
    }

    return { nodes, edges };
  }, [model]);

  const { nodes, edges } = view === 'state' ? stateNodesEdges : blockNodesEdges;

  return (
    <div className="w-full h-full flex flex-col bg-[#0a0a1a]">
      <div className="flex gap-2 p-2 bg-surface border-b border-accent">
        <button 
          onClick={() => setView('state')}
          className={`px-3 py-1 rounded text-sm ${view === 'state' ? 'bg-blue-600 text-white' : 'bg-gray-700 text-gray-300'}`}
        >
          State Machine
        </button>
        <button 
          onClick={() => setView('block')}
          className={`px-3 py-1 rounded text-sm ${view === 'block' ? 'bg-blue-600 text-white' : 'bg-gray-700 text-gray-300'}`}
        >
          Block Diagram
        </button>
      </div>
      <div className="flex-1">
        <ReactFlow
          nodes={nodes}
          edges={edges}
          nodeTypes={nodeTypes}
          fitView
          colorMode="dark"
        >
          <Background gap={16} color="#16213e" />
          <Controls />
        </ReactFlow>
      </div>
    </div>
  );
};
