export type PrimitiveValueType = 'string' | 'integer' | 'float' | 'boolean' | 'datetime' | 'binary' | 'anyURI' | 'decimal' | 'double' | 'time' | 'date';

export interface Parameter {
  name: string;
  type?: string; // Reference to a type
  required?: boolean;
}

export interface SimpleType {
  name: string;
  type: PrimitiveValueType;
  enum?: string[];
  pattern?: string;
  min_inclusive?: number;
  max_inclusive?: number;
  min_length?: number;
  max_length?: number;
}

export interface ArrayType {
  name: string;
  item_type: string;
  min_items?: number;
  max_items?: number;
}

export interface AbstractType {
  name: string;
  properties: Record<string, Parameter>;
}

export interface DataModel {
  name: string;
  types: (SimpleType | ArrayType | AbstractType)[];
}

export interface DataPackage {
  name: string;
  models: DataModel[];
}

export interface Command {
  name: string;
  async?: boolean;
  parameters: Parameter[];
}

export interface Event {
  name: string;
  publish?: boolean;
  parameters: Parameter[];
}

export interface Response {
  name: string;
  parameters: Parameter[];
}

export interface Alarm {
  name: string;
  publish?: boolean;
  level: number;
  parameters: Parameter[];
}

export interface DataPoint {
  name: string;
  publish?: boolean;
  type: string;
  value?: any;
  parameters: Parameter[];
}

export interface OperatingState {
  name: string;
  parameters: Parameter[];
}

export interface OperatingStates {
  states: OperatingState[];
  start_states: string[];
  end_states: string[];
}

export interface SubscribedItems {
  events?: string[];
  alarms?: string[];
  data_points?: string[];
}

export interface InterfaceDescription {
  name: string;
  uses?: string[];
  ip_address?: string;
  port?: { name: string; value: number };
  commands: Command[];
  events: Event[];
  responses: Response[];
  alarms: Alarm[];
  data_points: DataPoint[];
  operating_states?: OperatingStates;
  subscribed_items?: SubscribedItems;
}

export interface Action {
  raise_alarms?: string[];
  fire_commands?: string[];
  publish_events?: string[];
  trigger_data_points?: string[];
  execute_operations?: string[];
  transition_states?: string[]; // currentState => nextState
}

export interface Validation {
  parameters?: string[];
  check_max?: number;
  check_min?: number;
  check_values?: any[];
  on_fail_action?: Action;
  on_success_action?: Action;
}

export interface ResponseBlock {
  response_ref: string;
  action?: Action;
  validation_rules?: Validation[];
}

export interface CommandResponseBlock {
  command_ref: string;
  action?: Action;
  validation_rules?: Validation[];
  response_blocks?: ResponseBlock[];
}

export interface EventBlock {
  event_ref: string;
  action?: Action;
  validation_rules?: Validation[];
}

export interface AlarmBlock {
  alarm_ref: string;
  action?: Action;
  validation_rules?: Validation[];
}

export interface DataPointBlock {
  data_point_refs: string[];
  action?: Action;
  validation_rules?: Validation[];
}

export interface ControlNode {
  name: string;
  interface_ref: string;
  child_nodes?: string[];
  command_response_blocks?: CommandResponseBlock[];
  event_blocks?: EventBlock[];
  alarm_blocks?: AlarmBlock[];
  data_point_blocks?: DataPointBlock[];
}

export interface MncModel {
  name: string;
  interface_description: InterfaceDescription;
  control_node: ControlNode;
}

export interface Outcome {
  outcome: string;
}

export interface ConditionalActivity {
  name: string;
  condition: string;
  true_outcome?: Outcome;
  false_outcome?: Outcome;
}

export interface Activity {
  name: string;
  capability?: string;
  operation?: string;
  command?: string;
  event?: string;
  alarm?: string;
  input_data?: string[];
  timeout?: number;
  delay?: number;
  condition?: ConditionalActivity;
}

export interface ActivityDiagram {
  name: string;
  activities: Activity[];
}

export interface Capability {
  name: string;
  operation_ref: string;
}

export interface CapabilitiesOutcome {
  outcome: string;
}

export interface ControlCapabilities {
  name: string;
  capabilities: Capability[];
  outcomes?: CapabilitiesOutcome[];
}

export interface Operation {
  name: string;
  command_ref: string;
  response_ref: string;
}

export interface OperationDescriptions {
  name: string;
  operations: Operation[];
}

export interface FileItem {
  id: string;
  name: string;
  content: string;
  language: string;
  parsedAst?: any;
}

export interface ValidationError {
  fileId?: string;
  line?: number;
  column?: number;
  message: string;
  severity: 'error' | 'warning' | 'info';
}

export interface ParseResult {
  ast: any;
  errors: ValidationError[];
}

export interface TransformResult {
  model: MncModel | null;
  warnings: string[];
  validation_errors: string[];
}

export interface User {
  id: string | number;
  email: string;
  name?: string;
  full_name?: string;
  role?: string;
  org_name?: string;
}

export interface Project {
  id: string | number;
  name: string;
  description?: string;
  created_by_name?: string;
  file_count?: number;
  created_at: string;
  updated_at?: string;
}

export interface ProjectFile {
  id: string;
  project_id: string;
  name: string;
  content: string;
}
