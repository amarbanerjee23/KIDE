import React from 'react';
import { useEditorStore, useActiveFile } from '../../stores/editorStore';
import { CheckCircle2, AlertCircle } from 'lucide-react';

export const ArtifactInfo: React.FC = () => {
  const activeFile = useActiveFile();
  const { dirtyFileIds, validationErrors, isSynthesisStale, transformResult } = useEditorStore();

  if (!activeFile) {
    return (
      <div className="flex flex-col items-center justify-center p-6 text-center text-gray-500 h-full select-none">
        <p className="text-xs font-medium text-gray-300">No Artifact Selected</p>
      </div>
    );
  }

  const isDirty = dirtyFileIds.includes(activeFile.id);
  const errors = validationErrors[activeFile.id] || [];
  const lineCount = activeFile.content.split('\n').length;
  const charCount = activeFile.content.length;

  const getDslMeta = (name: string) => {
    const lower = name.toLowerCase();
    if (lower.endsWith('.activity')) {
      return { type: 'Supervisory Workflow', stage: 'Stage 3: Supervisory Workflow', badge: 'ACT' };
    }
    if (lower.endsWith('.dml')) {
      return { type: 'Domain Data Model', stage: 'Stage 1: Data Modeling', badge: 'DML' };
    }
    if (lower.endsWith('.cap') || lower.endsWith('.capability')) {
      return { type: 'Device Capability Specification', stage: 'Stage 2: Capabilities & Ops', badge: 'CAP' };
    }
    if (lower.endsWith('.op') || lower.endsWith('.operation')) {
      return { type: 'Executable Operation Description', stage: 'Stage 2: Capabilities & Ops', badge: 'OP' };
    }
    if (lower.endsWith('.mnc')) {
      return { type: 'Formal MNC-ML Model', stage: 'Stage 4: Automated Synthesis', badge: 'MNC' };
    }
    return { type: 'Engineering Artifact', stage: 'Workspace Resource', badge: 'RAW' };
  };

  const meta = getDslMeta(activeFile.name);

  // Extract model name
  const modelNameMatch = activeFile.content.match(/\b(?:ActivityDiagram|Model|Capability|Operation|DataModel|Package)\s+([A-Za-z0-9_]+)/);
  const rootName = modelNameMatch ? modelNameMatch[1] : activeFile.name.replace(/\.[^/.]+$/, '');

  return (
    <div className="flex-1 overflow-y-auto py-2 px-3 space-y-3 text-xs select-none">
      {/* Overview Card */}
      <div className="p-2.5 rounded-lg bg-gray-900/60 border border-gray-800 space-y-2">
        <div className="flex items-center justify-between">
          <span className="font-semibold text-gray-200 truncate">{rootName}</span>
          <span className="text-[10px] font-mono font-bold px-1.5 py-0.5 rounded bg-blue-950/80 text-blue-300 border border-blue-800/60">
            {meta.badge}
          </span>
        </div>
        <p className="text-[11px] text-gray-400">{meta.type}</p>
      </div>

      {/* Metadata Table */}
      <div className="space-y-1.5 pt-1">
        <div className="flex items-center justify-between py-1 border-b border-gray-800/60 text-[11px]">
          <span className="text-gray-400">File Name</span>
          <span className="font-mono text-gray-200 truncate max-w-[150px]">{activeFile.name}</span>
        </div>

        <div className="flex items-center justify-between py-1 border-b border-gray-800/60 text-[11px]">
          <span className="text-gray-400">Lifecycle Stage</span>
          <span className="text-gray-200 text-right">{meta.stage}</span>
        </div>

        <div className="flex items-center justify-between py-1 border-b border-gray-800/60 text-[11px]">
          <span className="text-gray-400">Validation Status</span>
          {errors.length === 0 ? (
            <span className="text-emerald-400 flex items-center gap-1">
              <CheckCircle2 size={12} /> Valid
            </span>
          ) : (
            <span className="text-rose-400 flex items-center gap-1">
              <AlertCircle size={12} /> {errors.length} issues
            </span>
          )}
        </div>

        <div className="flex items-center justify-between py-1 border-b border-gray-800/60 text-[11px]">
          <span className="text-gray-400">Save State</span>
          <span className="text-gray-200">
            {isDirty ? (
              <span className="text-amber-400 flex items-center gap-1">● Unsaved edits</span>
            ) : (
              <span className="text-emerald-400 flex items-center gap-1">✓ Saved</span>
            )}
          </span>
        </div>

        <div className="flex items-center justify-between py-1 border-b border-gray-800/60 text-[11px]">
          <span className="text-gray-400">Synthesis State</span>
          <span className="text-gray-200">
            {transformResult ? (
              isSynthesisStale ? (
                <span className="text-amber-400 font-medium">↻ Stale (edited)</span>
              ) : (
                <span className="text-emerald-400 font-medium">✓ Up to date</span>
              )
            ) : (
              <span className="text-gray-500">Not synthesized</span>
            )}
          </span>
        </div>

        <div className="flex items-center justify-between py-1 border-b border-gray-800/60 text-[11px]">
          <span className="text-gray-400">Lines / Size</span>
          <span className="font-mono text-gray-300">{lineCount} lines ({charCount} B)</span>
        </div>
      </div>

      {/* Target Generators */}
      <div className="pt-2">
        <span className="text-[10px] font-bold text-gray-500 uppercase tracking-wider block mb-1.5">
          Available Code Targets
        </span>
        <div className="flex flex-wrap gap-1">
          {['Python', 'ROS2', 'Java', 'PLC ST', 'C++'].map(t => (
            <span key={t} className="text-[10px] px-1.5 py-0.5 rounded bg-gray-900 border border-gray-800 text-gray-400">
              {t}
            </span>
          ))}
        </div>
      </div>
    </div>
  );
};
