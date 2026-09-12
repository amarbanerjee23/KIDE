import React, { useState } from 'react';
import { MncModel } from '../../types/models';
import { ChevronRight, ChevronDown, Activity, Settings, Cpu, Radio, AlertCircle, Database, GitMerge } from 'lucide-react';

interface Props {
  model: MncModel;
}

const TreeNode = ({ label, icon: Icon, children, defaultOpen = false }: any) => {
  const [isOpen, setIsOpen] = useState(defaultOpen);
  
  return (
    <div className="ml-4">
      <div 
        className="flex items-center gap-2 py-1 cursor-pointer hover:bg-surface rounded px-2"
        onClick={() => setIsOpen(!isOpen)}
      >
        {children ? (
          isOpen ? <ChevronDown size={14} className="text-gray-400" /> : <ChevronRight size={14} className="text-gray-400" />
        ) : (
          <span className="w-3.5 inline-block"></span>
        )}
        {Icon && <Icon size={14} className="text-highlight" />}
        <span className="text-sm text-gray-200">{label}</span>
      </div>
      {isOpen && children && <div className="border-l border-accent ml-2">{children}</div>}
    </div>
  );
};

export const ControlNodeTree: React.FC<Props> = ({ model }) => {
  const { interface_description, control_node } = model;
  
  return (
    <div className="font-mono text-sm">
      <TreeNode label={model.name} icon={Settings} defaultOpen>
        <TreeNode label="Interface Description" icon={Cpu} defaultOpen>
          <TreeNode label={`Commands (${interface_description.commands.length})`}>
            {interface_description.commands.map((c, i) => (
              <TreeNode key={i} label={c.name} icon={Activity} />
            ))}
          </TreeNode>
          <TreeNode label={`Events (${interface_description.events.length})`}>
            {interface_description.events.map((e, i) => (
              <TreeNode key={i} label={e.name} icon={Radio} />
            ))}
          </TreeNode>
          <TreeNode label={`Alarms (${interface_description.alarms.length})`}>
            {interface_description.alarms.map((a, i) => (
              <TreeNode key={i} label={a.name} icon={AlertCircle} />
            ))}
          </TreeNode>
          <TreeNode label={`Data Points (${interface_description.data_points.length})`}>
            {interface_description.data_points.map((d, i) => (
              <TreeNode key={i} label={d.name} icon={Database} />
            ))}
          </TreeNode>
          {(() => {
            const raw = interface_description.operating_states as any;
            const stateList: string[] = Array.isArray(raw)
              ? raw.map((s: any) => typeof s === 'string' ? s : s?.name).filter(Boolean)
              : Array.isArray(raw?.states)
              ? raw.states.map((s: any) => typeof s === 'string' ? s : s?.name).filter(Boolean)
              : Array.isArray(raw?.operatingStates)
              ? raw.operatingStates.map((s: any) => typeof s === 'string' ? s : s?.name).filter(Boolean)
              : [];
            if (stateList.length === 0) return null;
            return (
              <TreeNode label={`States (${stateList.length})`}>
                {stateList.map((name, i) => (
                  <TreeNode key={i} label={name} icon={GitMerge} />
                ))}
              </TreeNode>
            );
          })()}
        </TreeNode>

        <TreeNode label="Control Node" icon={Cpu} defaultOpen>
          <TreeNode label={`Command Blocks (${control_node.command_response_blocks?.length || 0})`}>
            {control_node.command_response_blocks?.map((b, i) => (
              <TreeNode key={i} label={`Block: ${b.command_ref}`}>
                {b.action && <TreeNode label="Action configured" />}
                {b.validation_rules && <TreeNode label={`${b.validation_rules.length} Validations`} />}
                {b.response_blocks?.map((rb, j) => (
                  <TreeNode key={j} label={`Response: ${rb.response_ref}`} />
                ))}
              </TreeNode>
            ))}
          </TreeNode>
          <TreeNode label={`Event Blocks (${control_node.event_blocks?.length || 0})`}>
             {control_node.event_blocks?.map((b, i) => (
              <TreeNode key={i} label={b.event_ref} />
            ))}
          </TreeNode>
        </TreeNode>
      </TreeNode>
    </div>
  );
};
