export interface Activity {
  name: string;
  requiresOperation: boolean;
  parameters: string[];
  commands: string[];
  events: string[];
  alarms: string[];
  dataPoints: string[];
  transitions: Transition[];
}

export interface Transition {
  from: string;
  to: string;
  condition?: string;
}

export interface ActivityDiagram {
  name: string;
  defaultOperatingStates: string[];
  activities: Activity[];
}

export interface OperatingState { name: string; }
export interface CommandResponseEntry { command: string; response: string; }
export interface EventEntry { event: string; }
export interface AlarmEntry { alarm: string; severity: string; }
export interface DataPointEntry { name: string; type: string; }
export interface ActionEntry { name: string; type: string; parameters: string[]; transitions: Transition[]; }

export interface ControlNode {
  name: string;
  operatingStates: OperatingState[];
  actions: ActionEntry[];
  eventBlock: EventEntry[];
  alarmBlock: AlarmEntry[];
  dataPointBlock: DataPointEntry[];
  commandResponseBlock: CommandResponseEntry[];
}

export interface MncModel {
  model: {
    name: string;
    interfaceDescription: {
      name: string;
      controlNode: ControlNode;
    };
  };
}

export interface User {
  id: number;
  email: string;
  full_name: string;
  role: 'admin' | 'member' | 'viewer';
  org_name: string;
}

export interface Project {
  id: number;
  name: string;
  description: string;
  created_by_name: string;
  file_count: number;
  created_at: string;
  updated_at: string;
}

export interface ProjectFile {
  id: number;
  filename: string;
  file_type: string;
  content: string;
  version: number;
}

export interface TransformResult {
  model: MncModel['model'];
  warnings: string[];
  validation_errors: string[];
}

export interface ValidationError {
  field: string;
  message: string;
}

