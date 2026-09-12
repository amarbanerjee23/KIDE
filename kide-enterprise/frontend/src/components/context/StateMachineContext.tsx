import React, { useState } from 'react';
import { useEditorStore } from '../../stores/editorStore';
import { Layers, GitCommit, Radio, AlertCircle, CheckCircle2 } from 'lucide-react';

export type SmContextMode = 'states' | 'transitions' | 'events' | 'issues';

export const StateMachineContext: React.FC = () => {
  const { transformResult, setSelectedNodeId, selectedNodeId } = useEditorStore();
  const [mode, setMode] = useState<SmContextMode>('states');

  const model = transformResult?.model;
  const ifDesc = model?.interface_description;
  const states = ifDesc?.operating_states?.states || [];
  const startStates = ifDesc?.operating_states?.start_states || [];
  const endStates = ifDesc?.operating_states?.end_states || [];

  // Extract transitions from ControlNode action blocks
  const transitions: Array<{ from: string; to: string; command?: string }> = [];
  if (model?.control_node?.command_response_blocks) {
    for (const crb of model.control_node.command_response_blocks) {
      if (crb.action?.transition_states) {
        for (const t of crb.action.transition_states) {
          const match = t.match(/currentState\s+([A-Za-z0-9_]+)\s*=>\s*nextState\s+([A-Za-z0-9_]+)/);
          if (match) {
            transitions.push({ from: match[1], to: match[2], command: crb.command_ref });
          } else {
            transitions.push({ from: t, to: '', command: crb.command_ref });
          }
        }
      }
    }
  }

  const commands = ifDesc?.commands || [];
  const events = ifDesc?.events || [];
  const alarms = ifDesc?.alarms || [];
  const warnings = transformResult?.warnings || [];

  if (!model) {
    return (
      <div className="flex flex-col items-center justify-center p-6 text-center text-gray-500 h-full select-none">
        <Layers className="w-7 h-7 text-gray-600 mb-2" />
        <p className="text-xs font-medium text-gray-300">Model Not Synthesized</p>
        <p className="text-[11px] text-gray-500 mt-1">Run synthesis to inspect supervisory automata states.</p>
      </div>
    );
  }

  return (
    <div className="flex-1 flex flex-col min-h-0 select-none overflow-hidden">
      {/* Submode Selector */}
      <div className="px-2 py-1.5 border-b border-gray-800/80 bg-[#111622]/60 flex items-center gap-1">
        <button
          onClick={() => setMode('states')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition ${
            mode === 'states'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
        >
          <Layers size={12} />
          <span>States ({states.length})</span>
        </button>

        <button
          onClick={() => setMode('transitions')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition ${
            mode === 'transitions'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
        >
          <GitCommit size={12} />
          <span>Transitions</span>
        </button>

        <button
          onClick={() => setMode('events')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition ${
            mode === 'events'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
        >
          <Radio size={12} />
          <span>Signals</span>
        </button>

        <button
          onClick={() => setMode('issues')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition ${
            mode === 'issues'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
        >
          <AlertCircle size={12} />
          <span>Issues</span>
        </button>
      </div>

      {/* Mode Content */}
      <div className="flex-1 overflow-y-auto py-2 px-3 space-y-1.5 text-xs">
        {mode === 'states' && (
          <div className="space-y-1">
            <div className="text-[10px] font-bold text-gray-500 uppercase tracking-wider mb-1">
              Automata Operating States
            </div>
            {states.map((s) => {
              const isStart = startStates.includes(s.name);
              const isEnd = endStates.includes(s.name);
              const isSelected = selectedNodeId === s.name;

              return (
                <div
                  key={s.name}
                  onClick={() => setSelectedNodeId(s.name)}
                  className={`flex items-center justify-between p-2 rounded cursor-pointer transition ${
                    isSelected
                      ? 'bg-blue-600/20 border border-blue-500/80 text-blue-200 font-semibold'
                      : 'bg-gray-900/40 hover:bg-gray-800/60 text-gray-200 border border-transparent'
                  }`}
                >
                  <div className="flex items-center gap-2">
                    <span className={`w-2 h-2 rounded-full ${
                      isStart ? 'bg-emerald-400' : isEnd ? 'bg-rose-400' : 'bg-blue-400'
                    }`} />
                    <span className="font-mono text-xs">{s.name}</span>
                  </div>
                  <div className="flex items-center gap-1">
                    {isStart && <span className="text-[9px] px-1 py-0.2 rounded bg-emerald-950 text-emerald-300 border border-emerald-800/50">START</span>}
                    {isEnd && <span className="text-[9px] px-1 py-0.2 rounded bg-rose-950 text-rose-300 border border-rose-800/50">END</span>}
                  </div>
                </div>
              );
            })}
          </div>
        )}

        {mode === 'transitions' && (
          <div className="space-y-1">
            <div className="text-[10px] font-bold text-gray-500 uppercase tracking-wider mb-1">
              Active State Transitions
            </div>
            {transitions.length === 0 ? (
              <p className="text-[11px] text-gray-500 italic">No direct transitions extracted.</p>
            ) : (
              transitions.map((t, idx) => (
                <div key={idx} className="p-2 rounded bg-gray-900/40 border border-gray-800/80 space-y-1">
                  <div className="flex items-center gap-1.5 font-mono text-xs text-gray-200">
                    <span className="text-blue-300">{t.from}</span>
                    <span className="text-gray-500">→</span>
                    <span className="text-emerald-300">{t.to}</span>
                  </div>
                  {t.command && (
                    <div className="text-[10px] text-gray-500">
                      Trigger: <span className="text-gray-400">{t.command}</span>
                    </div>
                  )}
                </div>
              ))
            )}
          </div>
        )}

        {mode === 'events' && (
          <div className="space-y-3">
            <div>
              <div className="text-[10px] font-bold text-gray-500 uppercase tracking-wider mb-1">
                Commands ({commands.length})
              </div>
              <div className="space-y-1">
                {commands.map(c => (
                  <div key={c.name} className="p-1.5 rounded bg-gray-900/40 text-gray-300 font-mono text-[11px]">
                    {c.async ? 'async ' : ''}{c.name}
                  </div>
                ))}
              </div>
            </div>

            <div>
              <div className="text-[10px] font-bold text-gray-500 uppercase tracking-wider mb-1">
                Published Events ({events.length})
              </div>
              <div className="space-y-1">
                {events.map(e => (
                  <div key={e.name} className="p-1.5 rounded bg-gray-900/40 text-gray-300 font-mono text-[11px]">
                    {e.name}
                  </div>
                ))}
              </div>
            </div>

            <div>
              <div className="text-[10px] font-bold text-gray-500 uppercase tracking-wider mb-1">
                Alarms ({alarms.length})
              </div>
              <div className="space-y-1">
                {alarms.map(a => (
                  <div key={a.name} className="p-1.5 rounded bg-rose-950/20 text-rose-300 font-mono text-[11px] border border-rose-900/30">
                    {a.name} (L{a.level})
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {mode === 'issues' && (
          <div className="space-y-2">
            {warnings.length === 0 ? (
              <div className="flex flex-col items-center justify-center p-4 text-center text-gray-500">
                <CheckCircle2 className="w-6 h-6 text-emerald-500 mb-1.5" />
                <p className="text-xs text-gray-300">Automata Synthesis Clean</p>
                <p className="text-[11px] text-gray-500 mt-0.5">No supervisory synthesis errors or warnings.</p>
              </div>
            ) : (
              warnings.map((w, idx) => (
                <div key={idx} className="p-2 rounded bg-amber-950/20 border border-amber-900/40 text-amber-300 text-[11px]">
                  {w}
                </div>
              ))
            )}
          </div>
        )}
      </div>
    </div>
  );
};

