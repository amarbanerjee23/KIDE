import React, { useState } from 'react';
import { useEditorStore, useActiveFile } from '../../stores/editorStore';
import { ArtifactOutline } from './ArtifactOutline';
import { ArtifactRelations } from './ArtifactRelations';
import { ArtifactIssues } from './ArtifactIssues';
import { ArtifactInfo } from './ArtifactInfo';
import { ListTree, Network, AlertCircle, Info } from 'lucide-react';

export type CodeContextMode = 'outline' | 'relations' | 'issues' | 'info';

export const CodeContext: React.FC = () => {
  const activeFile = useActiveFile();
  const { validationErrors } = useEditorStore();
  const [mode, setMode] = useState<CodeContextMode>('outline');

  const fileIssues = activeFile ? (validationErrors[activeFile.id] || []) : [];
  const errorCount = fileIssues.filter(e => e.severity === 'error').length;
  const issueCount = fileIssues.length;

  return (
    <div className="flex-1 flex flex-col min-h-0 select-none overflow-hidden">
      {/* Sub-Mode Selector Bar */}
      <div className="px-2 py-1.5 border-b border-gray-800/80 bg-[#111622]/60 flex items-center gap-1">
        <button
          onClick={() => setMode('outline')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition ${
            mode === 'outline'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
          title="Outline: AST Structural Symbols"
        >
          <ListTree size={12} />
          <span>Outline</span>
        </button>

        <button
          onClick={() => setMode('relations')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition ${
            mode === 'relations'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
          title="Relations: Dependencies & Connected Artifacts"
        >
          <Network size={12} />
          <span>Relations</span>
        </button>

        <button
          onClick={() => setMode('issues')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition relative ${
            mode === 'issues'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
          title={`Issues: ${issueCount} validation issues in this file`}
        >
          <AlertCircle size={12} />
          <span>Issues</span>
          {issueCount > 0 && (
            <span
              className={`ml-0.5 text-[9px] font-mono px-1 py-0.2 rounded-full font-bold ${
                errorCount > 0
                  ? 'bg-rose-950/90 text-rose-300 border border-rose-800/60'
                  : 'bg-amber-950/90 text-amber-300 border border-amber-800/60'
              }`}
            >
              {issueCount}
            </span>
          )}
        </button>

        <button
          onClick={() => setMode('info')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition ${
            mode === 'info'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
          title="Info: Engineering Metadata & Stage"
        >
          <Info size={12} />
          <span>Info</span>
        </button>
      </div>

      {/* Mode Content Panel */}
      <div className="flex-1 min-h-0 flex flex-col overflow-hidden">
        {mode === 'outline' && <ArtifactOutline />}
        {mode === 'relations' && <ArtifactRelations />}
        {mode === 'issues' && <ArtifactIssues />}
        {mode === 'info' && <ArtifactInfo />}
      </div>
    </div>
  );
};

