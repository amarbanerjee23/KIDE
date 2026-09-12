import { Handle, Position, NodeProps } from '@xyflow/react';
import { Activity, ConditionalActivity } from '../../types/models';

export const StartNode = (_props: NodeProps) => (
  <div className="w-8 h-8 rounded-full bg-green-500 border-2 border-green-700 flex items-center justify-center text-white text-xs font-bold shadow-md">
    S
    <Handle type="source" position={Position.Bottom} className="w-2 h-2 !bg-white" />
  </div>
);

export const EndNode = (_props: NodeProps) => (
  <div className="w-8 h-8 rounded-full bg-red-500 border-2 border-red-700 flex items-center justify-center text-white text-xs font-bold shadow-md">
    E
    <Handle type="target" position={Position.Top} className="w-2 h-2 !bg-white" />
  </div>
);

export const ActivityNode = ({ data }: NodeProps) => {
  const activity = (data.activity || {}) as Activity;
  const label = (data.label || '') as string;

  return (
    <div className="bg-surface border-2 border-accent rounded-md min-w-[150px] shadow-lg relative">
      <Handle type="target" position={Position.Top} className="w-2 h-2 !bg-white" />
      
      <div className="bg-accent px-3 py-1 text-sm font-semibold text-white rounded-t-sm">
        {label}
      </div>
      
      <div className="p-3 text-xs text-gray-300 flex flex-col gap-1">
        {activity.capability && (
          <div className="flex justify-between items-center bg-background px-2 py-1 rounded">
            <span className="text-gray-400">Cap:</span>
            <span className="text-highlight">{activity.capability}</span>
          </div>
        )}
        {activity.operation && (
          <div className="flex justify-between items-center bg-background px-2 py-1 rounded">
            <span className="text-gray-400">Op:</span>
            <span className="text-highlight">{activity.operation}</span>
          </div>
        )}
        {activity.command && (
          <div className="flex justify-between items-center bg-blue-900/30 px-2 py-1 rounded text-blue-300">
            <span>Cmd: {activity.command}</span>
          </div>
        )}
        {activity.event && (
          <div className="flex justify-between items-center bg-purple-900/30 px-2 py-1 rounded text-purple-300">
            <span>Evt: {activity.event}</span>
          </div>
        )}
        {activity.alarm && (
          <div className="flex justify-between items-center bg-red-900/30 px-2 py-1 rounded text-red-300">
            <span>Alm: {activity.alarm}</span>
          </div>
        )}
        {(activity.input_data && activity.input_data.length > 0) && (
          <div className="mt-1 text-gray-400 text-[10px]">
            Inputs: {activity.input_data.join(', ')}
          </div>
        )}
      </div>

      <Handle type="source" position={Position.Bottom} className="w-2 h-2 !bg-white" />
    </div>
  );
};

export const ConditionNode = ({ data }: NodeProps) => {
  const condition = (data.condition || {}) as ConditionalActivity;
  const label = (data.label || '') as string;

  return (
    <div className="relative flex items-center justify-center min-w-[100px] min-h-[100px]">
      <Handle type="target" position={Position.Top} className="w-2 h-2 !bg-white" />
      
      <div className="absolute inset-0 bg-yellow-900/50 border-2 border-yellow-600 rotate-45 transform origin-center rounded-sm"></div>
      
      <div className="relative z-10 text-center px-4 max-w-[120px]">
        <div className="text-xs font-bold text-yellow-100">{label}</div>
        <div className="text-[10px] text-yellow-300 mt-1 truncate">{condition.condition}</div>
      </div>

      <Handle type="source" position={Position.Left} id="true" className="w-2 h-2 !bg-green-500" />
      <Handle type="source" position={Position.Right} id="false" className="w-2 h-2 !bg-red-500" />
    </div>
  );
};

export const OperatingStateNode = ({ data }: NodeProps) => {
  const label = (data.label || '') as string;
  const isStart = !!data.isStart;
  const isEnd = !!data.isEnd;
  const isReady = label === 'READY';
  const isAlarm = label.toLowerCase().includes('abort') || label.toLowerCase().includes('error') || label.toLowerCase().includes('fault');
  const role = (data.role || '') as string;

  return (
    <div className={`px-4 py-3 rounded-xl border-2 min-w-[130px] max-w-[200px] text-center shadow-xl transition-all duration-200 relative group
      ${isStart ? 'bg-emerald-950/80 border-emerald-500 shadow-emerald-950/50 hover:border-emerald-400' : 
        isReady ? 'bg-cyan-950/80 border-cyan-500 shadow-cyan-950/50 hover:border-cyan-400' :
        isEnd ? 'bg-blue-950/80 border-blue-500 shadow-blue-950/50 hover:border-blue-400' : 
        isAlarm ? 'bg-rose-950/80 border-rose-500 shadow-rose-950/50 hover:border-rose-400' :
        'bg-indigo-950/80 border-indigo-500/80 shadow-indigo-950/50 hover:border-indigo-400'}`}>
      
      {/* 4-Way Handles for clean layout routing */}
      <Handle type="target" position={Position.Left} id="in-left" className="w-2.5 h-2.5 !bg-white border-2 border-indigo-900" />
      <Handle type="target" position={Position.Top} id="in-top" className="w-2.5 h-2.5 !bg-white border-2 border-indigo-900" />
      
      <div className="flex flex-col items-center">
        <span className={`text-[9px] font-mono uppercase tracking-wider px-1.5 py-0.5 rounded font-bold mb-1
          ${isStart ? 'bg-emerald-900/60 text-emerald-300' :
            isReady ? 'bg-cyan-900/60 text-cyan-300' :
            isEnd ? 'bg-blue-900/60 text-blue-300' :
            isAlarm ? 'bg-rose-900/60 text-rose-300' :
            'bg-indigo-900/60 text-indigo-300'}`}>
          {role || (isStart ? 'Start State' : isReady ? 'Operational' : isEnd ? 'Result State' : isAlarm ? 'Safety Alarm' : 'Activity State')}
        </span>
        <div className="font-semibold text-sm text-gray-100 truncate w-full" title={label}>{label}</div>
      </div>

      <Handle type="source" position={Position.Right} id="out-right" className="w-2.5 h-2.5 !bg-white border-2 border-indigo-900" />
      <Handle type="source" position={Position.Bottom} id="out-bottom" className="w-2.5 h-2.5 !bg-white border-2 border-indigo-900" />
    </div>
  );
};

export const InterfaceNode = ({ data }: NodeProps) => {
  const label = (data.label || '') as string;

  return (
    <div className="bg-blue-900/20 border-2 border-blue-500 rounded-lg p-4 min-w-[200px] shadow-lg">
      <div className="text-blue-300 font-bold mb-2 border-b border-blue-500/50 pb-1">{label}</div>
      <div className="text-xs text-gray-400">Interface Description</div>
      <Handle type="source" position={Position.Bottom} className="w-2 h-2 !bg-blue-400" />
      <Handle type="target" position={Position.Top} className="w-2 h-2 !bg-blue-400" />
    </div>
  );
};

export const ControlNodeFlowNode = ({ data }: NodeProps) => {
  const label = (data.label || '') as string;

  return (
    <div className="bg-gray-800/80 border-2 border-gray-500 rounded-lg p-4 min-w-[200px] shadow-lg">
      <div className="text-gray-200 font-bold mb-2 border-b border-gray-600 pb-1">{label}</div>
      <div className="text-xs text-gray-400">Control Node</div>
      <Handle type="source" position={Position.Bottom} className="w-2 h-2 !bg-gray-400" />
      <Handle type="target" position={Position.Top} className="w-2 h-2 !bg-gray-400" />
    </div>
  );
};

export const nodeTypes = {
  start: StartNode,
  end: EndNode,
  activity: ActivityNode,
  condition: ConditionNode,
  operatingState: OperatingStateNode,
  interface: InterfaceNode,
  controlNode: ControlNodeFlowNode
};
