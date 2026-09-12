import React, { useMemo, useState, useEffect } from 'react';
import { ReactFlow, Background, Controls, Node, Edge, MarkerType } from '@xyflow/react';
import '@xyflow/react/dist/style.css';
import { MncModel } from '../../types/models';
import { nodeTypes } from './FlowNodeTypes';
import { GitMerge, Layers, Info } from 'lucide-react';
import { useEditorStore } from '../../stores/editorStore';

interface Props {
  model: MncModel;
  mode?: 'statemachine' | 'blockdiagram' | string;
}

interface StateInfo {
  name: string;
  isStart: boolean;
  isEnd: boolean;
  isReady: boolean;
  isAlarm: boolean;
  role: string;
  commands: string[];
  events: string[];
}

interface TransitionInfo {
  from: string;
  to: string;
  label?: string;
  type?: string;
}

export const MncFlowViewer: React.FC<Props> = ({ model, mode }) => {
  const { setSelectedNodeId } = useEditorStore();
  const [view, setView] = useState<'state' | 'block'>(
    mode === 'blockdiagram' ? 'block' : 'state'
  );
  const [selectedState, setSelectedState] = useState<StateInfo | null>(null);

  useEffect(() => {
    if (mode === 'blockdiagram') setView('block');
    else if (mode === 'statemachine') setView('state');
  }, [mode]);

  // Extract ALL states and transitions comprehensively from any thesis model representation
  const { statesList, transitionsList } = useMemo(() => {
    const rawStatesSet = new Set<string>();
    const iface = (model?.interface_description || (model as any)?.systems?.[0] || {}) as any;
    const ctrl = (model?.control_node || (model as any)?.controlNode || {}) as any;

    // 1. Collect from operating_states (array of objects, array of strings, or utility object)
    const opStatesData = iface?.operating_states || iface?.operatingStates || iface?.operatingStatesUtility;
    if (Array.isArray(opStatesData)) {
      opStatesData.forEach((s: any) => {
        const name = typeof s === 'string' ? s : s?.name;
        if (name) rawStatesSet.add(name);
      });
    } else if (opStatesData && typeof opStatesData === 'object') {
      const nested = (opStatesData as any).operatingStates || (opStatesData as any).states || [];
      if (Array.isArray(nested)) {
        nested.forEach((s: any) => {
          const name = typeof s === 'string' ? s : s?.name;
          if (name) rawStatesSet.add(name);
        });
      }
    }

    // 2. Collect from control node operating_states
    const ctrlStates = ctrl?.operating_states || ctrl?.operatingStates || [];
    if (Array.isArray(ctrlStates)) {
      ctrlStates.forEach((s: any) => {
        const name = typeof s === 'string' ? s : s?.name;
        if (name) rawStatesSet.add(name);
      });
    }

    // 3. Collect from actions
    const ctrlActions = ctrl?.actions || [];
    if (Array.isArray(ctrlActions)) {
      ctrlActions.forEach((a: any) => {
        if (a?.name) rawStatesSet.add(a.name);
      });
    }

    // 4. Collect from transitions in control node
    const transitions: TransitionInfo[] = [];
    const ctrlTransitions = ctrl?.transitions || [];
    if (Array.isArray(ctrlTransitions)) {
      ctrlTransitions.forEach((t: any) => {
        const from = t?.currentState || t?.from;
        const to = t?.nextState || t?.to;
        const cond = t?.condition;
        if (from && to) {
          rawStatesSet.add(from);
          rawStatesSet.add(to);
          transitions.push({ from, to, label: cond || undefined });
        }
      });
    }

    // 5. Collect from CommandResponseBlocks and ResponseBlocks
    const crbs = ctrl?.command_response_blocks || ctrl?.commandResponseBlocks || [];
    if (Array.isArray(crbs)) {
      crbs.forEach((crb: any) => {
        const cmdName = crb?.command || crb?.command_ref || crb?.name;
        const transitionStates = crb?.action?.transitionStates || crb?.action?.transition_states || [];
        if (Array.isArray(transitionStates)) {
          transitionStates.forEach((ts: any) => {
            if (typeof ts === 'string' && ts.includes('=>')) {
              const [source, target] = ts.split('=>').map((s: string) => s.trim().replace(/^currentState\s*/, '').replace(/^nextState\s*/, ''));
              if (source && target) {
                rawStatesSet.add(source);
                rawStatesSet.add(target);
                transitions.push({ from: source, to: target, label: cmdName ? `cmd: ${cmdName}` : undefined });
              }
            } else if (typeof ts === 'object' && ts) {
              const source = ts.currentState || ts.from;
              const target = ts.nextState || ts.to;
              if (source && target) {
                rawStatesSet.add(source);
                rawStatesSet.add(target);
                transitions.push({ from: source, to: target, label: cmdName ? `cmd: ${cmdName}` : undefined });
              }
            }
          });
        }

        const rbs = crb?.responseBlock || crb?.response_blocks || [];
        if (Array.isArray(rbs)) {
          rbs.forEach((rb: any) => {
            const respName = rb?.response || rb?.response_ref;
            const rbTransitions = rb?.action?.transitionStates || rb?.onSuccessAction?.transitionStates || [];
            if (Array.isArray(rbTransitions)) {
              rbTransitions.forEach((ts: any) => {
                const source = ts?.currentState || ts?.from;
                const target = ts?.nextState || ts?.to;
                if (source && target) {
                  rawStatesSet.add(source);
                  rawStatesSet.add(target);
                  transitions.push({ from: source, to: target, label: respName ? `res: ${respName}` : undefined });
                }
              });
            }
          });
        }
      });
    }

    // Always guarantee INITIALIZED and READY states from thesis algorithm
    if (!rawStatesSet.has('INITIALIZED')) rawStatesSet.add('INITIALIZED');
    if (!rawStatesSet.has('READY')) rawStatesSet.add('READY');

    // If no lifecycle transition exists between INITIALIZED and READY, add standard INIT transition
    const hasInitTransition = transitions.some(t => t.from === 'INITIALIZED' && t.to === 'READY');
    if (!hasInitTransition) {
      transitions.unshift({ from: 'INITIALIZED', to: 'READY', label: 'cmd: INIT' });
    }

    // Extract start and end states
    const startStates = (iface?.operatingStatesUtility?.startStates || ['INITIALIZED']) as string[];
    const endStates = (iface?.operatingStatesUtility?.endStates || []) as string[];

    // Classify each state
    const states: StateInfo[] = Array.from(rawStatesSet).map((name) => {
      const isStart = startStates.includes(name) || name === 'INITIALIZED';
      const isReady = name === 'READY';
      const isAlarm = name.toLowerCase().includes('abort') || name.toLowerCase().includes('error') || name.toLowerCase().includes('fault');
      const isEnd = endStates.includes(name) || name.toLowerCase().includes('status') || name.toLowerCase().includes('grant') || name.toLowerCase().includes('complete');
      
      let role = 'Activity State';
      if (isStart) role = 'Start State';
      else if (isReady) role = 'Operational';
      else if (isAlarm) role = 'Safety Alarm';
      else if (isEnd) role = 'Result State';

      // Correlate with commands/events if applicable
      const cmds = (iface?.commands || []).filter((c: any) => c?.name?.toLowerCase() === name.toLowerCase()).map((c: any) => c.name);
      const evts = (iface?.events || []).filter((e: any) => e?.name?.toLowerCase() === name.toLowerCase()).map((e: any) => e.name);

      return {
        name,
        isStart,
        isEnd,
        isReady,
        isAlarm,
        role,
        commands: cmds,
        events: evts
      };
    });

    // If READY has no outgoing transition to the first activity, link it smoothly
    const activityStates = states.filter(s => !s.isStart && !s.isReady && !s.isAlarm);
    if (activityStates.length > 0 && !transitions.some(t => t.from === 'READY')) {
      transitions.push({ from: 'READY', to: activityStates[0].name, label: 'cmd: START' });
    }

    return { statesList: states, transitionsList: transitions };
  }, [model]);

  // Compute Layout Nodes & Edges
  const stateNodesEdges = useMemo(() => {
    const nodes: Node[] = [];
    const edges: Edge[] = [];

    // Categorize states into layout columns
    const startState = statesList.find(s => s.name === 'INITIALIZED') || statesList.find(s => s.isStart);
    const readyState = statesList.find(s => s.name === 'READY');
    const alarmStates = statesList.filter(s => s.isAlarm);
    const endStates = statesList.filter(s => s.isEnd && !s.isStart && !s.isReady && !s.isAlarm);
    const middleStates = statesList.filter(s => s !== startState && s !== readyState && !alarmStates.includes(s) && !endStates.includes(s));

    let col = 0;
    const placedNodes = new Map<string, { x: number; y: number }>();

    // Col 0: Start State (INITIALIZED)
    if (startState) {
      const pos = { x: 60, y: 180 };
      placedNodes.set(startState.name, pos);
      nodes.push({
        id: `state-${startState.name}`,
        type: 'operatingState',
        position: pos,
        data: { label: startState.name, isStart: true, role: startState.role }
      });
      col++;
    }

    // Col 1: Operational Standby (READY)
    if (readyState && readyState !== startState) {
      const pos = { x: 60 + col * 260, y: 180 };
      placedNodes.set(readyState.name, pos);
      nodes.push({
        id: `state-${readyState.name}`,
        type: 'operatingState',
        position: pos,
        data: { label: readyState.name, isReady: true, role: readyState.role }
      });
      col++;
    }

    // Col 2..N: Operational Workflow Activities
    const actXStart = 60 + col * 260;
    middleStates.forEach((st, idx) => {
      // Offset vertically if branching occurs, else step horizontally
      const x = actXStart + Math.floor(idx / 2) * 240;
      const y = (idx % 2 === 0) ? 180 : 320;
      placedNodes.set(st.name, { x, y });
      nodes.push({
        id: `state-${st.name}`,
        type: 'operatingState',
        position: { x, y },
        data: { label: st.name, role: st.role }
      });
    });

    if (middleStates.length > 0) {
      col += Math.ceil(middleStates.length / 2);
    }

    // Col N+1: End / Result States
    const endX = actXStart + (middleStates.length > 0 ? Math.ceil(middleStates.length / 2) * 240 : 260);
    endStates.forEach((st, idx) => {
      const x = endX;
      const y = 140 + idx * 110;
      placedNodes.set(st.name, { x, y });
      nodes.push({
        id: `state-${st.name}`,
        type: 'operatingState',
        position: { x, y },
        data: { label: st.name, isEnd: true, role: st.role }
      });
    });

    // Col Safety: Alarm States (e.g. Aborted) placed at bottom
    alarmStates.forEach((st, idx) => {
      const x = 320 + idx * 240;
      const y = 440;
      placedNodes.set(st.name, { x, y });
      nodes.push({
        id: `state-${st.name}`,
        type: 'operatingState',
        position: { x, y },
        data: { label: st.name, isAlarm: true, role: st.role }
      });
    });

    // Generate directed smooth edges with arrowheads
    transitionsList.forEach((t, idx) => {
      const sourcePos = placedNodes.get(t.from);
      const targetPos = placedNodes.get(t.to);
      if (!sourcePos || !targetPos) return;

      // Determine clean source/target handle routing
      const isForward = targetPos.x > sourcePos.x;
      const isDown = targetPos.y > sourcePos.y;

      edges.push({
        id: `trans-${idx}-${t.from}-${t.to}`,
        source: `state-${t.from}`,
        target: `state-${t.to}`,
        sourceHandle: isForward ? 'out-right' : 'out-bottom',
        targetHandle: isForward ? 'in-left' : (isDown ? 'in-top' : 'in-left'),
        label: t.label,
        type: 'smoothstep',
        animated: t.from === 'INITIALIZED' || t.from === 'READY',
        style: { stroke: t.label?.includes('INIT') ? '#10b981' : t.label?.includes('START') ? '#06b6d4' : '#6366f1', strokeWidth: 2 },
        markerEnd: {
          type: MarkerType.ArrowClosed,
          color: t.label?.includes('INIT') ? '#10b981' : t.label?.includes('START') ? '#06b6d4' : '#6366f1',
          width: 14,
          height: 14
        },
        labelStyle: { fill: '#cbd5e1', fontSize: 10, fontFamily: 'monospace', fontWeight: 600 },
        labelBgStyle: { fill: '#0f172a', fillOpacity: 0.9, rx: 4, stroke: '#334155' }
      });
    });

    return { nodes, edges };
  }, [statesList, transitionsList]);

  // Block Diagram computation
  const blockNodesEdges = useMemo(() => {
    const nodes: Node[] = [];
    const edges: Edge[] = [];
    const ifaceName = model?.interface_description?.name || `${model?.name}_Interface`;
    const ctrlName = model?.control_node?.name || `${model?.name}CN`;

    nodes.push({
      id: 'interface',
      type: 'interface',
      position: { x: 340, y: 40 },
      data: { label: ifaceName }
    });

    nodes.push({
      id: 'control-main',
      type: 'controlNode',
      position: { x: 340, y: 240 },
      data: { label: ctrlName }
    });

    edges.push({
      id: 'e-iface-ctrl',
      source: 'interface',
      target: 'control-main',
      type: 'smoothstep',
      label: 'implements',
      markerEnd: { type: MarkerType.ArrowClosed, color: '#3b82f6' },
      style: { stroke: '#3b82f6', strokeWidth: 2 }
    });

    const childNodes = model?.control_node?.child_nodes || (model?.control_node as any)?.childNodes || [];
    if (childNodes.length > 0) {
      let x = 80;
      childNodes.forEach((child: any, idx: number) => {
        const cName = typeof child === 'string' ? child : child?.name || `SubNode_${idx}`;
        nodes.push({
          id: `child-${cName}`,
          type: 'controlNode',
          position: { x, y: 440 },
          data: { label: cName }
        });
        edges.push({
          id: `e-ctrl-child-${cName}`,
          source: 'control-main',
          target: `child-${cName}`,
          type: 'smoothstep',
          label: 'child node',
          markerEnd: { type: MarkerType.ArrowClosed, color: '#64748b' }
        });
        x += 240;
      });
    }

    return { nodes, edges };
  }, [model]);

  const { nodes, edges } = view === 'state' ? stateNodesEdges : blockNodesEdges;

  const handleNodeClick = (_: any, node: Node) => {
    const stateName = node.id.replace('state-', '');
    const found = statesList.find(s => s.name === stateName);
    if (found) {
      setSelectedState(found);
      setSelectedNodeId(found.name);
    }
  };

  return (
    <div className="w-full h-full flex flex-col bg-[#0b0f19] relative select-none">
      {/* Top Controls & Legend Bar */}
      <div className="flex items-center justify-between px-3 py-2 bg-[#111827]/90 backdrop-blur border-b border-gray-800 z-10">
        <div className="flex items-center gap-2">
          <div className="flex bg-[#1e293b] p-0.5 rounded-lg border border-gray-700">
            <button 
              onClick={() => setView('state')}
              className={`flex items-center gap-1.5 px-3 py-1 rounded-md text-xs font-semibold transition-all ${
                view === 'state' ? 'bg-indigo-600 text-white shadow' : 'text-gray-400 hover:text-white'
              }`}
            >
              <GitMerge className="w-3.5 h-3.5" />
              <span>State Machine ({statesList.length} States)</span>
            </button>
            <button 
              onClick={() => setView('block')}
              className={`flex items-center gap-1.5 px-3 py-1 rounded-md text-xs font-semibold transition-all ${
                view === 'block' ? 'bg-indigo-600 text-white shadow' : 'text-gray-400 hover:text-white'
              }`}
            >
              <Layers className="w-3.5 h-3.5" />
              <span>Block Diagram</span>
            </button>
          </div>

          <div className="hidden md:flex items-center gap-3 text-[11px] text-gray-400 pl-4 border-l border-gray-800">
            <span className="flex items-center gap-1">
              <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 inline-block"></span> Start
            </span>
            <span className="flex items-center gap-1">
              <span className="w-2.5 h-2.5 rounded-full bg-cyan-500 inline-block"></span> Ready
            </span>
            <span className="flex items-center gap-1">
              <span className="w-2.5 h-2.5 rounded-full bg-indigo-500 inline-block"></span> Activity
            </span>
            <span className="flex items-center gap-1">
              <span className="w-2.5 h-2.5 rounded-full bg-blue-500 inline-block"></span> Result
            </span>
            <span className="flex items-center gap-1">
              <span className="w-2.5 h-2.5 rounded-full bg-rose-500 inline-block"></span> Alarm
            </span>
          </div>
        </div>

        <div className="flex items-center gap-2">
          <span className="text-xs text-gray-500 font-mono">
            {transitionsList.length} Transitions
          </span>
        </div>
      </div>

      {/* Canvas */}
      <div className="flex-1 relative">
        <ReactFlow
          nodes={nodes}
          edges={edges}
          nodeTypes={nodeTypes}
          onNodeClick={handleNodeClick}
          onPaneClick={() => {
            setSelectedState(null);
            setSelectedNodeId(null);
          }}
          fitView
          colorMode="dark"
        >
          <Background gap={20} color="#1e293b" />
          <Controls className="!bg-[#1e293b] !border-gray-700 !text-gray-200" />
        </ReactFlow>

        {/* Selected State Inspector Drawer */}
        {selectedState && (
          <div className="absolute top-4 right-4 z-20 w-72 bg-[#0f172a]/95 backdrop-blur-md border border-gray-700 rounded-xl p-4 shadow-2xl animate-in fade-in slide-in-from-right-4 duration-200">
            <div className="flex items-center justify-between pb-2 border-b border-gray-800">
              <div className="flex items-center gap-1.5">
                <Info className="w-4 h-4 text-indigo-400" />
                <span className="text-xs font-bold uppercase text-gray-300">State Inspector</span>
              </div>
              <button 
                onClick={() => setSelectedState(null)}
                className="text-gray-400 hover:text-white text-xs px-1.5 py-0.5 rounded hover:bg-gray-800"
              >
                ✕
              </button>
            </div>

            <div className="mt-3 space-y-2.5 text-xs">
              <div>
                <span className="text-gray-400 text-[10px] uppercase font-mono">Name:</span>
                <div className="font-bold text-base text-white font-mono">{selectedState.name}</div>
              </div>

              <div>
                <span className="text-gray-400 text-[10px] uppercase font-mono">Category / Role:</span>
                <div className="text-indigo-300 font-semibold">{selectedState.role}</div>
              </div>

              <div>
                <span className="text-gray-400 text-[10px] uppercase font-mono">Outgoing Transitions:</span>
                <div className="mt-1 space-y-1">
                  {transitionsList.filter(t => t.from === selectedState.name).map((t, i) => (
                    <div key={i} className="flex items-center gap-1.5 text-gray-300 bg-gray-900/60 px-2 py-1 rounded font-mono text-[11px]">
                      <span>➔</span>
                      <span className="font-semibold text-emerald-400">{t.to}</span>
                      {t.label && <span className="text-gray-400 text-[10px]">({t.label})</span>}
                    </div>
                  ))}
                  {transitionsList.filter(t => t.from === selectedState.name).length === 0 && (
                    <span className="text-gray-500 italic text-[11px]">Terminal / End State</span>
                  )}
                </div>
              </div>

              <div>
                <span className="text-gray-400 text-[10px] uppercase font-mono">Incoming Transitions:</span>
                <div className="mt-1 space-y-1">
                  {transitionsList.filter(t => t.to === selectedState.name).map((t, i) => (
                    <div key={i} className="flex items-center gap-1.5 text-gray-300 bg-gray-900/60 px-2 py-1 rounded font-mono text-[11px]">
                      <span>⬅</span>
                      <span className="font-semibold text-cyan-400">{t.from}</span>
                      {t.label && <span className="text-gray-400 text-[10px]">({t.label})</span>}
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
