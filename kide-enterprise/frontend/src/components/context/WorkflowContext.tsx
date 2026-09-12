import React, { useState, useMemo } from 'react';
import { useEditorStore } from '../../stores/editorStore';
import { Activity as ActivityIcon, Link2, GitFork, AlertCircle, CheckCircle2 } from 'lucide-react';

export type WfContextMode = 'activities' | 'dependencies' | 'conditions' | 'issues';

export const WorkflowContext: React.FC = () => {
  const { files, setSelectedNodeId, selectedNodeId, validationErrors } = useEditorStore();
  const [mode, setMode] = useState<WfContextMode>('activities');

  // Find the primary activity file
  const activityFile = useMemo(() => {
    return files.find(f => f.name.endsWith('.activity')) || files.find(f => f.name.endsWith('.json'));
  }, [files]);

  const activitiesData = useMemo(() => {
    if (!activityFile) return { activities: [], dependencies: [], conditions: [] };

    const content = activityFile.content;
    const activities: Array<{ name: string; desc?: string; next?: string }> = [];
    const dependencies: Array<{ activity: string; required: string; type: string }> = [];
    const conditions: Array<{ condition: string; target: string }> = [];

    const lines = content.split('\n');
    let currentAct = '';

    for (let i = 0; i < lines.length; i++) {
      const line = lines[i];

      const actMatch = line.match(/\bActivity\s+([A-Za-z0-9_]+)\s*\{/);
      if (actMatch) {
        currentAct = actMatch[1];
        activities.push({ name: currentAct });
        continue;
      }

      if (currentAct) {
        const descMatch = line.match(/description\s*:\s*"([^"]+)"/);
        if (descMatch) {
          const a = activities.find(x => x.name === currentAct);
          if (a) a.desc = descMatch[1];
        }

        const nextMatch = line.match(/nextActivity\s*:\s*([A-Za-z0-9_]+)/);
        if (nextMatch) {
          const a = activities.find(x => x.name === currentAct);
          if (a) a.next = nextMatch[1];
        }

        const reqCap = line.match(/requireCapability\s*:\s*"?([A-Za-z0-9_]+)"?/);
        if (reqCap) {
          dependencies.push({ activity: currentAct, required: reqCap[1], type: 'Capability' });
        }

        const reqOp = line.match(/requireOperation\s*\(\s*([A-Za-z0-9_]+)\s*\)/);
        if (reqOp) {
          dependencies.push({ activity: currentAct, required: reqOp[1], type: 'Operation' });
        }

        const condMatch = line.match(/if outcome ([A-Za-z0-9_]+) is \([^)]+\)\s*=>\s*nextActivity\s*:\s*([A-Za-z0-9_]+)/);
        if (condMatch) {
          conditions.push({ condition: line.trim(), target: condMatch[2] });
        }
      }
    }

    return { activities, dependencies, conditions };
  }, [activityFile]);

  const issues = activityFile ? (validationErrors[activityFile.id] || []) : [];

  if (!activityFile) {
    return (
      <div className="flex flex-col items-center justify-center p-6 text-center text-gray-500 h-full select-none">
        <ActivityIcon className="w-7 h-7 text-gray-600 mb-2" />
        <p className="text-xs font-medium text-gray-300">No Workflow File Found</p>
        <p className="text-[11px] text-gray-500 mt-1">Create an .activity file to design supervisory flows.</p>
      </div>
    );
  }

  return (
    <div className="flex-1 flex flex-col min-h-0 select-none overflow-hidden">
      {/* Submode Selector */}
      <div className="px-2 py-1.5 border-b border-gray-800/80 bg-[#111622]/60 flex items-center gap-1">
        <button
          onClick={() => setMode('activities')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition ${
            mode === 'activities'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
        >
          <ActivityIcon size={12} />
          <span>Steps ({activitiesData.activities.length})</span>
        </button>

        <button
          onClick={() => setMode('dependencies')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition ${
            mode === 'dependencies'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
        >
          <Link2 size={12} />
          <span>Requires</span>
        </button>

        <button
          onClick={() => setMode('conditions')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition ${
            mode === 'conditions'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
        >
          <GitFork size={12} />
          <span>Branching</span>
        </button>

        <button
          onClick={() => setMode('issues')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition relative ${
            mode === 'issues'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
        >
          <AlertCircle size={12} />
          <span>Issues</span>
          {issues.length > 0 && (
            <span className="ml-0.5 text-[9px] font-mono px-1 py-0.2 rounded-full font-bold bg-amber-950/80 text-amber-300">
              {issues.length}
            </span>
          )}
        </button>
      </div>

      {/* Mode Content */}
      <div className="flex-1 overflow-y-auto py-2 px-3 space-y-1.5 text-xs">
        {mode === 'activities' && (
          <div className="space-y-1">
            <div className="text-[10px] font-bold text-gray-500 uppercase tracking-wider mb-1">
              Workflow Activity Steps
            </div>
            {activitiesData.activities.map((a) => {
              const isSelected = selectedNodeId === a.name;
              return (
                <div
                  key={a.name}
                  onClick={() => setSelectedNodeId(a.name)}
                  className={`p-2 rounded cursor-pointer transition ${
                    isSelected
                      ? 'bg-blue-600/20 border border-blue-500/80 text-blue-200 font-medium'
                      : 'bg-gray-900/40 hover:bg-gray-800/60 text-gray-300 border border-transparent'
                  }`}
                >
                  <div className="flex items-center justify-between">
                    <span className="font-semibold text-gray-200">{a.name}</span>
                    {a.next && (
                      <span className="text-[10px] text-gray-500">→ {a.next}</span>
                    )}
                  </div>
                  {a.desc && (
                    <p className="text-[11px] text-gray-400 mt-0.5 truncate">{a.desc}</p>
                  )}
                </div>
              );
            })}
          </div>
        )}

        {mode === 'dependencies' && (
          <div className="space-y-1.5">
            <div className="text-[10px] font-bold text-gray-500 uppercase tracking-wider mb-1">
              Activity Capabilities & Operations
            </div>
            {activitiesData.dependencies.length === 0 ? (
              <p className="text-[11px] text-gray-500 italic">No external capabilities required.</p>
            ) : (
              activitiesData.dependencies.map((dep, idx) => (
                <div key={idx} className="p-2 rounded bg-gray-900/40 border border-gray-800 space-y-0.5">
                  <div className="flex items-center justify-between text-[11px]">
                    <span className="font-semibold text-gray-200">{dep.activity}</span>
                    <span className="text-[10px] text-indigo-400 uppercase">{dep.type}</span>
                  </div>
                  <p className="font-mono text-gray-400 text-[11px]">requires {dep.required}</p>
                </div>
              ))
            )}
          </div>
        )}

        {mode === 'conditions' && (
          <div className="space-y-1.5">
            <div className="text-[10px] font-bold text-gray-500 uppercase tracking-wider mb-1">
              Branching & Decisions
            </div>
            {activitiesData.conditions.length === 0 ? (
              <p className="text-[11px] text-gray-500 italic">No conditional branches declared.</p>
            ) : (
              activitiesData.conditions.map((c, idx) => (
                <div key={idx} className="p-2 rounded bg-gray-900/40 border border-gray-800 space-y-1">
                  <span className="text-[11px] font-mono text-amber-300 block truncate">
                    {c.condition}
                  </span>
                  <span className="text-[10px] text-gray-400">Branch target: {c.target}</span>
                </div>
              ))
            )}
          </div>
        )}

        {mode === 'issues' && (
          <div className="space-y-2">
            {issues.length === 0 ? (
              <div className="flex flex-col items-center justify-center p-4 text-center text-gray-500">
                <CheckCircle2 className="w-6 h-6 text-emerald-500 mb-1.5" />
                <p className="text-xs text-gray-300">Workflow Schema Valid</p>
                <p className="text-[11px] text-gray-500 mt-0.5">No sequential or conditional errors.</p>
              </div>
            ) : (
              issues.map((iss, idx) => (
                <div key={idx} className="p-2 rounded bg-gray-900/60 border border-gray-800 text-[11px] text-gray-300">
                  <span className="text-amber-400 font-semibold block mb-0.5">Line {iss.line || 1}</span>
                  {iss.message}
                </div>
              ))
            )}
          </div>
        )}
      </div>
    </div>
  );
};
