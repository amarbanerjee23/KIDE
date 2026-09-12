import React, { useState } from 'react';
import { useEditorStore } from '../../stores/editorStore';
import { NavigatorMode } from '../../types/navigator';
import { ProjectExplorer } from './ProjectExplorer';
import { ProjectSearch } from './ProjectSearch';
import { ProjectProblems } from './ProjectProblems';
import { ProjectOutline } from './ProjectOutline';
import { ProjectJobStatus } from './ProjectJobStatus';
import { NewArtifactModal } from './NewArtifactModal';
import { 
  FolderTree, Search, AlertCircle, ListTree, Plus 
} from 'lucide-react';

interface ProjectNavigatorProps {
  activeMode?: NavigatorMode;
  onModeChange?: (mode: NavigatorMode) => void;
}

export const ProjectNavigator: React.FC<ProjectNavigatorProps> = ({
  activeMode: controlledMode,
  onModeChange
}) => {
  const { projectName, projectId, validationErrors } = useEditorStore();
  const [internalMode, setInternalMode] = useState<NavigatorMode>('explorer');
  const [isNewModalOpen, setIsNewModalOpen] = useState(false);

  const mode = controlledMode || internalMode;
  const setMode = (m: NavigatorMode) => {
    if (onModeChange) onModeChange(m);
    else setInternalMode(m);
  };

  // Calculate total problems for the badge
  const totalProblems = Object.values(validationErrors).reduce(
    (acc, errs) => acc + (errs ? errs.length : 0), 0
  );
  const hasErrors = Object.values(validationErrors).some(
    errs => errs && errs.some(e => e.severity === 'error')
  );

  return (
    <div className="flex-1 min-h-0 flex flex-col select-none bg-[#0d1117] border-t border-gray-800/80 overflow-hidden">
      {/* 1. Project Context Header */}
      <div className="px-3 py-2 border-b border-gray-800/60 flex items-center justify-between bg-[#111622]/80">
        <div className="min-w-0 flex-1 mr-2">
          <div className="flex items-center gap-1.5 text-[10px] uppercase font-bold text-gray-400 tracking-wider">
            <span>PROJECT</span>
            {projectId && (
              <span className="font-mono text-gray-400 px-1 py-0.2 rounded bg-gray-800/90 text-[9px] border border-gray-700/60">
                PR#{projectId}
              </span>
            )}
          </div>
          <p className="text-xs font-semibold text-gray-200 truncate mt-0.5" title={projectName || 'Current Project'}>
            {projectName || 'Current Project'}
          </p>
        </div>

        {/* Small '+' Create Artifact Button */}
        <button
          onClick={() => setIsNewModalOpen(true)}
          className="p-1 rounded text-gray-400 hover:text-white hover:bg-gray-800 transition shrink-0"
          title="Create New Project Artifact (+)"
          aria-label="Create New Project Artifact"
        >
          <Plus size={14} />
        </button>
      </div>

      {/* 2. Compact Mode Selector */}
      <div className="px-2 py-1.5 border-b border-gray-800/60 bg-[#0b0f19]/40 flex items-center gap-1">
        <button
          onClick={() => setMode('explorer')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition ${
            mode === 'explorer'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
          title="Explorer: Project Artifact Tree"
        >
          <FolderTree size={12} />
          <span>Explorer</span>
        </button>

        <button
          onClick={() => setMode('search')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition ${
            mode === 'search'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
          title="Search: Fast Project-Local Search"
        >
          <Search size={12} />
          <span>Search</span>
        </button>

        <button
          onClick={() => setMode('problems')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition relative ${
            mode === 'problems'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
          title={`Problems: ${totalProblems} Issues`}
        >
          <AlertCircle size={12} />
          <span>Issues</span>
          {totalProblems > 0 && (
            <span
              className={`ml-0.5 text-[9px] font-mono px-1 py-0.2 rounded-full font-bold ${
                hasErrors ? 'bg-rose-950/80 text-rose-300 border border-rose-800/60' : 'bg-amber-950/80 text-amber-300 border border-amber-800/60'
              }`}
            >
              {totalProblems}
            </span>
          )}
        </button>

        <button
          onClick={() => setMode('outline')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition ${
            mode === 'outline'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
          title="Outline: Active File Symbol Hierarchy"
        >
          <ListTree size={12} />
          <span>Outline</span>
        </button>
      </div>

      {/* 3. Active Mode View Panel */}
      <div className="flex-1 min-h-0 flex flex-col overflow-hidden">
        {mode === 'explorer' && (
          <ProjectExplorer onOpenNewArtifactModal={() => setIsNewModalOpen(true)} />
        )}
        {mode === 'search' && <ProjectSearch />}
        {mode === 'problems' && <ProjectProblems />}
        {mode === 'outline' && <ProjectOutline />}
      </div>

      {/* 4. Background Job Status */}
      <ProjectJobStatus />

      {/* New Artifact Modal */}
      <NewArtifactModal
        isOpen={isNewModalOpen}
        onClose={() => setIsNewModalOpen(false)}
      />
    </div>
  );
};
