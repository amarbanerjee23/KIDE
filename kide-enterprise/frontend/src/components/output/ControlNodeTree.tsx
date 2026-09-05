import React, { useState } from 'react';
import { ChevronRight, ChevronDown, Activity, Settings, Zap, Bell, Database } from 'lucide-react';
import { useEditorStore } from '../../stores/editorStore';

const TreeNode: React.FC<{ label: string, icon?: React.ReactNode, children?: React.ReactNode }> = ({ label, icon, children }) => {
  const [isOpen, setIsOpen] = useState(true);
  const hasChildren = React.Children.count(children) > 0;

  return (
    <div className="ml-4">
      <div 
        className={`flex items-center py-1 px-2 hover:bg-accent/50 rounded-md cursor-pointer text-sm ${!hasChildren ? 'ml-6' : ''}`}
        onClick={() => setIsOpen(!isOpen)}
      >
        {hasChildren && (
          <span className="mr-1 text-gray-400">
            {isOpen ? <ChevronDown className="w-4 h-4" /> : <ChevronRight className="w-4 h-4" />}
          </span>
        )}
        {icon && <span className="mr-2 text-highlight">{icon}</span>}
        <span className="text-gray-200">{label}</span>
      </div>
      {isOpen && hasChildren && <div className="ml-2 border-l border-accent/50 pl-2">{children}</div>}
    </div>
  );
};

const ControlNodeTree = () => {
  const transformResult = useEditorStore(state => state.transformResult);
  if (!transformResult?.model) return <div className="p-6 text-gray-500">Run Transform to see output</div>;

  const model = transformResult.model;
  const cn = model.interfaceDescription.controlNode;

  return (
    <div className="p-4 h-full overflow-y-auto">
      <h3 className="text-sm font-semibold text-gray-400 mb-4 px-2 uppercase tracking-wider">Hierarchy View</h3>
      <div className="-ml-4">
        <TreeNode label={`Model: ${model.name}`} icon={<Database className="w-4 h-4" />}>
          <TreeNode label={`Interface: ${model.interfaceDescription.name}`} icon={<Settings className="w-4 h-4" />}>
            <TreeNode label={`ControlNode: ${cn.name}`} icon={<Activity className="w-4 h-4" />}>
              
              <TreeNode label="Operating States">
                {cn.operatingStates?.map((os, i) => (
                  <TreeNode key={i} label={os.name} />
                ))}
              </TreeNode>

              <TreeNode label="Actions" icon={<PlayIcon />}>
                {cn.actions?.map((act, i) => (
                  <TreeNode key={i} label={`${act.name} (${act.type})`} />
                ))}
              </TreeNode>

              <TreeNode label="Events" icon={<Zap className="w-4 h-4" />}>
                {cn.eventBlock?.map((ev, i) => (
                  <TreeNode key={i} label={ev.event} />
                ))}
              </TreeNode>

              <TreeNode label="Alarms" icon={<Bell className="w-4 h-4" />}>
                {cn.alarmBlock?.map((al, i) => (
                  <TreeNode key={i} label={`${al.alarm} [${al.severity}]`} />
                ))}
              </TreeNode>

              <TreeNode label="Data Points" icon={<Database className="w-4 h-4" />}>
                {cn.dataPointBlock?.map((dp, i) => (
                  <TreeNode key={i} label={`${dp.name} (${dp.type})`} />
                ))}
              </TreeNode>

            </TreeNode>
          </TreeNode>
        </TreeNode>
      </div>
    </div>
  );
};

const PlayIcon = () => <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polygon points="5 3 19 12 5 21 5 3"></polygon></svg>;

export default ControlNodeTree;

