import { fetchClient } from './client';

export interface SimulationLog {
  timestamp: string;
  level: string;
  message: string;
  state: string;
}

export interface SimulationStatus {
  project_id: number;
  model_name: string;
  current_state: string;
  states: string[];
  commands: string[];
  events: string[];
  alarms: string[];
  telemetry: Record<string, number>;
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
};
