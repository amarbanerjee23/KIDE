import React, { useState } from 'react';
import { useEditorStore } from '../../stores/editorStore';
import { Radio, Cpu, GitCommit, Zap } from 'lucide-react';

export type SimContextMode = 'live' | 'variables' | 'signals' | 'transitions';

export const SimulatorContext: React.FC = () => {
  const { transformResult } = useEditorStore();
  const [mode, setMode] = useState<SimContextMode>('live');

  const model = transformResult?.model;
  const ifDesc = model?.interface_description;
  const states = ifDesc?.operating_states?.states || [];

  return (
    <div className="flex-1 flex flex-col min-h-0 select-none overflow-hidden">
      {/* Submode Selector */}
      <div className="px-2 py-1.5 border-b border-gray-800/80 bg-[#111622]/60 flex items-center gap-1">
        <button
          onClick={() => setMode('live')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition ${
            mode === 'live'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
        >
          <Zap size={12} />
          <span>Live State</span>
        </button>

        <button
          onClick={() => setMode('variables')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition ${
            mode === 'variables'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
        >
          <Cpu size={12} />
          <span>Telemetry</span>
        </button>

        <button
          onClick={() => setMode('signals')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition ${
            mode === 'signals'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
        >
          <Radio size={12} />
          <span>Commands</span>
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
          <span>Next State</span>
        </button>
      </div>

      {/* Mode Content */}
      <div className="flex-1 overflow-y-auto py-2 px-3 space-y-2 text-xs">
        {mode === 'live' && (
          <div className="space-y-3">
            {/* Live State Card */}
            <div className="p-3 rounded-lg bg-emerald-950/20 border border-emerald-800/50 space-y-1">
              <div className="flex items-center justify-between">
                <span className="text-[10px] uppercase font-bold text-emerald-400 tracking-wider">
                  Active Simulation State
                </span>
                <span className="flex items-center gap-1 text-[10px] text-emerald-300 font-medium">
                  <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" /> RUNNING
                </span>
              </div>
              <p className="text-sm font-mono font-bold text-gray-100">
                {states.length > 0 ? states[0].name : 'INITIALIZED'}
              </p>
            </div>

            {/* Quick Metrics */}
            <div className="grid grid-cols-2 gap-2 text-center">
              <div className="p-2 rounded bg-gray-900/60 border border-gray-800">
                <div className="text-[10px] text-gray-500 uppercase">Available States</div>
                <div className="text-sm font-mono font-bold text-blue-400 mt-0.5">{states.length || 8}</div>
              </div>
              <div className="p-2 rounded bg-gray-900/60 border border-gray-800">
                <div className="text-[10px] text-gray-500 uppercase">Cycle Rate</div>
                <div className="text-sm font-mono font-bold text-emerald-400 mt-0.5">10 Hz</div>
              </div>
            </div>
          </div>
        )}

        {mode === 'variables' && (
          <div className="space-y-1.5">
            <div className="text-[10px] font-bold text-gray-500 uppercase tracking-wider mb-1">
              Runtime Telemetry Variables
            </div>
            {[
              { name: 'currentTemperature', value: '25.4 °C', status: 'normal' },
              { name: 'coolingFanSpeed', value: '1200 RPM', status: 'normal' },
              { name: 'valveOpen', value: 'true', status: 'normal' },
              { name: 'emergencyStopTriggered', value: 'false', status: 'nominal' }
            ].map(v => (
              <div key={v.name} className="flex items-center justify-between p-2 rounded bg-gray-900/40 border border-gray-800 font-mono text-[11px]">
                <span className="text-gray-300">{v.name}</span>
                <span className="text-emerald-300 font-semibold">{v.value}</span>
              </div>
            ))}
          </div>
        )}

        {mode === 'signals' && (
          <div className="space-y-1.5">
            <div className="text-[10px] font-bold text-gray-500 uppercase tracking-wider mb-1">
              Fireable Supervisory Commands
            </div>
            {ifDesc?.commands?.map(c => (
              <div key={c.name} className="flex items-center justify-between p-2 rounded bg-gray-900/40 border border-gray-800">
                <span className="font-mono text-gray-200 text-[11px]">{c.name}</span>
                <span className="text-[10px] text-gray-500 font-mono">READY</span>
              </div>
            )) || (
              <p className="text-[11px] text-gray-500 italic">No commands loaded.</p>
            )}
          </div>
        )}

        {mode === 'transitions' && (
          <div className="space-y-1.5">
            <div className="text-[10px] font-bold text-gray-500 uppercase tracking-wider mb-1">
              Possible Next State Transitions
            </div>
            {states.slice(0, 3).map((s, idx) => (
              <div key={idx} className="p-2 rounded bg-gray-900/40 border border-gray-800 flex items-center justify-between">
                <span className="font-mono text-gray-200 text-[11px]">→ {s.name}</span>
                <span className="text-[10px] text-blue-400">Guarded</span>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
