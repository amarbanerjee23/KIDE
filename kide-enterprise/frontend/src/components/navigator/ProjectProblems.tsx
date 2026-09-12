import React, { useState } from 'react';
import { useEditorStore } from '../../stores/editorStore';
import { ValidationError } from '../../types/models';
import { navigateToDefinition } from '../../lib/monaco/xtextLanguageService';
import { 
  AlertCircle, AlertTriangle, CheckCircle2, ChevronDown, ChevronRight, 
  FileCode2 
} from 'lucide-react';

export const ProjectProblems: React.FC = () => {
  const { files, validationErrors, setActiveFileId, setActiveView } = useEditorStore();
  const [collapsedFiles, setCollapsedFiles] = useState<Record<string, boolean>>({});

  // Group problems by file
  const groupedProblems: { fileId: string; fileName: string; errors: ValidationError[] }[] = [];
  let totalErrors = 0;
  let totalWarnings = 0;

  for (const file of files) {
    const errs = validationErrors[file.id] || [];
    if (errs.length > 0) {
      groupedProblems.push({
        fileId: file.id,
        fileName: file.name,
        errors: errs
      });
      for (const e of errs) {
        if (e.severity === 'error') totalErrors++;
        else totalWarnings++;
      }
    }
  }

  const toggleFileCollapse = (fileId: string) => {
    setCollapsedFiles(prev => ({ ...prev, [fileId]: !prev[fileId] }));
  };

  const handleSelectProblem = (fileId: string, err: ValidationError) => {
    setActiveFileId(fileId);
    setActiveView('editor');
    if (err.line) {
      navigateToDefinition(fileId, err.line, err.column || 1);
    }
  };

  if (groupedProblems.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center p-6 text-center text-gray-500 h-full select-none">
        <CheckCircle2 className="w-8 h-8 text-emerald-500/80 mb-2" />
        <p className="text-xs font-semibold text-gray-300">No Problems Detected</p>
        <p className="text-[11px] text-gray-500 mt-1">All project models and syntax are valid.</p>
      </div>
    );
  }

  return (
    <div className="flex-1 overflow-y-auto select-none py-2 text-xs">
      {/* Problems Summary Header */}
      <div className="px-3 py-1.5 flex items-center justify-between text-[11px] text-gray-400 border-b border-gray-800/80 mb-1">
        <span>PROJECT ISSUES</span>
        <div className="flex items-center gap-2">
          {totalErrors > 0 && (
            <span className="flex items-center gap-1 text-rose-400 font-medium">
              <AlertCircle size={12} /> {totalErrors} {totalErrors === 1 ? 'error' : 'errors'}
            </span>
          )}
          {totalWarnings > 0 && (
            <span className="flex items-center gap-1 text-amber-400 font-medium">
              <AlertTriangle size={12} /> {totalWarnings} {totalWarnings === 1 ? 'warning' : 'warnings'}
            </span>
          )}
        </div>
      </div>

      {/* Grouped Problem List */}
      <div className="space-y-1">
        {groupedProblems.map(({ fileId, fileName, errors }) => {
          const isCollapsed = Boolean(collapsedFiles[fileId]);
          return (
            <div key={fileId} className="border-b border-gray-800/40 pb-1">
              {/* File Group Header */}
              <button
                onClick={() => toggleFileCollapse(fileId)}
                className="w-full flex items-center justify-between px-3 py-1 hover:bg-gray-800/50 text-gray-200 transition text-left"
              >
                <div className="flex items-center gap-1.5 overflow-hidden flex-1">
                  {isCollapsed ? <ChevronRight size={13} className="text-gray-500" /> : <ChevronDown size={13} className="text-gray-500" />}
                  <FileCode2 size={13} className="text-blue-400 shrink-0" />
                  <span className="truncate font-medium">{fileName}</span>
                </div>
                <span className="text-[10px] text-gray-400 font-mono px-1.5 py-0.2 rounded bg-gray-800 shrink-0 ml-1.5">
                  {errors.length}
                </span>
              </button>

              {/* Problem Items */}
              {!isCollapsed && (
                <div className="pl-6 pr-2 space-y-0.5 mt-0.5">
                  {errors.map((err, idx) => (
                    <div
                      key={`${fileId}-err-${idx}`}
                      onClick={() => handleSelectProblem(fileId, err)}
                      className="group flex items-start gap-2 py-1 px-1.5 rounded hover:bg-gray-800/80 cursor-pointer text-[11px] transition-colors"
                      title={err.message}
                    >
                      {err.severity === 'error' ? (
                        <AlertCircle size={12} className="text-rose-400 shrink-0 mt-0.5" />
                      ) : (
                        <AlertTriangle size={12} className="text-amber-400 shrink-0 mt-0.5" />
                      )}
                      <div className="flex-1 min-w-0">
                        <p className="truncate text-gray-300 group-hover:text-white">
                          {err.message}
                        </p>
                      </div>
                      {err.line && (
                        <span className="text-[10px] text-gray-400 font-mono shrink-0 ml-1">
                          L{err.line}
                        </span>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
};

