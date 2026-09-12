import React, { useMemo } from 'react';
import { useEditorStore } from '../../stores/editorStore';
import { CollapsibleSection } from './CollapsibleSection';
import { 
  GitMerge, ArrowRight, ArrowLeft, Radio, 
  Sparkles, Play, ShieldAlert, X 
} from 'lucide-react';

export const StateMachineInspector: React.FC = () => {
  const { 
    transformResult, 
    selectedNodeId, 
    setSelectedNodeId, 
    setActiveView 
  } = useEditorStore();

  const model = transformResult?.model;

  // Extract all states and transitions from model
  const { states, transitions } = useMemo(() => {
    if (!model) return { states: [], transitions: [] };

    const iface = (model?.interface_description || (model as any)?.systems?.[0] || {}) as any;
    const ctrl = (model?.control_node || (model as any)?.controlNode || {}) as any;

    const rawStatesSet = new Set<string>();
    const opStates = iface?.operating_states || iface?.operatingStates || iface?.operatingStatesUtility || [];
    if (Array.isArray(opStates)) {
      opStates.forEach((s: any) => {
        const name = typeof s === 'string' ? s : s?.name;
        if (name) rawStatesSet.add(name);
      });
    }

    const transList: Array<{ from: string; to: string; event?: string; guard?: string }> = [];
    const ctrlTransitions = ctrl?.transitions || [];
    if (Array.isArray(ctrlTransitions)) {
      ctrlTransitions.forEach((t: any) => {
        const from = t?.currentState || t?.from;
        const to = t?.nextState || t?.to;
        if (from && to) {
          rawStatesSet.add(from);
          rawStatesSet.add(to);
          transList.push({
            from,
            to,
            event: t?.event || t?.trigger || t?.label,
            guard: t?.condition || t?.guard
          });
        }
      });
    }

    const stateObjects = Array.from(rawStatesSet).map(name => ({
      name,
      isStart: name === 'INITIALIZED' || name.toLowerCase().includes('init'),
      isAlarm: name.toLowerCase().includes('alarm') || name.toLowerCase().includes('emergency'),
    }));

    return { states: stateObjects, transitions: transList };
  }, [model]);

  // Selected state info
  const selectedState = useMemo(() => {
    if (!selectedNodeId) return null;
    return states.find(s => s.name === selectedNodeId);
  }, [selectedNodeId, states]);

  // Transitions for the selected state
  const stateTransitions = useMemo(() => {
    if (!selectedState) return { incoming: [], outgoing: [] };
    const incoming = transitions.filter(t => t.to === selectedState.name);
    const outgoing = transitions.filter(t => t.from === selectedState.name);
    return { incoming, outgoing };
  }, [selectedState, transitions]);

  // ------------------------------------------------------------------
  // STATE C: SPECIFIC OPERATING STATE SELECTED (e.g. "Monitoring")
  // ------------------------------------------------------------------
  if (selectedState) {
    const guards = stateTransitions.outgoing.map(t => t.guard).filter(Boolean);
    const events = stateTransitions.outgoing.map(t => t.event).filter(Boolean);

    return (
      <div className="flex-1 flex flex-col min-h-0 overflow-y-auto select-none">
        {/* Selected State Header */}
        <div className="p-3 border-b border-gray-800 bg-[#111622]/60 flex items-center justify-between">
          <div className="flex items-center gap-1.5 text-xs text-rose-300 font-semibold truncate">
            <GitMerge size={14} className="text-rose-400 shrink-0" />
            <span>{selectedState.name}</span>
          </div>
          <button
            onClick={() => setSelectedNodeId(null)}
            className="text-[11px] text-gray-400 hover:text-gray-200 flex items-center gap-1 hover:underline"
          >
            <X size={12} />
            <span>Deselect</span>
          </button>
        </div>

        {/* Entered From (Incoming Transitions) */}
        <CollapsibleSection
          title="Entered From"
          count={stateTransitions.incoming.length}
          icon={<ArrowLeft size={13} className="text-cyan-400" />}
          hideIfZero={true}
        >
          <div className="space-y-1 mt-1">
            {stateTransitions.incoming.map((t, idx) => (
              <button
                key={idx}
                onClick={() => setSelectedNodeId(t.from)}
                className="w-full p-1.5 rounded bg-gray-900 border border-gray-800 hover:border-blue-500 text-left font-mono text-xs text-cyan-300 flex items-center justify-between transition group"
              >
                <span>{t.from}</span>
                {t.event && <span className="text-[10px] text-gray-500 font-sans">{t.event}</span>}
              </button>
            ))}
          </div>
        </CollapsibleSection>

        {/* Transitions To (Outgoing Transitions) */}
        <CollapsibleSection
          title="Transitions To"
          count={stateTransitions.outgoing.length}
          icon={<ArrowRight size={13} className="text-emerald-400" />}
          hideIfZero={true}
        >
          <div className="space-y-1 mt-1">
            {stateTransitions.outgoing.map((t, idx) => (
              <button
                key={idx}
                onClick={() => setSelectedNodeId(t.to)}
                className="w-full p-1.5 rounded bg-gray-900 border border-gray-800 hover:border-blue-500 text-left font-mono text-xs text-emerald-300 flex items-center justify-between transition group"
              >
                <span>{t.to}</span>
                {t.event && <span className="text-[10px] text-gray-500 font-sans">{t.event}</span>}
              </button>
            ))}
          </div>
        </CollapsibleSection>

        {/* Trigger Events */}
        {events.length > 0 && (
          <CollapsibleSection
            title="On Event / Command"
            count={events.length}
            icon={<Radio size={13} className="text-purple-400" />}
            hideIfZero={true}
          >
            <div className="space-y-1 mt-1">
              {events.map((ev, idx) => (
                <div key={idx} className="p-1.5 rounded bg-purple-950/20 border border-purple-800/40 font-mono text-xs text-purple-300">
                  {ev}
                </div>
              ))}
            </div>
          </CollapsibleSection>
        )}

        {/* Guards & Conditions */}
        {guards.length > 0 && (
          <CollapsibleSection
            title="Guards"
            count={guards.length}
            icon={<ShieldAlert size={13} className="text-amber-400" />}
            hideIfZero={true}
          >
            <div className="space-y-1 mt-1">
              {guards.map((g, idx) => (
                <div key={idx} className="p-1.5 rounded bg-amber-950/20 border border-amber-800/40 font-mono text-xs text-amber-300">
                  {g}
                </div>
              ))}
            </div>
          </CollapsibleSection>
        )}

        {/* Actions */}
        <div className="p-3 mt-auto border-t border-gray-800 space-y-1.5">
          <button
            onClick={() => setSelectedNodeId(null)}
            className="w-full py-1.5 px-2.5 bg-gray-800 hover:bg-gray-700 text-gray-200 rounded text-xs transition"
          >
            Return to Automata Overview
          </button>
        </div>
      </div>
    );
  }

  // ------------------------------------------------------------------
  // STATE A: STATE MACHINE OVERVIEW (Nothing selected)
  // ------------------------------------------------------------------
  return (
    <div className="flex-1 flex flex-col min-h-0 overflow-y-auto select-none">
      {/* Automata Summary Banner */}
      <div className="p-3 border-b border-gray-800 bg-[#111622]/40">
        <div className="text-xs font-bold text-gray-200 uppercase tracking-wider flex items-center gap-1.5 mb-1">
          <GitMerge size={14} className="text-rose-400" />
          <span>Formal Supervisory Automata</span>
        </div>
        <div className="text-[11px] text-gray-400">
          Synthesized discrete-event supervisory control node
        </div>
        <div className="mt-2 text-xs font-mono text-gray-300 font-semibold flex items-center gap-2">
          <span>{states.length} States</span>
          <span className="text-gray-600">&bull;</span>
          <span>{transitions.length} Transitions</span>
        </div>
      </div>

      {/* Operating States List */}
      <CollapsibleSection
        title="Operating States"
        count={states.length}
        icon={<GitMerge size={13} className="text-rose-400" />}
        hideIfZero={false}
      >
        <div className="space-y-1 mt-1">
          {states.length === 0 ? (
            <div className="text-gray-500 text-xs py-1">No operating states generated yet.</div>
          ) : (
            states.map(s => (
              <div
                key={s.name}
                onClick={() => setSelectedNodeId(s.name)}
                className="p-1.5 rounded hover:bg-gray-800 border border-transparent hover:border-gray-700 flex items-center justify-between cursor-pointer transition text-xs group"
              >
                <div className="flex items-center gap-2 min-w-0">
                  <span className={`w-2 h-2 rounded-full shrink-0 ${
                    s.isStart ? 'bg-emerald-400' : s.isAlarm ? 'bg-rose-500' : 'bg-indigo-400'
                  }`} />
                  <span className="font-semibold text-gray-200 group-hover:text-blue-300 truncate">
                    {s.name}
                  </span>
                </div>
                {s.isStart && (
                  <span className="text-[9px] font-mono text-emerald-300 bg-emerald-950/80 px-1 py-0.2 rounded border border-emerald-800/60">
                    START
                  </span>
                )}
              </div>
            ))
          )}
        </div>
      </CollapsibleSection>

      {/* Transitions */}
      <CollapsibleSection
        title="Transitions"
        count={transitions.length}
        icon={<ArrowRight size={13} className="text-blue-400" />}
        hideIfZero={true}
        defaultExpanded={false}
      >
        <div className="space-y-1 mt-1 font-mono text-[11px]">
          {transitions.map((t, idx) => (
            <div key={idx} className="p-1.5 rounded bg-gray-900 border border-gray-800/60 flex items-center justify-between">
              <span className="text-gray-300">{t.from}</span>
              <span className="text-gray-500">&rarr;</span>
              <span className="text-blue-300">{t.to}</span>
            </div>
          ))}
        </div>
      </CollapsibleSection>

      {/* Relevant Actions */}
      <div className="p-3 mt-auto border-t border-gray-800 space-y-1.5">
        <div className="text-[10px] uppercase font-bold text-gray-500 tracking-wider mb-1">
          Actions
        </div>
        <button
          onClick={() => setActiveView('codegen')}
          className="w-full py-1.5 px-2.5 bg-blue-600 hover:bg-blue-500 text-white rounded text-xs font-semibold flex items-center justify-center gap-1.5 transition shadow"
        >
          <Sparkles size={13} />
          <span>Generate Industrial Code</span>
        </button>
        <button
          onClick={() => setActiveView('simulator')}
          className="w-full py-1 px-2.5 bg-gray-800 hover:bg-gray-700 text-gray-200 rounded text-xs font-semibold flex items-center justify-center gap-1.5 transition border border-gray-700"
        >
          <Play size={13} className="text-emerald-400" />
          <span>Launch Live Simulator</span>
        </button>
      </div>
    </div>
  );
};

