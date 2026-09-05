import React from 'react';
import { Handle, Position, NodeProps } from '@xyflow/react';

export const ActivityNode = ({ data }: NodeProps) => {
  const isOp = data.requiresOperation;
  return (
    <div className={`px-4 py-2 shadow-md rounded-md bg-surface border-2 ${isOp ? 'border-blue-500' : 'border-green-500'}`}>
      <Handle type="target" position={Position.Top} className="w-2 h-2" />
      <div className="font-bold text-sm">{data.name as string}</div>
      <div className="text-xs text-gray-400 mt-1 flex space-x-2">
        {(data.commands as string[])?.length > 0 && <span className="bg-orange-500/20 text-orange-400 px-1 rounded">Cmd</span>}
        {(data.events as string[])?.length > 0 && <span className="bg-green-500/20 text-green-400 px-1 rounded">Evt</span>}
      </div>
      <Handle type="source" position={Position.Bottom} className="w-2 h-2" />
    </div>
  );
};

export const StartNode = ({ data }: NodeProps) => {
  return (
    <div className="w-12 h-12 rounded-full bg-green-500 flex items-center justify-center border-4 border-green-800 shadow-lg text-xs font-bold text-black">
      Start
      <Handle type="source" position={Position.Bottom} />
    </div>
  );
};

export const EndNode = ({ data }: NodeProps) => {
  return (
    <div className="w-12 h-12 rounded-full bg-red-500 flex items-center justify-center border-4 border-red-800 shadow-lg text-xs font-bold text-white">
      End
      <Handle type="target" position={Position.Top} />
    </div>
  );
};

export const ConditionNode = ({ data }: NodeProps) => {
  return (
    <div className="w-24 h-24 bg-yellow-600 rotate-45 flex items-center justify-center border-2 border-yellow-400">
      <div className="-rotate-45 text-xs font-bold text-center text-white px-2">
        {data.label as string}
      </div>
      <Handle type="target" position={Position.Top} className="-rotate-45" />
      <Handle type="source" position={Position.Bottom} className="-rotate-45" />
      <Handle type="source" position={Position.Right} id="right" className="-rotate-45" />
      <Handle type="source" position={Position.Left} id="left" className="-rotate-45" />
    </div>
  );
};

export const nodeTypes = {
  activity: ActivityNode,
  start: StartNode,
  end: EndNode,
  condition: ConditionNode,
};

