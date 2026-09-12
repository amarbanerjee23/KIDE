import React, { useMemo } from 'react';
import { useEditorStore, useActivityFile } from '../../stores/editorStore';
import { CollapsibleSection } from './CollapsibleSection';
import { 
  Activity, ArrowRight, ShieldCheck, 
  GitMerge, Sparkles, FileCode2, AlertTriangle, AlertCircle, X 
} from 'lucide-react';

export const WorkflowInspector: React.FC = () => {
  const activityFile = useActivityFile();
  const { 
    files, 
    selectedNodeId, 
    setSelectedNodeId, 
    validationErrors, 
    setActiveView
  } = useEditorStore();

  const activeRawFile = files.find(f => f.name.endsWith('.activity') || f.language === 'activitydsl');
  const issues = activeRawFile ? (validationErrors[activeRawFile.id] || []) : [];
  const errorCount = issues.filter(e => e.severity === 'error').length;

  const activities = (activityFile?.activities || []) as any[];

  // Currently selected activity
  const selectedActivity = useMemo(() => {
    if (!selectedNodeId || activities.length === 0) return null;
    return activities.find(a => a.name === selectedNodeId);
  }, [selectedNodeId, activities]);

  // Aggregate required capabilities across whole workflow
  const allRequiredCaps = useMemo(() => {
    const caps = new Set<string>();
    activities.forEach((a: any) => {
      if (a.require_capability) caps.add(a.require_capability);
      if (a.requiredCapability) caps.add(a.requiredCapability);
    });
    return Array.from(caps);
  }, [activities]);

  const handleSynthesize = async () => {
    setActiveView('statemachine');
  };

  // ---------------------------------------------------------------
  // STATE C: SPECIFIC WORKFLOW ACTIVITY SELECTED
  // ---------------------------------------------------------------
  if (selectedActivity) {
    const conditions: string[] = [];
    if (selectedActivity.condition) {
      const cond = selectedActivity.condition;
      if (cond.variable && cond.operator && cond.value) {
        conditions.push(`${cond.variable} ${cond.operator} ${cond.value}`);
      }
    }

    const nextActs = selectedActivity.next_activity ? [selectedActivity.next_activity] : [];

    return (
      <div className="flex-1 flex flex-col min-h-0 overflow-y-auto select-none">
        {/* Quick Nav Header */}
        <div className="p-3 border-b border-gray-800 bg-[#111622]/60 flex items-center justify-between">
          <div className="flex items-center gap-1.5 text-xs text-indigo-300 font-semibold truncate">
            <Activity size={14} className="text-orange-400 shrink-0" />
            <span>{selectedActivity.name}</span>
          </div>
          <button
            onClick={() => setSelectedNodeId(null)}
            className="text-[11px] text-gray-400 hover:text-gray-200 flex items-center gap-1 hover:underline"
          >
            <X size={12} />
            <span>Deselect</span>
          </button>
        </div>

        {/* Description */}
        {selectedActivity.description && (
          <div className="p-3 border-b border-gray-800/60 bg-gray-900/30 text-xs text-gray-300 italic">
            "{selectedActivity.description}"
          </div>
        )}

        {/* Requires (Capability / Operation) */}
        {(selectedActivity.require_capability || selectedActivity.require_operation) && (
          <CollapsibleSection
            title="Requires"
            icon={<ArrowRight size={13} className="text-emerald-400" />}
            hideIfZero={false}
          >
            <div className="space-y-1 mt-1">
              {selectedActivity.require_capability && (
                <div className="p-1.5 rounded bg-indigo-950/20 border border-indigo-800/40 font-mono text-xs text-indigo-300">
                  <span className="text-gray-500 mr-1">Capability:</span>
                  <span className="font-semibold">{selectedActivity.require_capability}</span>
                </div>
              )}
              {selectedActivity.require_operation && (
                <div className="p-1.5 rounded bg-amber-950/20 border border-amber-800/40 font-mono text-xs text-amber-300">
                  <span className="text-gray-500 mr-1">Operation:</span>
                  <span className="font-semibold">{selectedActivity.require_operation}</span>
                </div>
              )}
            </div>
          </CollapsibleSection>
        )}

        {/* Next Activities */}
        {nextActs.length > 0 && (
          <CollapsibleSection
            title="Next Step"
            count={nextActs.length}
            icon={<ArrowRight size={13} className="text-blue-400" />}
            hideIfZero={true}
          >
            <div className="space-y-1 mt-1">
              {nextActs.map((nxt, idx) => (
                <button
                  key={idx}
                  onClick={() => setSelectedNodeId(nxt)}
                  className="w-full p-1.5 rounded bg-gray-900 border border-gray-800 text-left font-medium text-xs text-gray-200 hover:border-blue-500 transition"
                >
                  {nxt}
                </button>
              ))}
            </div>
          </CollapsibleSection>
        )}

        {/* Conditions */}
        {conditions.length > 0 && (
          <CollapsibleSection
            title="Branch Conditions"
            count={conditions.length}
            icon={<GitMerge size={13} className="text-amber-400" />}
            hideIfZero={true}
          >
            <div className="space-y-1 mt-1">
              {conditions.map((cond, idx) => (
                <div key={idx} className="p-1.5 rounded bg-amber-950/20 border border-amber-800/40 font-mono text-xs text-amber-300">
                  {cond}
                </div>
              ))}
            </div>
          </CollapsibleSection>
        )}

        {/* Actions */}
        <div className="p-3 mt-auto border-t border-gray-800 space-y-1.5">
          <button
            onClick={() => setActiveView('editor')}
            className="w-full py-1.5 px-2.5 bg-blue-600 hover:bg-blue-500 text-white rounded text-xs font-semibold flex items-center justify-center gap-1.5 transition shadow"
          >
            <FileCode2 size={13} />
            <span>Open in Code Editor</span>
          </button>
          <button
            onClick={() => setSelectedNodeId(null)}
            className="w-full py-1 px-2.5 bg-gray-800 hover:bg-gray-700 text-gray-300 rounded text-xs transition"
          >
            Return to Workflow Overview
          </button>
        </div>
      </div>
    );
  }

  // ---------------------------------------------------------------
  // STATE A: WORKFLOW OVERVIEW (Nothing selected)
  // ---------------------------------------------------------------
  return (
    <div className="flex-1 flex flex-col min-h-0 overflow-y-auto select-none">
      {/* Workflow Summary Banner */}
      <div className="p-3 border-b border-gray-800 bg-[#111622]/40">
        <div className="text-xs font-bold text-gray-200 uppercase tracking-wider flex items-center gap-1.5 mb-1">
          <Activity size={14} className="text-orange-400" />
          <span>{activityFile?.name || activeRawFile?.name || 'Process Workflow'}</span>
        </div>
        <div className="text-[11px] text-gray-400">
          Supervisory sequencing and conditional branch execution
        </div>
        <div className="mt-2 text-xs font-mono text-gray-300 font-semibold flex items-center gap-2">
          <span>{activities.length} Activities</span>
          <span className="text-gray-600">&bull;</span>
          <span>{allRequiredCaps.length} Capabilities</span>
        </div>
      </div>

      {/* Sequential Activities */}
      <CollapsibleSection
        title="Activities"
        count={activities.length}
        icon={<Activity size={13} className="text-orange-400" />}
        hideIfZero={false}
      >
        <div className="space-y-1 mt-1">
          {activities.length === 0 ? (
            <div className="text-gray-500 text-xs py-1">No activities declared.</div>
          ) : (
            activities.map((act, idx) => (
              <div
                key={act.name || idx}
                onClick={() => setSelectedNodeId(act.name)}
                className="p-1.5 rounded hover:bg-gray-800 border border-transparent hover:border-gray-700 flex items-center justify-between cursor-pointer transition text-xs group"
              >
                <div className="flex items-center gap-2 min-w-0">
                  <span className="font-mono text-[10px] text-gray-500">{idx + 1}.</span>
                  <span className="font-semibold text-gray-200 group-hover:text-blue-300 truncate">
                    {act.name}
                  </span>
                </div>
                {act.require_capability && (
                  <span className="text-[10px] font-mono text-indigo-400 bg-indigo-950/60 px-1 py-0.2 rounded border border-indigo-800/40 truncate max-w-[100px]">
                    {act.require_capability}
                  </span>
                )}
              </div>
            ))
          )}
        </div>
      </CollapsibleSection>

      {/* Required Capabilities */}
      <CollapsibleSection
        title="Required Capabilities"
        count={allRequiredCaps.length}
        icon={<ShieldCheck size={13} className="text-indigo-400" />}
        hideIfZero={true}
      >
        <div className="space-y-1 mt-1">
          {allRequiredCaps.map((cap, idx) => (
            <div key={idx} className="p-1.5 rounded bg-indigo-950/20 border border-indigo-800/40 text-xs font-mono text-indigo-300">
              {cap}
            </div>
          ))}
        </div>
      </CollapsibleSection>

      {/* Issues */}
      {issues.length > 0 && (
        <CollapsibleSection
          title={errorCount > 0 ? 'Workflow Errors' : 'Workflow Warnings'}
          count={issues.length}
          icon={errorCount > 0 ? <AlertCircle size={13} className="text-rose-400" /> : <AlertTriangle size={13} className="text-amber-400" />}
          badgeClass={errorCount > 0 ? 'bg-rose-950 text-rose-300 border border-rose-800' : 'bg-amber-950 text-amber-300 border border-amber-800'}
          hideIfZero={true}
        >
          <div className="space-y-1.5 mt-1">
            {issues.map((iss, idx) => (
              <div key={idx} className="p-2 rounded bg-gray-900 border border-gray-800 text-xs text-gray-300">
                <div className="font-bold text-[10px] uppercase text-amber-400">{iss.severity}</div>
                <div>{iss.message}</div>
              </div>
            ))}
          </div>
        </CollapsibleSection>
      )}

      {/* Relevant Actions */}
      <div className="p-3 mt-auto border-t border-gray-800 space-y-1.5">
        <div className="text-[10px] uppercase font-bold text-gray-500 tracking-wider mb-1">
          Actions
        </div>
        <button
          onClick={handleSynthesize}
          className="w-full py-1.5 px-2.5 bg-indigo-600 hover:bg-indigo-500 text-white rounded text-xs font-semibold flex items-center justify-center gap-1.5 transition shadow"
        >
          <Sparkles size={13} />
          <span>Synthesize State Machine Automata</span>
        </button>
        <button
          onClick={() => setActiveView('editor')}
          className="w-full py-1 px-2.5 bg-gray-800 hover:bg-gray-700 text-gray-300 rounded text-xs transition"
        >
          Edit Workflow DSL Code
        </button>
      </div>
    </div>
  );
};
