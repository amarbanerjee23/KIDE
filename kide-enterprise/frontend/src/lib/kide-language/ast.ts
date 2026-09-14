/**
 * Typed AST Definitions with Precise Source Ranges for KIDE Enterprise DSLs.
 * Every AST node carries exact source positions for flawless outline, navigation,
 * hover, semantic tokens, and error diagnostics.
 */

export interface SourcePosition {
  line: number;
  column: number;
}

export interface SourceRange {
  startLine: number;
  startColumn: number;
  endLine: number;
  endColumn: number;
}

export interface SymbolReference {
  name: string;
  kind: 'DataModel' | 'Capability' | 'Operation' | 'Activity' | 'InterfaceDescription' | 'ControlNode' | 'State' | 'Command' | 'Event' | 'Alarm' | 'DataPoint' | 'Parameter';
  range: SourceRange;
  containerName?: string;
  targetId?: string;
}

export interface AstNode {
  type: string;
  id: string;
  name?: string;
  range: SourceRange;
  parent?: AstNode;
  children: AstNode[];
  references: SymbolReference[];
  docComment?: string;
  metadata?: Record<string, any>;
}

// ----------------------------------------------------------------------------
// DML AST Nodes
// ----------------------------------------------------------------------------

export interface DmlParameterNode extends AstNode {
  type: 'Parameter';
  paramType: 'simple' | 'abstract' | 'array';
  valueType: string;
  value?: string;
  defaultValue?: string;
}

export interface DmlDataModelNode extends AstNode {
  type: 'DataModel';
  primitives: DmlParameterNode[];
  composites: SymbolReference[];
}

export interface DmlPackageNode extends AstNode {
  type: 'DataPackage' | 'DmlPackage';
  models: DmlDataModelNode[];
  dataModels?: DmlDataModelNode[];
}

// ----------------------------------------------------------------------------
// Capability AST Nodes
// ----------------------------------------------------------------------------

export interface CapabilityActionNode extends AstNode {
  type: 'Action';
  fireCommands: SymbolReference[];
  raiseAlarms: SymbolReference[];
  subscribeAlarms?: SymbolReference[];
  publishEvents: SymbolReference[];
  subscribeEvents?: SymbolReference[];
  triggerDataPoints: SymbolReference[];
  subscribeData?: SymbolReference[];
  executeOperations: SymbolReference[];
}

export interface ControlCapabilitiesNode extends AstNode {
  type: 'ControlCapabilities';
  commands: SymbolReference[];
  events: SymbolReference[];
  alarms: SymbolReference[];
  dataPoints: SymbolReference[];
}

export interface CapabilityOutcomeNode extends AstNode {
  type: 'CapabilitiesOutcome';
  responses: SymbolReference[];
  events: SymbolReference[];
  alarms: SymbolReference[];
  dataPoints: SymbolReference[];
}

export interface CapabilityNode extends AstNode {
  type: 'Capability';
  componentInterfaces: SymbolReference[];
  initAction?: CapabilityActionNode;
  controlCapabilities?: ControlCapabilitiesNode;
  outcomes?: CapabilityOutcomeNode;
}

// ----------------------------------------------------------------------------
// Operation AST Nodes
// ----------------------------------------------------------------------------

export interface OperationNode extends AstNode {
  type: 'Operation';
  inputParameters: DmlParameterNode[];
  outputParameters?: DmlParameterNode;
  outputParameter?: DmlParameterNode;
  executableScript?: string;
  executableResourceRef?: string;
}

export interface OperationDescriptionsNode extends AstNode {
  type: 'OperationDescriptions';
  operations: OperationNode[];
}

// ----------------------------------------------------------------------------
// Activity AST Nodes
// ----------------------------------------------------------------------------

export interface ActivityNode extends AstNode {
  type: 'Activity';
  description?: string;
  inputParameters: SymbolReference[];
  requiredCapability?: SymbolReference;
  requireCapability?: SymbolReference;
  requiredOperations: SymbolReference[];
  requireOperation?: SymbolReference;
  childActivityDiagram?: SymbolReference;
  nextActivity?: SymbolReference;
  nextActivityDiagram?: SymbolReference;
  timeDuration?: number;
  timeUnit?: 'secs' | 'mins' | 'hrs' | 'days';
  conditions: AstNode[];
}

export interface ActivityDiagramNode extends AstNode {
  type: 'ActivityDiagram';
  contextDataModel?: SymbolReference;
  contextModel?: SymbolReference;
  physicalContexts: string[];
  results: DmlParameterNode[];
  activities: ActivityNode[];
}

// ----------------------------------------------------------------------------
// MNC-ML AST Nodes
// ----------------------------------------------------------------------------

export interface MncOperatingStateNode extends AstNode {
  type: 'OperatingState';
  parameters: DmlParameterNode[];
}

export interface MncTransitionNode extends AstNode {
  type: 'Transition';
  currentStates: string[];
  currentState?: string;
  nextState: string;
  condition?: string;
}

export interface MncInterfaceNode extends AstNode {
  type: 'InterfaceDescription';
  uses: SymbolReference[];
  port?: { name: string; value?: number; range: SourceRange };
  commands: AstNode[];
  events: AstNode[];
  responses: AstNode[];
  alarms: AstNode[];
  dataPoints: AstNode[];
  operatingStates: MncOperatingStateNode[];
}

export interface MncControlNodeNode extends AstNode {
  type: 'ControlNode';
  implementedInterface?: SymbolReference;
  implementsInterface?: SymbolReference;
  childNodes: SymbolReference[];
  transitions: MncTransitionNode[];
  commandBlocks: AstNode[];
  eventBlocks: AstNode[];
  alarmBlocks: AstNode[];
}

export interface MncModelNode extends AstNode {
  type: 'Model';
  interfaceDescription?: MncInterfaceNode;
  controlNode?: MncControlNodeNode;
}

