import { fetchClient } from './client';
import { useAuthStore } from '../stores/authStore';

export interface SimulationLog {
  timestamp: string;
  level: string;
  message: string;
  state: string;
}

export interface TelemetryPoint {
  timestamp: string;
  step: number;
  value: number;
  is_anomaly: boolean;
  state: string;
}

export interface TelemetryThreshold {
  warn_min?: number;
  warn_max?: number;
  fault_min?: number;
  fault_max?: number;
}

export type SimulationModeType = 
  | 'VIRTUAL_EMULATION' 
  | 'HIL_MODBUS_TCP' 
  | 'HIL_MQTT' 
  | 'HIL_OPC_UA' 
  | 'REPLAY';

export type ExecutionStateType = 'RUNNING' | 'PAUSED' | 'STOPPED';

export interface SimulationStatus {
  project_id: number;
  model_name: string;
  mode: SimulationModeType | string;
  execution_state: ExecutionStateType | string;
  protocol_config?: Record<string, any>;
  step_counter: number;
  tick_rate_hz: number;
  current_state: string;
  states: string[];
  commands: string[];
  events: string[];
  alarms: string[];
  telemetry: Record<string, number>;
  telemetry_history?: Record<string, TelemetryPoint[]>;
  telemetry_thresholds?: Record<string, TelemetryThreshold>;
  break_on_states?: string[];
  break_on_alarms?: boolean;
  is_breakpoint_hit?: boolean;
  breakpoint_reason?: string | null;
  alarm_history: Array<{ alarm: string; level: number; message: string; timestamp: string; state: string }>;
  command_history: Array<{ command: string; status: string; response: string; current_state: string }>;
  logs: SimulationLog[];
}

export const simulationApi = {
  startSimulation: (projectId: number, forceReset: boolean = false, activityFileId?: number): Promise<SimulationStatus> =>
    fetchClient(`/projects/${projectId}/simulate`, { method: 'POST', body: JSON.stringify({ force_reset: forceReset, activity_file_id: activityFileId }) }),

  getStatus: (projectId: number): Promise<SimulationStatus> =>
    fetchClient(`/projects/${projectId}/simulate`),

  executeCommand: (projectId: number, command: string, payload?: any): Promise<{ result: any; session: SimulationStatus }> =>
    fetchClient(`/projects/${projectId}/simulate/command`, { method: 'POST', body: JSON.stringify({ command, payload }) }),

  injectEvent: (projectId: number, event: string, payload?: any): Promise<{ result: any; session: SimulationStatus }> =>
    fetchClient(`/projects/${projectId}/simulate/event`, { method: 'POST', body: JSON.stringify({ event, payload }) }),

  step: (projectId: number): Promise<SimulationStatus> =>
    fetchClient(`/projects/${projectId}/simulate/step`, { method: 'POST' }),

  setExecutionState: (projectId: number, state: ExecutionStateType | string): Promise<SimulationStatus> =>
    fetchClient(`/projects/${projectId}/simulate/execution-state`, {
      method: 'POST',
      body: JSON.stringify({ state })
    }),

  setMode: (projectId: number, mode: SimulationModeType | string, protocolConfig?: Record<string, any>): Promise<SimulationStatus> =>
    fetchClient(`/projects/${projectId}/simulate/mode`, {
      method: 'POST',
      body: JSON.stringify({ mode, protocol_config: protocolConfig })
    }),

  setBreakpoints: (projectId: number, breakOnStates: string[], breakOnAlarms: boolean): Promise<SimulationStatus> =>
    fetchClient(`/projects/${projectId}/simulate/breakpoints`, {
      method: 'POST',
      body: JSON.stringify({ break_on_states: breakOnStates, break_on_alarms: breakOnAlarms })
    }),

  overrideTelemetry: (projectId: number, datapoint: string, value: number): Promise<{ result: any; session: SimulationStatus }> =>
    fetchClient(`/projects/${projectId}/simulate/telemetry/override`, {
      method: 'POST',
      body: JSON.stringify({ datapoint, value })
    }),

  getTelemetryHistory: (projectId: number): Promise<Record<string, TelemetryPoint[]>> =>
    fetchClient(`/projects/${projectId}/simulate/telemetry/history`),

  exportTelemetry: async (projectId: number, format: 'csv' | 'json' = 'csv'): Promise<string> => {
    const token = useAuthStore.getState().token;
    const headers: Record<string, string> = {};
    if (token) headers['Authorization'] = `Bearer ${token}`;
    const res = await fetch(`/api/v1/projects/${projectId}/simulate/telemetry/export?format=${format}`, { headers });
    if (!res.ok) throw new Error('Failed to export telemetry data');
    return res.text();
  },

  downloadTelemetryExport: async (projectId: number, format: 'csv' | 'json' = 'csv'): Promise<void> => {
    const token = useAuthStore.getState().token;
    const headers: Record<string, string> = {};
    if (token) headers['Authorization'] = `Bearer ${token}`;
    const res = await fetch(`/api/v1/projects/${projectId}/simulate/telemetry/export?format=${format}`, { headers });
    if (!res.ok) throw new Error('Failed to download telemetry export');
    const blob = await res.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `telemetry_trace_project_${projectId}.${format}`;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
  },

  createSimulationWebSocket: (
    projectId: number,
    onMessage: (data: any) => void,
    onError?: (err: any) => void,
    onClose?: () => void
  ): WebSocket => {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const host = window.location.host;
    const wsUrl = `${protocol}//${host}/api/v1/projects/${projectId}/ws`;
    const ws = new WebSocket(wsUrl);
    ws.onmessage = (evt) => {
      try {
        const parsed = JSON.parse(evt.data);
        onMessage(parsed);
      } catch (err) {
        console.error('Simulation WS parse error:', err);
      }
    };
    if (onError) ws.onerror = onError;
    if (onClose) ws.onclose = onClose;
    return ws;
  }
};
