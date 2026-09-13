import React, { useState, useEffect, useRef, useMemo } from 'react';
import { 
  simulationApi, 
  SimulationStatus, 
  SimulationLog, 
  SimulationModeType, 
  ExecutionStateType,
  TelemetryPoint 
} from '../../api/simulation';
import { 
  Play, Pause, RotateCcw, 
  ArrowRight, Radio, CheckCircle, Zap, Loader2, Gauge, Terminal,
  AlertTriangle, Download, Sliders, StepForward, Wifi, WifiOff,
  ShieldAlert, Filter, Bug
} from 'lucide-react';

interface LiveRunnerConsoleProps {
  projectId: number;
}

// Sparkline SVG renderer for live telemetry waveforms
const Sparkline: React.FC<{
  data: number[];
  warnMax?: number;
  faultMax?: number;
  color?: string;
  width?: number;
  height?: number;
}> = ({ data, warnMax = 85, faultMax = 95, color = '#10b981', width = 120, height = 32 }) => {
  if (!data || data.length === 0) {
    return <div className="text-[10px] text-slate-600 font-mono">No data</div>;
  }

  const min = Math.min(...data, 0);
  const max = Math.max(...data, faultMax, 100);
  const range = max - min || 1;

  const points = data
    .map((val, idx) => {
      const x = (idx / (Math.max(data.length - 1, 1))) * width;
      const y = height - ((val - min) / range) * (height - 4) - 2;
      return `${x.toFixed(1)},${y.toFixed(1)}`;
    })
    .join(' ');

  const warnY = height - ((warnMax - min) / range) * (height - 4) - 2;

  return (
    <svg width={width} height={height} className="overflow-visible">
      {/* Warning reference line */}
      {warnMax && (
        <line
          x1={0}
          y1={warnY}
          x2={width}
          y2={warnY}
          stroke="#f59e0b"
          strokeDasharray="2,2"
          strokeWidth={0.8}
          opacity={0.6}
        />
      )}
      {/* Waveform line */}
      <polyline
        fill="none"
        stroke={color}
        strokeWidth={1.8}
        strokeLinecap="round"
        strokeLinejoin="round"
        points={points}
      />
      {/* Latest data point dot */}
      {data.length > 0 && (
        <circle
          cx={width}
          cy={height - ((data[data.length - 1] - min) / range) * (height - 4) - 2}
          r={2.5}
          fill={color}
        />
      )}
    </svg>
  );
};

export const LiveRunnerConsole: React.FC<LiveRunnerConsoleProps> = ({ projectId }) => {
  const [status, setStatus] = useState<SimulationStatus | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isExecuting, setIsExecuting] = useState<boolean>(false);
  const [isWsConnected, setIsWsConnected] = useState<boolean>(false);
  const [showBreakpointsDrawer, setShowBreakpointsDrawer] = useState<boolean>(false);
  const [showFaultDrawer, setShowFaultDrawer] = useState<boolean>(false);
  const [logFilter, setLogFilter] = useState<string>('ALL');

  // Fault injection state
  const [selectedDatapoint, setSelectedDatapoint] = useState<string>('');
  const [overrideValue, setOverrideValue] = useState<number>(100.0);

  // Breakpoints configuration state
  const [activeBreakStates, setActiveBreakStates] = useState<string[]>([]);
  const [breakOnAlarms, setBreakOnAlarms] = useState<boolean>(false);

  const autoRunTimerRef = useRef<any>(null);
  const wsRef = useRef<WebSocket | null>(null);
  const terminalRef = useRef<HTMLDivElement>(null);

  // Fetch initial simulation status
  const initSimulation = async (forceReset = false) => {
    setIsLoading(true);
    try {
      const data = await simulationApi.startSimulation(projectId, forceReset);
      setStatus(data);
      if (data.break_on_states) setActiveBreakStates(data.break_on_states);
      if (typeof data.break_on_alarms === 'boolean') setBreakOnAlarms(data.break_on_alarms);
      if (data.telemetry && Object.keys(data.telemetry).length > 0 && !selectedDatapoint) {
        setSelectedDatapoint(Object.keys(data.telemetry)[0]);
      }
    } catch (err) {
      console.error('Failed to init simulation:', err);
    } finally {
      setIsLoading(false);
    }
  };

  // Set up WebSocket connection for real-time streaming
  useEffect(() => {
    if (!projectId) return;

    initSimulation(false);

    let isSubscribed = true;

    const setupWs = () => {
      try {
        const ws = simulationApi.createSimulationWebSocket(
          projectId,
          (msg) => {
            if (!isSubscribed) return;
            if (msg.type === 'connection_established' || msg.type === 'status_update') {
              setStatus(msg.status);
              setIsWsConnected(true);
            }
          },
          () => {
            if (isSubscribed) setIsWsConnected(false);
          },
          () => {
            if (isSubscribed) {
              setIsWsConnected(false);
              // Retry connect after 3 seconds if unmounted
              setTimeout(() => {
                if (isSubscribed) setupWs();
              }, 3000);
            }
          }
        );

        ws.onopen = () => {
          if (isSubscribed) setIsWsConnected(true);
        };

        wsRef.current = ws;
      } catch (e) {
        console.warn('WebSocket connection not available, fallback to REST polling:', e);
        setIsWsConnected(false);
      }
    };

    setupWs();

    return () => {
      isSubscribed = false;
      if (wsRef.current) {
        wsRef.current.close();
        wsRef.current = null;
      }
      if (autoRunTimerRef.current) clearInterval(autoRunTimerRef.current);
    };
  }, [projectId]);

  // Handle active RUNNING execution state ticker
  useEffect(() => {
    const isRunning = status?.execution_state === 'RUNNING';

    if (isRunning && !status?.is_breakpoint_hit) {
      const hz = status?.tick_rate_hz || 2.0;
      const intervalMs = Math.max(250, Math.round(1000 / hz));

      autoRunTimerRef.current = setInterval(async () => {
        try {
          if (wsRef.current && wsRef.current.readyState === WebSocket.OPEN) {
            wsRef.current.send(JSON.stringify({ type: 'step' }));
          } else {
            const updated = await simulationApi.step(projectId);
            setStatus(updated);
          }
        } catch (err) {
          console.error('Simulation step tick failed:', err);
        }
      }, intervalMs);
    } else {
      if (autoRunTimerRef.current) clearInterval(autoRunTimerRef.current);
    }

    return () => {
      if (autoRunTimerRef.current) clearInterval(autoRunTimerRef.current);
    };
  }, [status?.execution_state, status?.is_breakpoint_hit, status?.tick_rate_hz, projectId]);

  // Auto scroll terminal to bottom
  useEffect(() => {
    if (terminalRef.current) {
      terminalRef.current.scrollTop = terminalRef.current.scrollHeight;
    }
  }, [status?.logs]);

  // Command execution
  const handleCommand = async (cmd: string) => {
    setIsExecuting(true);
    try {
      if (wsRef.current && wsRef.current.readyState === WebSocket.OPEN) {
        wsRef.current.send(JSON.stringify({ type: 'command', command: cmd }));
      } else {
        const res = await simulationApi.executeCommand(projectId, cmd);
        setStatus(res.session);
      }
    } catch (err) {
      console.error(`Command ${cmd} failed:`, err);
    } finally {
      setIsExecuting(false);
    }
  };

  // Event injection
  const handleEvent = async (evt: string) => {
    setIsExecuting(true);
    try {
      if (wsRef.current && wsRef.current.readyState === WebSocket.OPEN) {
        wsRef.current.send(JSON.stringify({ type: 'inject_event', event: evt }));
      } else {
        const res = await simulationApi.injectEvent(projectId, evt);
        setStatus(res.session);
      }
    } catch (err) {
      console.error(`Event ${evt} injection failed:`, err);
    } finally {
      setIsExecuting(false);
    }
  };

  // Single step
  const handleStep = async () => {
    setIsExecuting(true);
    try {
      if (wsRef.current && wsRef.current.readyState === WebSocket.OPEN) {
        wsRef.current.send(JSON.stringify({ type: 'step' }));
      } else {
        const res = await simulationApi.step(projectId);
        setStatus(res);
      }
    } catch (err) {
      console.error('Step tick failed:', err);
    } finally {
      setIsExecuting(false);
    }
  };

  // Execution state toggle (RUNNING <-> PAUSED)
  const toggleExecutionState = async (nextState: ExecutionStateType) => {
    try {
      if (wsRef.current && wsRef.current.readyState === WebSocket.OPEN) {
        wsRef.current.send(JSON.stringify({ type: 'set_execution_state', state: nextState }));
      } else {
        const updated = await simulationApi.setExecutionState(projectId, nextState);
        setStatus(updated);
      }
    } catch (err) {
      console.error('Failed to change execution state:', err);
    }
  };

  // Simulation mode change
  const handleModeChange = async (mode: SimulationModeType) => {
    try {
      if (wsRef.current && wsRef.current.readyState === WebSocket.OPEN) {
        wsRef.current.send(JSON.stringify({ type: 'set_mode', mode }));
      } else {
        const updated = await simulationApi.setMode(projectId, mode);
        setStatus(updated);
      }
    } catch (err) {
      console.error('Failed to update mode:', err);
    }
  };

  // Apply breakpoints
  const handleSaveBreakpoints = async () => {
    try {
      if (wsRef.current && wsRef.current.readyState === WebSocket.OPEN) {
        wsRef.current.send(JSON.stringify({ 
          type: 'set_breakpoints', 
          break_on_states: activeBreakStates, 
          break_on_alarms: breakOnAlarms 
        }));
      } else {
        const updated = await simulationApi.setBreakpoints(projectId, activeBreakStates, breakOnAlarms);
        setStatus(updated);
      }
      setShowBreakpointsDrawer(false);
    } catch (err) {
      console.error('Failed to save breakpoints:', err);
    }
  };

  // Apply telemetry fault override
  const handleApplyOverride = async () => {
    if (!selectedDatapoint) return;
    try {
      if (wsRef.current && wsRef.current.readyState === WebSocket.OPEN) {
        wsRef.current.send(JSON.stringify({ 
          type: 'telemetry_override', 
          datapoint: selectedDatapoint, 
          value: overrideValue 
        }));
      } else {
        const res = await simulationApi.overrideTelemetry(projectId, selectedDatapoint, overrideValue);
        setStatus(res.session);
      }
    } catch (err) {
      console.error('Failed to apply telemetry override:', err);
    }
  };

  // Filtered logs
  const filteredLogs = useMemo(() => {
    if (!status?.logs) return [];
    if (logFilter === 'ALL') return status.logs;
    return status.logs.filter(l => l.level === logFilter);
  }, [status?.logs, logFilter]);

  if (isLoading && !status) {
    return (
      <div className="flex flex-col items-center justify-center p-12 text-slate-400 gap-3">
        <Loader2 className="w-6 h-6 animate-spin text-indigo-400" />
        <span className="text-sm">Initializing controller simulation environment & hardware gateway...</span>
      </div>
    );
  }

  const currentState = status?.current_state || 'INITIALIZED';
  const states = status?.states || ['INITIALIZED', 'READY', 'RUNNING', 'STOPPED'];
  const commands = status?.commands || ['INIT', 'START', 'STOP'];
  const events = status?.events || ['Started', 'Stopped'];
  const telemetry = status?.telemetry || {};
  const currentMode = (status?.mode || 'VIRTUAL_EMULATION') as SimulationModeType;
  const executionState = (status?.execution_state || 'PAUSED') as ExecutionStateType;
  const isBreakpointHit = status?.is_breakpoint_hit || false;

  return (
    <div className="flex flex-col h-full bg-slate-950 text-slate-100 rounded-lg overflow-hidden border border-slate-800">
      
      {/* Top Banner: State Machine Monitor & Live Stream Status */}
      <div className="p-4 bg-slate-900 border-b border-slate-800 flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        
        {/* State Indicator and Mode Badge */}
        <div className="flex items-center gap-4">
          <div className="flex items-center gap-3">
            <div className="relative">
              <span className="flex h-3.5 w-3.5">
                <span className={`animate-ping absolute inline-flex h-full w-full rounded-full opacity-75 ${
                  executionState === 'RUNNING' ? 'bg-emerald-400' : isBreakpointHit ? 'bg-amber-400' : 'bg-slate-500'
                }`}></span>
                <span className={`relative inline-flex rounded-full h-3.5 w-3.5 ${
                  executionState === 'RUNNING' ? 'bg-emerald-500' : isBreakpointHit ? 'bg-amber-500' : 'bg-slate-600'
                }`}></span>
              </span>
            </div>
            <div>
              <div className="text-[10px] uppercase font-mono text-slate-400 flex items-center gap-2">
                <span>Controller State</span>
                <span className="text-slate-600">•</span>
                <span className="text-indigo-400">Step #{status?.step_counter || 0}</span>
              </div>
              <div className="text-base font-bold text-white flex items-center gap-2">
                <span className={`font-mono tracking-wider ${
                  currentState === 'STOPPED' ? 'text-rose-400' : 'text-emerald-400'
                }`}>{currentState}</span>
                <span className="text-xs text-slate-500 font-normal">({status?.model_name})</span>
              </div>
            </div>
          </div>

          {/* WebSocket / Stream Health Badge */}
          <div className="flex items-center gap-2 pl-4 border-l border-slate-800">
            <div className={`px-2 py-1 rounded text-[11px] font-mono flex items-center gap-1.5 ${
              isWsConnected 
                ? 'bg-emerald-950/60 text-emerald-300 border border-emerald-800/60' 
                : 'bg-slate-800/80 text-slate-400 border border-slate-700'
            }`}>
              {isWsConnected ? <Wifi className="w-3 h-3 text-emerald-400" /> : <WifiOff className="w-3 h-3 text-slate-400" />}
              <span>{isWsConnected ? 'Live Stream (WS)' : 'REST Polling'}</span>
            </div>

            {/* Mode Selector */}
            <div className="relative">
              <select
                value={currentMode}
                onChange={(e) => handleModeChange(e.target.value as SimulationModeType)}
                className="bg-slate-800 border border-slate-700 text-slate-200 text-xs rounded px-2.5 py-1 font-mono focus:outline-none focus:border-indigo-500"
              >
                <option value="VIRTUAL_EMULATION">Virtual Emulation</option>
                <option value="HIL_MODBUS_TCP">HIL Modbus-TCP</option>
                <option value="HIL_MQTT">HIL MQTT Gateway</option>
                <option value="HIL_OPC_UA">HIL OPC-UA</option>
                <option value="REPLAY">Replay Execution</option>
              </select>
            </div>
          </div>
        </div>

        {/* Global Controls & Action Buttons */}
        <div className="flex items-center gap-2">
          {/* Breakpoints Drawer Toggle */}
          <button
            onClick={() => setShowBreakpointsDrawer(!showBreakpointsDrawer)}
            className={`px-2.5 py-1.5 rounded-lg text-xs font-mono flex items-center gap-1.5 transition border ${
              activeBreakStates.length > 0 || breakOnAlarms
                ? 'bg-indigo-950 text-indigo-300 border-indigo-700'
                : 'bg-slate-800 hover:bg-slate-700 text-slate-400 border-slate-700'
            }`}
            title="Configure Breakpoints"
          >
            <Bug className="w-3.5 h-3.5 text-indigo-400" />
            <span>Breakpoints ({activeBreakStates.length + (breakOnAlarms ? 1 : 0)})</span>
          </button>

          {/* Fault Injection Drawer Toggle */}
          <button
            onClick={() => setShowFaultDrawer(!showFaultDrawer)}
            className={`px-2.5 py-1.5 rounded-lg text-xs font-mono flex items-center gap-1.5 transition border ${
              showFaultDrawer
                ? 'bg-amber-950 text-amber-300 border-amber-700'
                : 'bg-slate-800 hover:bg-slate-700 text-slate-400 border-slate-700'
            }`}
            title="Fault Injection & Signal Overrides"
          >
            <Sliders className="w-3.5 h-3.5 text-amber-400" />
            <span>Fault Inject</span>
          </button>

          {/* Export Telemetry Trace */}
          <div className="flex items-center rounded-lg overflow-hidden border border-slate-700">
            <button
              onClick={() => simulationApi.downloadTelemetryExport(projectId, 'csv')}
              className="px-2.5 py-1.5 bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-mono flex items-center gap-1 transition"
              title="Download CSV Execution Trace"
            >
              <Download className="w-3.5 h-3.5" />
              <span>CSV</span>
            </button>
            <button
              onClick={() => simulationApi.downloadTelemetryExport(projectId, 'json')}
              className="px-2 py-1.5 bg-slate-800 hover:bg-slate-700 text-slate-400 hover:text-slate-200 text-[11px] font-mono border-l border-slate-700 transition"
              title="Download JSON Execution Trace"
            >
              JSON
            </button>
          </div>

          {/* Play / Pause Toggle */}
          {executionState === 'RUNNING' ? (
            <button
              onClick={() => toggleExecutionState('PAUSED')}
              className="px-3 py-1.5 rounded-lg text-xs font-semibold flex items-center gap-1.5 transition bg-amber-500/20 text-amber-300 border border-amber-500/40 hover:bg-amber-500/30"
            >
              <Pause className="w-3.5 h-3.5 fill-amber-300" />
              <span>Pause</span>
            </button>
          ) : (
            <button
              onClick={() => toggleExecutionState('RUNNING')}
              className="px-3 py-1.5 rounded-lg text-xs font-semibold flex items-center gap-1.5 transition bg-emerald-600 hover:bg-emerald-500 text-white shadow-lg shadow-emerald-600/20"
            >
              <Play className="w-3.5 h-3.5 fill-white" />
              <span>Run (2Hz)</span>
            </button>
          )}

          {/* Single Step Tick */}
          <button
            onClick={handleStep}
            disabled={isExecuting || executionState === 'RUNNING'}
            className="px-2.5 py-1.5 bg-slate-800 hover:bg-slate-700 disabled:opacity-50 text-slate-300 rounded-lg text-xs font-mono transition border border-slate-700 flex items-center gap-1"
            title="Execute Single Simulation Step"
          >
            <StepForward className="w-3.5 h-3.5 text-slate-400" />
            <span>Step</span>
          </button>

          {/* Reset Simulation */}
          <button
            onClick={() => initSimulation(true)}
            disabled={isExecuting}
            className="p-1.5 bg-slate-800 hover:bg-slate-700 text-slate-400 hover:text-white rounded-lg transition border border-slate-700"
            title="Reset Simulation Session"
          >
            <RotateCcw className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Breakpoint Hit Warning Banner */}
      {isBreakpointHit && (
        <div className="bg-amber-950/90 border-b border-amber-600/60 px-4 py-2.5 flex items-center justify-between text-amber-200 text-xs font-mono">
          <div className="flex items-center gap-2">
            <AlertTriangle className="w-4 h-4 text-amber-400 animate-pulse flex-shrink-0" />
            <span className="font-semibold">BREAKPOINT REACHED:</span>
            <span>{status?.breakpoint_reason || 'Target state reached. Execution halted.'}</span>
          </div>
          <button
            onClick={() => toggleExecutionState('RUNNING')}
            className="px-3 py-1 bg-amber-500 hover:bg-amber-400 text-black font-semibold rounded text-xs transition"
          >
            Resume Execution ▶
          </button>
        </div>
      )}

      {/* Breakpoints Configuration Drawer */}
      {showBreakpointsDrawer && (
        <div className="p-3 bg-slate-900 border-b border-slate-800 flex flex-col gap-2">
          <div className="flex items-center justify-between text-xs font-semibold text-indigo-300">
            <div className="flex items-center gap-1.5">
              <Bug className="w-3.5 h-3.5 text-indigo-400" />
              <span>Controller Breakpoint Triggers</span>
            </div>
            <button
              onClick={handleSaveBreakpoints}
              className="px-2.5 py-0.5 bg-indigo-600 hover:bg-indigo-500 text-white rounded text-xs font-mono transition"
            >
              Apply Breakpoints
            </button>
          </div>
          <div className="flex flex-wrap items-center gap-4 text-xs font-mono text-slate-300">
            <label className="flex items-center gap-1.5 cursor-pointer bg-slate-950 px-2 py-1 rounded border border-slate-800">
              <input
                type="checkbox"
                checked={breakOnAlarms}
                onChange={(e) => setBreakOnAlarms(e.target.checked)}
                className="rounded border-slate-700 text-indigo-600 focus:ring-0"
              />
              <span className="text-amber-300 font-semibold">Break on Any Alarm</span>
            </label>

            <span className="text-slate-600">|</span>

            <span className="text-slate-400">Break on States:</span>
            {states.map((st) => (
              <label key={st} className="flex items-center gap-1.5 cursor-pointer bg-slate-950 px-2 py-1 rounded border border-slate-800">
                <input
                  type="checkbox"
                  checked={activeBreakStates.includes(st)}
                  onChange={(e) => {
                    if (e.target.checked) {
                      setActiveBreakStates([...activeBreakStates, st]);
                    } else {
                      setActiveBreakStates(activeBreakStates.filter(s => s !== st));
                    }
                  }}
                  className="rounded border-slate-700 text-indigo-600 focus:ring-0"
                />
                <span>{st}</span>
              </label>
            ))}
          </div>
        </div>
      )}

      {/* Fault Injection Panel */}
      {showFaultDrawer && (
        <div className="p-3 bg-slate-900/90 border-b border-slate-800 flex items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="flex items-center gap-1.5 text-xs font-semibold text-amber-300">
              <Sliders className="w-3.5 h-3.5 text-amber-400" />
              <span>Fault Injection (Signal Override):</span>
            </div>
            <select
              value={selectedDatapoint}
              onChange={(e) => setSelectedDatapoint(e.target.value)}
              className="bg-slate-950 border border-slate-700 text-xs font-mono rounded px-2 py-1 text-slate-200"
            >
              {Object.keys(telemetry).map(dp => (
                <option key={dp} value={dp}>{dp}</option>
              ))}
            </select>
            <div className="flex items-center gap-2">
              <span className="text-xs text-slate-400 font-mono">Value:</span>
              <input
                type="number"
                value={overrideValue}
                onChange={(e) => setOverrideValue(parseFloat(e.target.value) || 0)}
                className="w-20 bg-slate-950 border border-slate-700 rounded px-2 py-1 text-xs font-mono text-amber-300"
              />
            </div>
            <button
              onClick={handleApplyOverride}
              className="px-2.5 py-1 bg-amber-600 hover:bg-amber-500 text-black font-semibold rounded text-xs transition"
            >
              Apply Fault
            </button>
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={() => { setOverrideValue(105.0); }}
              className="px-2 py-0.5 bg-rose-950 hover:bg-rose-900 text-rose-300 border border-rose-700/50 rounded text-[11px] font-mono transition"
            >
              High Spik (105.0)
            </button>
            <button
              onClick={() => { setOverrideValue(-5.0); }}
              className="px-2 py-0.5 bg-cyan-950 hover:bg-cyan-900 text-cyan-300 border border-cyan-700/50 rounded text-[11px] font-mono transition"
            >
              Low Drop (-5.0)
            </button>
          </div>
        </div>
      )}

      {/* State Flow Bar */}
      <div className="px-4 py-2 bg-slate-900/60 border-b border-slate-800/80 flex items-center gap-1.5 overflow-x-auto text-[11px]">
        {states.map((st, idx) => {
          const isActive = st === currentState;
          const isBreak = activeBreakStates.includes(st);
          return (
            <React.Fragment key={st}>
              <div className={`px-2.5 py-1 rounded-md font-mono flex items-center gap-1.5 transition ${
                isActive 
                  ? 'bg-indigo-600 text-white font-semibold shadow' 
                  : 'bg-slate-800/60 text-slate-400 border border-slate-700/40'
              }`}>
                {isActive && <CheckCircle className="w-3 h-3 text-emerald-300" />}
                {isBreak && <span className="w-2 h-2 rounded-full bg-amber-400" title="Breakpoint set" />}
                {st}
              </div>
              {idx < states.length - 1 && (
                <ArrowRight className="w-3 h-3 text-slate-600 flex-shrink-0" />
              )}
            </React.Fragment>
          );
        })}
      </div>

      {/* Middle Grid: Commands, Events & Telemetry Visualizer */}
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

        {/* Live Telemetry with Waveforms */}
        <div className="bg-slate-900/80 border border-slate-800 rounded-lg p-3">
          <div className="text-[11px] font-semibold text-slate-300 uppercase tracking-wider mb-2 flex items-center justify-between">
            <div className="flex items-center gap-1.5">
              <Gauge className="w-3.5 h-3.5 text-amber-400" />
              <span>Telemetry Waveforms</span>
            </div>
            <span className="text-[10px] text-slate-500 font-mono">Live HIL Feed</span>
          </div>
          <div className="space-y-2">
            {Object.keys(telemetry).length === 0 ? (
              <span className="text-[11px] text-slate-500">No active telemetry datapoints.</span>
            ) : (
              Object.entries(telemetry).map(([k, v]) => {
                const historyPoints: TelemetryPoint[] = status?.telemetry_history?.[k] || [];
                const values = historyPoints.map(p => p.value);
                const thresh = status?.telemetry_thresholds?.[k] || {};
                const warnMax = thresh.warn_max || 85.0;
                const faultMax = thresh.fault_max || 95.0;
                const isFault = v > faultMax || (thresh.fault_min !== undefined && v < thresh.fault_min);
                const isWarn = !isFault && v > warnMax;

                return (
                  <div key={k} className="bg-slate-950/70 border border-slate-800 rounded p-2 flex items-center justify-between text-xs font-mono">
                    <div className="flex flex-col">
                      <div className="flex items-center gap-1.5">
                        <span className="text-slate-300 font-medium">{k}</span>
                        {isFault ? (
                          <span className="px-1 py-0.2 rounded bg-rose-950 text-rose-300 text-[9px] border border-rose-800 font-bold animate-pulse">
                            FAULT
                          </span>
                        ) : isWarn ? (
                          <span className="px-1 py-0.2 rounded bg-amber-950 text-amber-300 text-[9px] border border-amber-800">
                            WARN
                          </span>
                        ) : (
                          <span className="px-1 py-0.2 rounded bg-emerald-950 text-emerald-300 text-[9px] border border-emerald-800">
                            NORMAL
                          </span>
                        )}
                      </div>
                      <span className="text-amber-300 font-bold text-sm">{v.toFixed(1)}</span>
                    </div>

                    {/* Live Sparkline */}
                    <div className="pl-2">
                      <Sparkline
                        data={values.length > 0 ? values : [v]}
                        warnMax={warnMax}
                        faultMax={faultMax}
                        color={isFault ? '#f43f5e' : isWarn ? '#f59e0b' : '#10b981'}
                      />
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </div>
      </div>

      {/* Active Alarms Bar (if any alarms present) */}
      {status?.alarm_history && status.alarm_history.length > 0 && (
        <div className="px-3 py-1.5 bg-rose-950/30 border-b border-rose-900/40 flex items-center gap-2 overflow-x-auto text-[11px] font-mono">
          <ShieldAlert className="w-3.5 h-3.5 text-rose-400 flex-shrink-0" />
          <span className="text-rose-300 font-semibold uppercase text-[10px] flex-shrink-0">Active Alarms:</span>
          {status.alarm_history.slice(-4).map((a, idx) => (
            <div key={idx} className="bg-rose-950/70 border border-rose-800/60 rounded px-2 py-0.5 text-rose-200 flex items-center gap-1.5 flex-shrink-0">
              <span className="text-rose-400 font-bold">[{a.alarm}]</span>
              <span className="text-slate-300 text-[10px]">{a.message}</span>
              <span className="text-slate-500 text-[9px]">({a.timestamp})</span>
            </div>
          ))}
        </div>
      )}

      {/* Terminal View */}
      <div className="flex-1 flex flex-col min-h-0 bg-black/90">
        <div className="px-4 py-1.5 bg-slate-900/90 border-b border-slate-800 flex items-center justify-between text-xs text-slate-400 font-mono">
          <div className="flex items-center gap-2">
            <Terminal className="w-3.5 h-3.5 text-indigo-400" />
            <span>Controller Live Execution Trace</span>
          </div>

          {/* Log Filter Pills */}
          <div className="flex items-center gap-1.5">
            <Filter className="w-3 h-3 text-slate-500" />
            {['ALL', 'TRANSITION', 'CMD', 'EVENT', 'ALARM', 'BREAKPOINT', 'OVERRIDE'].map((filt) => (
              <button
                key={filt}
                onClick={() => setLogFilter(filt)}
                className={`px-1.5 py-0.5 rounded text-[10px] font-mono transition ${
                  logFilter === filt 
                    ? 'bg-indigo-600 text-white' 
                    : 'bg-slate-800 text-slate-400 hover:text-slate-200'
                }`}
              >
                {filt}
              </button>
            ))}
            <span className="text-[10px] text-slate-500 pl-2">{filteredLogs.length} events</span>
          </div>
        </div>

        <div ref={terminalRef} className="flex-1 overflow-y-auto p-4 space-y-1 font-mono text-xs select-text">
          {filteredLogs.length === 0 ? (
            <div className="text-slate-600">Waiting for controller execution events...</div>
          ) : (
            filteredLogs.map((log: SimulationLog, index: number) => {
              let badgeColor = 'text-slate-400 bg-slate-800/50';
              if (log.level === 'TRANSITION') badgeColor = 'text-cyan-300 bg-cyan-950/60 border border-cyan-700/50';
              if (log.level === 'CMD') badgeColor = 'text-indigo-300 bg-indigo-950/60 border border-indigo-700/50';
              if (log.level === 'EVENT') badgeColor = 'text-emerald-300 bg-emerald-950/60 border border-emerald-700/50';
              if (log.level === 'ALARM') badgeColor = 'text-rose-300 bg-rose-950/60 border border-rose-700/50 font-bold';
              if (log.level === 'BREAKPOINT') badgeColor = 'text-amber-300 bg-amber-950/60 border border-amber-700/50 font-bold';
              if (log.level === 'OVERRIDE') badgeColor = 'text-purple-300 bg-purple-950/60 border border-purple-700/50 font-bold';

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
