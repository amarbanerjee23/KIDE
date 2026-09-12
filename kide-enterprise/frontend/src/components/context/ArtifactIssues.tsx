import React from 'react';
import { useEditorStore, useActiveFile } from '../../stores/editorStore';
import { navigateToDefinition } from '../../lib/monaco/xtextLanguageService';
import { AlertCircle, AlertTriangle, CheckCircle2 } from 'lucide-react';

export const ArtifactIssues: React.FC = () => {
  const activeFile = useActiveFile();
  const { validationErrors, setActiveView } = useEditorStore();

  if (!activeFile) {
    return (
      <div className="flex flex-col items-center justify-center p-6 text-center text-gray-500 h-full select-none">
        <p className="text-xs font-medium text-gray-300">No Artifact Selected</p>
      </div>
    );
  }

  const errors = validationErrors[activeFile.id] || [];

  if (errors.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center p-6 text-center text-gray-500 h-full select-none">
        <CheckCircle2 className="w-8 h-8 text-emerald-500/80 mb-2" />
        <p className="text-xs font-semibold text-gray-200">No Issues Detected</p>
        <p className="text-[11px] text-gray-500 mt-1">
          {activeFile.name} passes all grammar and semantic validation rules.
        </p>
      </div>
    );
  }

  const handleSelectIssue = (line?: number, col?: number) => {
    setActiveView('editor');
    if (line) {
      navigateToDefinition(activeFile.id, line, col || 1);
    }
  };

  const errorCount = errors.filter(e => e.severity === 'error').length;
  const warningCount = errors.filter(e => e.severity !== 'error').length;

  return (
    <div className="flex-1 overflow-y-auto py-2 px-3 select-none text-xs">
      <div className="flex items-center justify-between text-[11px] text-gray-400 font-semibold mb-2 pb-1 border-b border-gray-800/80">
        <span>ISSUES IN {activeFile.name.toUpperCase()}</span>
        <div className="flex items-center gap-2">
          {errorCount > 0 && (
            <span className="text-rose-400 font-mono flex items-center gap-1">
              <AlertCircle size={12} /> {errorCount}
            </span>
          )}
          {warningCount > 0 && (
            <span className="text-amber-400 font-mono flex items-center gap-1">
              <AlertTriangle size={12} /> {warningCount}
            </span>
          )}
        </div>
      </div>

      <div className="space-y-1.5">
        {errors.map((err, idx) => (
          <div
            key={idx}
            onClick={() => handleSelectIssue(err.line, err.column)}
            className="p-2 rounded bg-gray-900/60 hover:bg-gray-800/80 border border-gray-800 cursor-pointer transition"
          >
            <div className="flex items-start gap-2">
              {err.severity === 'error' ? (
                <AlertCircle size={13} className="text-rose-400 shrink-0 mt-0.5" />
              ) : (
                <AlertTriangle size={13} className="text-amber-400 shrink-0 mt-0.5" />
              )}
              <div className="flex-1 min-w-0">
                <div className="flex items-center justify-between mb-0.5">
                  <span className={`font-semibold text-[10px] uppercase tracking-wider ${
                    err.severity === 'error' ? 'text-rose-400' : 'text-amber-400'
                  }`}>
                    {err.severity === 'error' ? 'Error' : 'Warning'}
                  </span>
                  {err.line && (
                    <span className="font-mono text-[10px] text-gray-400 bg-gray-800/80 px-1 py-0.2 rounded">
                      Line {err.line}{err.column ? `:${err.column}` : ''}
                    </span>
                  )}
                </div>
                <p className="text-[11px] text-gray-300 leading-relaxed">
                  {err.message}
                </p>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

