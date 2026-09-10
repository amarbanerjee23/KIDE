import React, { useState, useEffect, useRef } from 'react';
import { simulationApi, SimulationStatus, SimulationLog } from '../../api/simulation';
import { 
  Play, RotateCcw, 
  ArrowRight, Radio, CheckCircle, Zap, Loader2, Gauge, Terminal
} from 'lucide-react';

interface LiveRunnerConsoleProps {
  projectId: number;
}

export const LiveRunnerConsole: React.FC<LiveRunnerConsoleProps> = ({ projectId }) => {
  const [status, setStatus] = useState<SimulationStatus | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isExecuting, setIsExecuting] = useState<boolean>(false);
  const [autoRun, setAutoRun] = useState<boolean>(false);
  const autoRunTimerRef = useRef<any>(null);
  const terminalRef = useRef<HTMLDivElement>(null);

  // Initialize or fetch simulation session
  const initSimulation = async (forceReset = false) => {
    setIsLoading(true);
    try {
      const data = await simulationApi.startSimulation(projectId, forceReset);
      setStatus(data);
    } catch (err) {
      console.error('Failed to init simulation:', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (projectId) {
      initSimulation(false);
    }
    return () => {
      if (autoRunTimerRef.current) clearInterval(autoRunTimerRef.current);
    };
  }, [projectId]);

  // Auto-run ticker
  useEffect(() => {
    if (autoRun) {
      autoRunTimerRef.current = setInterval(async () => {
        try {
          const updated = await simulationApi.step(projectId);
          setStatus(updated);
        } catch (err) {
          console.error('Simulation step failed:', err);
          setAutoRun(false);
        }
      }, 1000);
    } else {
      if (autoRunTimerRef.current) clearInterval(autoRunTimerRef.current);
    }
    return () => {
      if (autoRunTimerRef.current) clearInterval(autoRunTimerRef.current);
    };
  }, [autoRun, projectId]);

  // Auto scroll terminal to bottom
  useEffect(() => {
    if (terminalRef.current) {
      terminalRef.current.scrollTop = terminalRef.current.scrollHeight;
    }
  }, [status?.logs]);

  const handleCommand = async (cmd: string) => {
    setIsExecuting(true);
    try {
      const res = await simulationApi.executeCommand(projectId, cmd);
      setStatus(res.session);
    } catch (err) {
      console.error(`Command ${cmd} failed:`, err);
    } finally {
      setIsExecuting(false);
    }
  };

  const handleEvent = async (evt: string) => {
    setIsExecuting(true);
    try {
      const res = await simulationApi.injectEvent(projectId, evt);
      setStatus(res.session);
    } catch (err) {
      console.error(`Event ${evt} injection failed:`, err);
    } finally {
      setIsExecuting(false);
    }
  };

  const handleStep = async () => {
    setIsExecuting(true);
    try {
      const res = await simulationApi.step(projectId);
      setStatus(res);
    } catch (err) {
      console.error('Step tick failed:', err);
    } finally {
      setIsExecuting(false);
    }
  };

  if (isLoading && !status) {
    return (
      <div className="flex flex-col items-center justify-center p-12 text-slate-400 gap-3">
        <Loader2 className="w-6 h-6 animate-spin text-indigo-400" />
        <span className="text-sm">Initializing controller simulation environment...</span>
      </div>
    );
  }

  const currentState = status?.current_state || 'INITIALIZED';
  const states = status?.states || ['INITIALIZED', 'READY', 'RUNNING', 'STOPPED'];
  const commands = status?.commands || ['INIT', 'START', 'STOP'];
  const events = status?.events || ['Started', 'Stopped'];
  const telemetry = status?.telemetry || {};

  return (
    <div className="flex flex-col h-full bg-slate-950 text-slate-100 rounded-lg overflow-hidden border border-slate-800">
      
      {/* Top Banner: State Machine Monitor */}
      <div className="p-4 bg-slate-900 border-b border-slate-800 flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        
        {/* State Indicator */}
        <div className="flex items-center gap-3">
          <div className="relative">
            <span className="flex h-3.5 w-3.5">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
              <span className="relative inline-flex rounded-full h-3.5 w-3.5 bg-emerald-500"></span>
            </span>
          </div>
          <div>
            <div className="text-[10px] uppercase font-mono text-slate-400">Current Operating State</div>
            <div className="text-base font-bold text-white flex items-center gap-2">
              <span className="text-emerald-400 font-mono tracking-wider">{currentState}</span>
              <span className="text-xs text-slate-500 font-normal">({status?.model_name})</span>
            </div>
          </div>
        </div>

        {/* Global Controls */}
        <div className="flex items-center gap-2">
          <button
            onClick={() => setAutoRun(!autoRun)}
            className={`px-3 py-1.5 rounded-lg text-xs font-semibold flex items-center gap-1.5 transition ${
              autoRun
                ? 'bg-amber-500/20 text-amber-300 border border-amber-500/40'
                : 'bg-emerald-600 hover:bg-emerald-500 text-white shadow-lg shadow-emerald-600/20'
            }`}
          >
            <Play className={`w-3.5 h-3.5 ${autoRun ? 'fill-amber-300' : ''}`} />
            {autoRun ? 'Pause Auto-Run' : '▶ Auto Run (1s)'}
          </button>

          <button
            onClick={handleStep}
            disabled={isExecuting || autoRun}
            className="px-2.5 py-1.5 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-lg text-xs font-mono transition border border-slate-700"
            title="Single Simulation Tick"
          >
            Step Tick
          </button>

          <button
            onClick={() => initSimulation(true)}
            disabled={isExecuting}
            className="p-1.5 bg-slate-800 hover:bg-slate-700 text-slate-400 hover:text-white rounded-lg transition border border-slate-700"
            title="Reset Simulation"
          >
            <RotateCcw className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* State Flow Bar */}
      <div className="px-4 py-2 bg-slate-900/60 border-b border-slate-800/80 flex items-center gap-1.5 overflow-x-auto text-[11px]">
        {states.map((st, idx) => {
          const isActive = st === currentState;
          return (
            <React.Fragment key={st}>
              <div className={`px-2.5 py-1 rounded-md font-mono flex items-center gap-1.5 transition ${
                isActive 
                  ? 'bg-indigo-600 text-white font-semibold shadow' 
                  : 'bg-slate-800/60 text-slate-400 border border-slate-700/40'
              }`}>
                {isActive && <CheckCircle className="w-3 h-3 text-emerald-300" />}
                {st}
              </div>
              {idx < states.length - 1 && (
                <ArrowRight className="w-3 h-3 text-slate-600 flex-shrink-0" />
              )}
            </React.Fragment>
          );
        })}
      </div>

      {/* Middle Grid: Commands, Events & Telemetry */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-3 p-3 border-b border-slate-800 bg-slate-950/70">
        
        {/* Fire Commands */}
        <div className="bg-slate-900/80 border border-slate-800 rounded-lg p-3">
          <div className="text-[11px] font-semibold text-slate-300 uppercase tracking-wider mb-2 flex items-center gap-1.5">
            <Zap className="w-3.5 h-3.5 text-indigo-400" />
            Fire Supervisory Commands
          </div>
          <div className="flex flex-wrap gap-1.5">
            {commands.map(cmd => (
              <button
                key={cmd}
                onClick={() => handleCommand(cmd)}
                disabled={isExecuting}
                className="px-2.5 py-1 bg-indigo-950/60 hover:bg-indigo-900 text-indigo-200 border border-indigo-700/50 rounded text-xs font-mono transition"
              >
                {cmd}()
              </button>
            ))}
          </div>
        </div>

        {/* Inject Events */}
        <div className="bg-slate-900/80 border border-slate-800 rounded-lg p-3">
          <div className="text-[11px] font-semibold text-slate-300 uppercase tracking-wider mb-2 flex items-center gap-1.5">
            <Radio className="w-3.5 h-3.5 text-emerald-400" />
            Inject External Events
          </div>
          <div className="flex flex-wrap gap-1.5">
            {events.map(ev => (
              <button
                key={ev}
                onClick={() => handleEvent(ev)}
                disabled={isExecuting}
                className="px-2.5 py-1 bg-emerald-950/50 hover:bg-emerald-900 text-emerald-200 border border-emerald-700/40 rounded text-xs font-mono transition"
              >
                {ev}
              </button>
            ))}
          </div>
        </div>

        {/* Live Telemetry */}
        <div className="bg-slate-900/80 border border-slate-800 rounded-lg p-3">
          <div className="text-[11px] font-semibold text-slate-300 uppercase tracking-wider mb-2 flex items-center gap-1.5">
            <Gauge className="w-3.5 h-3.5 text-amber-400" />
            Telemetry DataPoints
          </div>
          <div className="grid grid-cols-2 gap-2">
            {Object.keys(telemetry).length === 0 ? (
              <span className="text-[11px] text-slate-500 col-span-2">No active telemetry datapoints.</span>
            ) : (
              Object.entries(telemetry).map(([k, v]) => (
                <div key={k} className="bg-slate-950/70 border border-slate-800 rounded px-2 py-1 flex items-center justify-between text-xs font-mono">
                  <span className="text-slate-400 text-[10px] truncate max-w-[80px]">{k}</span>
                  <span className="text-amber-300 font-bold">{v}</span>
                </div>
              ))
            )}
          </div>
        </div>
      </div>

      {/* Terminal View */}
      <div className="flex-1 flex flex-col min-h-0 bg-black/90">
        <div className="px-4 py-1.5 bg-slate-900/90 border-b border-slate-800 flex items-center justify-between text-xs text-slate-400 font-mono">
          <div className="flex items-center gap-2">
            <Terminal className="w-3.5 h-3.5 text-indigo-400" />
            <span>Controller Live Execution Trace</span>
          </div>
          <span className="text-[10px] text-slate-500">{status?.logs?.length || 0} events logged</span>
        </div>

        <div ref={terminalRef} className="flex-1 overflow-y-auto p-4 space-y-1 font-mono text-xs select-text">
          {(!status?.logs || status.logs.length === 0) ? (
            <div className="text-slate-600">Waiting for controller execution events...</div>
          ) : (
            status.logs.map((log: SimulationLog, index: number) => {
              let badgeColor = 'text-slate-400 bg-slate-800/50';
              if (log.level === 'TRANSITION') badgeColor = 'text-cyan-300 bg-cyan-950/60 border border-cyan-700/50';
              if (log.level === 'CMD') badgeColor = 'text-indigo-300 bg-indigo-950/60 border border-indigo-700/50';
              if (log.level === 'EVENT') badgeColor = 'text-emerald-300 bg-emerald-950/60 border border-emerald-700/50';
              if (log.level === 'ALARM') badgeColor = 'text-rose-300 bg-rose-950/60 border border-rose-700/50 font-bold';

              return (
                <div key={index} className="flex items-start gap-2 leading-relaxed hover:bg-slate-900/50 px-1 rounded transition">
                  <span className="text-slate-600 text-[11px] flex-shrink-0">{log.timestamp}</span>
                  <span className={`text-[10px] px-1.5 py-0.2 rounded font-semibold flex-shrink-0 ${badgeColor}`}>
                    [{log.level}]
                  </span>
                  <span className="text-slate-300 flex-1 break-all">{log.message}</span>
                </div>
              );
            })
          )}
        </div>
      </div>
    </div>
  );
};
