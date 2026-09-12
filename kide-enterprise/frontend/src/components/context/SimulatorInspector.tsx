import React, { useState } from 'react';
import { useEditorStore } from '../../stores/editorStore';
import { CollapsibleSection } from './CollapsibleSection';
import { 
  Activity, Send, GitMerge, Clock 
} from 'lucide-react';

export const SimulatorInspector: React.FC = () => {
  const { setActiveView } = useEditorStore();
  const [currentState, setCurrentState] = useState<string>('INITIALIZED');
  const [lastEvent, setLastEvent] = useState<string>('SYSTEM_START');
  const [eventsLog, setEventsLog] = useState<Array<{ time: string; event: string }>>([
    { time: '12:00:00', event: 'Simulator Started' },
    { time: '12:00:01', event: 'Entered INITIALIZED' }
  ]);

  const [variables] = useState<Record<string, string | number>>({
    status: 'ACTIVE',
    cycleCount: 42,
    temperature: 98.6,
    pressure: 2.1,
    pumpSpeed: 1450
  });

  // Next permissible states
  const nextStates = ['READY', 'STANDBY', 'NORMAL', 'EMERGENCY'];

  // Fire a runtime command
  const handleFireCommand = (cmd: string) => {
    const now = new Date().toLocaleTimeString();
    setLastEvent(cmd);
    setEventsLog(prev => [{ time: now, event: `Fired ${cmd}` }, ...prev.slice(0, 7)]);
    if (cmd === 'START') setCurrentState('READY');
    if (cmd === 'STEP') setCurrentState('NORMAL');
    if (cmd === 'FAULT') setCurrentState('EMERGENCY');
    if (cmd === 'RESET') setCurrentState('INITIALIZED');
  };

  return (
    <div className="flex-1 flex flex-col min-h-0 overflow-y-auto select-none">
      {/* Live Runtime Header Banner */}
      <div className="p-3 border-b border-gray-800 bg-[#111622]/40">
        <div className="flex items-center gap-1.5 text-xs font-bold text-emerald-400 uppercase tracking-wider mb-1">
          <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
          <span>Live Controller Runtime</span>
        </div>
        <div className="text-[11px] text-gray-400">
          Supervisory reactive loop execution
        </div>
        <div className="mt-2 text-xs font-mono text-gray-200 font-semibold flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="text-gray-400">State:</span>
            <span className="text-emerald-400 bg-emerald-950/80 px-2 py-0.5 rounded border border-emerald-800/60">
              {currentState}
            </span>
          </div>
          <div className="text-[10px] text-gray-400 font-mono">
            Last: <span className="text-cyan-300">{lastEvent}</span>
          </div>
        </div>
      </div>

      {/* Runtime Telemetry Variables */}
      <CollapsibleSection
        title="Variables & Telemetry"
        count={Object.keys(variables).length}
        icon={<Activity size={13} className="text-cyan-400" />}
        hideIfZero={false}
      >
        <div className="bg-gray-900/80 p-2 rounded-lg border border-gray-800 text-[11px] font-mono space-y-1 text-gray-300 mt-1">
          {Object.entries(variables).map(([k, v]) => (
            <div key={k} className="border-b border-gray-800/60 pb-1 last:border-none flex items-baseline justify-between">
              <span className="text-gray-500">{k}:</span>
              <span className="text-cyan-300 font-medium">{v}</span>
            </div>
          ))}
        </div>
      </CollapsibleSection>

      {/* Fireable Signals / Commands */}
      <CollapsibleSection
        title="Fire Commands"
        icon={<Send size={13} className="text-blue-400" />}
        hideIfZero={false}
      >
        <div className="grid grid-cols-2 gap-1.5 mt-1">
          {['START', 'STEP', 'FAULT', 'RESET'].map(cmd => (
            <button
              key={cmd}
              onClick={() => handleFireCommand(cmd)}
              className="py-1 px-2 rounded bg-gray-800 hover:bg-gray-700 text-gray-200 font-mono text-xs font-semibold border border-gray-700 hover:border-blue-500 transition"
            >
              {cmd}
            </button>
          ))}
        </div>
      </CollapsibleSection>

      {/* Available Transitions */}
      <CollapsibleSection
        title="Available Transitions"
        count={nextStates.length}
        icon={<GitMerge size={13} className="text-rose-400" />}
        hideIfZero={true}
      >
        <div className="space-y-1 mt-1 font-mono text-xs">
          {nextStates.map(st => (
            <button
              key={st}
              onClick={() => setCurrentState(st)}
              className="w-full p-1.5 rounded bg-gray-900 border border-gray-800 text-left text-gray-300 hover:text-white hover:border-blue-500 transition"
            >
              &rarr; {st}
            </button>
          ))}
        </div>
      </CollapsibleSection>

      {/* Recent Event Log */}
      <CollapsibleSection
        title="Recent Events"
        count={eventsLog.length}
        icon={<Clock size={13} className="text-amber-400" />}
        hideIfZero={false}
      >
        <div className="space-y-1 mt-1 font-mono text-[10px]">
          {eventsLog.map((ev, idx) => (
            <div key={idx} className="p-1 rounded bg-gray-900/50 text-gray-400 flex items-center justify-between">
              <span>{ev.event}</span>
              <span className="text-gray-600">{ev.time}</span>
            </div>
          ))}
        </div>
      </CollapsibleSection>

      {/* Actions */}
      <div className="p-3 mt-auto border-t border-gray-800 space-y-1.5">
        <button
          onClick={() => setActiveView('statemachine')}
          className="w-full py-1.5 px-2.5 bg-gray-800 hover:bg-gray-700 text-gray-200 rounded text-xs font-semibold transition border border-gray-700"
        >
          View State Machine Diagram
        </button>
      </div>
    </div>
  );
};
