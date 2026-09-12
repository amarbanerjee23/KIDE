import React, { useState, useEffect } from 'react';
import { useEditorStore, useActiveFile } from '../../stores/editorStore';
import { projectsApi } from '../../api/projects';
import { transformWorkspace } from '../../api/transform';
import { 
  FolderKanban, FileCode2, Activity, GitMerge, Sparkles, Play, 
  CheckCircle2, ChevronRight, Columns, Bot, Loader2, BookOpen, 
  Zap, ArrowRight
} from 'lucide-react';
import { KnowledgeCatalogModal } from '../knowledge/KnowledgeCatalogModal';

interface Props {
  showAssistant: boolean;
  setShowAssistant: (show: boolean | ((prev: boolean) => boolean)) => void;
}

export const UnifiedWorkspaceHeader: React.FC<Props> = ({
  showAssistant,
  setShowAssistant
}) => {
  const activeFile = useActiveFile();
  const { 
    files, projectId, projectName, dirtyFileIds, activeView, setActiveView,
    setActiveFileId, isTransforming, setIsTransforming, 
    transformResult, setTransformResult, markFileSaved 
  } = useEditorStore();

  const [isSaving, setIsSaving] = useState(false);
  const [isKnowledgeOpen, setIsKnowledgeOpen] = useState(false);
  const isCurrentFileDirty = activeFile ? dirtyFileIds.includes(activeFile.id) : false;

  // File categories & counts
  const getFname = (f: any) => f.filename || f.name || '';
  const dmlFiles = files.filter(f => getFname(f).endsWith('.dml'));
  const capFiles = files.filter(f => getFname(f).endsWith('.cap') || getFname(f).endsWith('.capability'));
  const opFiles = files.filter(f => getFname(f).endsWith('.op') || getFname(f).endsWith('.operation'));
  const actFiles = files.filter(f => getFname(f).endsWith('.activity'));
  const mncFiles = files.filter(f => getFname(f).endsWith('.mnc') || getFname(f).endsWith('.mncspec'));

  const specCount = dmlFiles.length + capFiles.length + opFiles.length;
  const isSynthesized = Boolean(transformResult?.model || mncFiles.length > 0);

  // Operating states count if synthesized
  const mAny = transformResult?.model as any;
  const iface = mAny?.systems?.[0] || mAny?.interface_description;
  const statesRaw = iface?.operating_states || iface?.operatingStates;
  const stateCount = Array.isArray(statesRaw) ? statesRaw.length : (statesRaw?.operatingStates?.length || 0);

  // Save handler with Ctrl+S
  const handleSave = async () => {
    if (!activeFile || !projectId) return;
    setIsSaving(true);
    try {
      await projectsApi.updateFile(projectId, activeFile.id, {
        content: activeFile.content,
        filename: activeFile.name
      });
      markFileSaved(activeFile.id);
    } catch (err) {
      console.error('Failed to save file:', err);
    } finally {
      setIsSaving(false);
    }
  };

  useEffect(() => {
    const onKeyDown = (e: KeyboardEvent) => {
      if ((e.ctrlKey || e.metaKey) && e.key === 's') {
        e.preventDefault();
        handleSave();
      }
    };
    window.addEventListener('keydown', onKeyDown);
    return () => window.removeEventListener('keydown', onKeyDown);
  }, [activeFile, projectId]);

  // Synthesis runner (Thesis automated transformation)
  const handleRunSynthesis = async () => {
    if (files.length === 0) return;
    setIsTransforming(true);
    try {
      const res = await transformWorkspace(files, activeFile?.id);
      setTransformResult(res);
      setActiveView('statemachine');
    } catch (err) {
      console.error('Synthesis failed:', err);
      alert(`Synthesis failed: ${err}`);
    } finally {
      setIsTransforming(false);
    }
  };

  // 5 Linear Stage Definitions
  const workflowStages = [
    {
      id: 'editor',
      label: 'Specification',
      desc: 'DML, CapML, OpML',
      icon: FileCode2,
      count: specCount > 0 ? `${specCount} Files` : undefined,
      isDone: specCount > 0,
      onClick: () => {
        if (dmlFiles.length > 0 && !dmlFiles.some(f => f.id === activeFile?.id)) {
          setActiveFileId(dmlFiles[0].id);
        }
        setActiveView('editor');
      }
    },
    {
      id: 'workflow',
      label: 'Activity Flow',
      desc: 'Process logic',
      icon: Activity,
      count: actFiles.length > 0 ? `${actFiles.length} Flow` : undefined,
      isDone: actFiles.length > 0,
      onClick: () => {
        if (actFiles.length > 0 && activeFile?.id !== actFiles[0].id) {
          setActiveFileId(actFiles[0].id);
        }
        setActiveView('workflow');
      }
    },
    {
      id: 'statemachine',
      label: 'State Machine',
      desc: 'Synthesized MNC-ML',
      icon: GitMerge,
      count: isSynthesized ? `${stateCount || '8'} States` : undefined,
      isDone: isSynthesized,
      onClick: () => setActiveView('statemachine')
    },
    {
      id: 'codegen',
      label: 'Code Studio',
      desc: 'Multi-target export',
      icon: Sparkles,
      count: '6 Targets',
      isDone: isSynthesized,
      onClick: () => setActiveView('codegen')
    },
    {
      id: 'simulator',
      label: 'Live Simulator',
      desc: 'Digital twin runner',
      icon: Play,
      count: 'Runner',
      isDone: isSynthesized,
      onClick: () => setActiveView('simulator')
    }
  ];

  return (
    <>
      <header className="h-14 bg-[#0d1117] border-b border-gray-800/80 px-4 flex items-center justify-between select-none z-20 text-gray-200">
        
        {/* LEFT: Project & File Breadcrumbs */}
        <div className="flex items-center gap-2.5 min-w-[220px] max-w-[340px]">
          <div className="p-1.5 rounded-md bg-blue-950/60 text-blue-400 border border-blue-800/40">
            <FolderKanban className="w-4 h-4" />
          </div>
          <div className="flex flex-col min-w-0">
            <div className="flex items-center gap-1.5 truncate">
              <span className="font-semibold text-xs text-gray-100 truncate" title={projectName || 'Project Workspace'}>
                {projectName || 'Project Workspace'}
              </span>
              {activeFile && (
                <>
                  <span className="text-gray-500 text-xs">/</span>
                  <span className="font-mono text-[11px] text-gray-400 truncate" title={activeFile.name}>
                    {activeFile.name}
                  </span>
                </>
              )}
            </div>
            {/* Status indicator */}
            <div className="flex items-center gap-1 text-[10px]">
              {isSaving ? (
                <span className="text-blue-400 flex items-center gap-1">
                  <Loader2 className="w-2.5 h-2.5 animate-spin" /> Saving...
                </span>
              ) : isCurrentFileDirty ? (
                <span className="text-amber-400 font-medium flex items-center gap-1">
                  <span className="w-1.5 h-1.5 rounded-full bg-amber-400 animate-pulse"></span>
                  Unsaved (Ctrl+S)
                </span>
              ) : (
                <span className="text-emerald-400/80 flex items-center gap-1">
                  <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
                  Saved
                </span>
              )}
            </div>
          </div>
        </div>

        {/* CENTER: The Unified 5-Stage Stepper & View Switcher */}
        <nav className="hidden lg:flex items-center bg-[#161b22] border border-gray-800/90 rounded-xl p-1 gap-1 shadow-inner">
          {workflowStages.map((stage, idx) => {
            const Icon = stage.icon;
            const isActive = activeView === stage.id;
            return (
              <React.Fragment key={stage.id}>
                <button
                  onClick={stage.onClick}
                  className={`flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-medium transition-all group relative ${
                    isActive
                      ? 'bg-blue-600 text-white shadow-md shadow-blue-900/30'
                      : stage.isDone
                      ? 'text-gray-300 hover:text-white hover:bg-gray-800/60'
                      : 'text-gray-500 hover:text-gray-300 hover:bg-gray-800/40'
                  }`}
                  title={`${stage.label}: ${stage.desc}`}
                >
                  <Icon className={`w-3.5 h-3.5 ${isActive ? 'text-white' : stage.isDone ? 'text-emerald-400' : 'text-gray-500'}`} />
                  <span className="font-semibold tracking-tight">{stage.label}</span>
                  {stage.count && (
                    <span className={`text-[10px] px-1.5 py-0.2 rounded-full font-mono transition-colors ${
                      isActive 
                        ? 'bg-blue-700/60 text-blue-100 border border-blue-400/30' 
                        : 'bg-gray-800 text-gray-400 group-hover:text-gray-200'
                    }`}>
                      {stage.count}
                    </span>
                  )}
                  {stage.isDone && !isActive && (
                    <CheckCircle2 className="w-3 h-3 text-emerald-500/80 ml-0.5" />
                  )}
                </button>
                {idx < workflowStages.length - 1 && (
                  <ChevronRight className="w-3 h-3 text-gray-700 shrink-0" />
                )}
              </React.Fragment>
            );
          })}
        </nav>

        {/* RIGHT: Auxiliary Utilities & Contextual Call-To-Action */}
        <div className="flex items-center gap-2">
          
          {/* Knowledge Catalog Hub */}
          <button
            onClick={() => setIsKnowledgeOpen(true)}
            className="flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-xs font-medium text-gray-300 hover:text-white hover:bg-gray-800 border border-gray-800 transition"
            title="Browse pre-indexed thesis equipment & sensor ontologies"
          >
            <BookOpen className="w-3.5 h-3.5 text-indigo-400" />
            <span className="hidden xl:inline">Knowledge Hub</span>
          </button>

          {/* Split View Toggle */}
          <button
            onClick={() => setActiveView(activeView === 'split' ? 'editor' : 'split')}
            className={`p-2 rounded-lg text-xs transition border ${
              activeView === 'split'
                ? 'bg-gray-800 text-blue-400 border-blue-500/40 shadow-sm'
                : 'text-gray-400 hover:text-white hover:bg-gray-800/80 border-gray-800'
            }`}
            title="Side-by-side Editor & Diagram preview"
          >
            <Columns className="w-3.5 h-3.5" />
          </button>

          {/* AI Copilot Panel Toggle */}
          <button
            onClick={() => setShowAssistant(prev => !prev)}
            className={`p-2 rounded-lg text-xs transition border ${
              showAssistant
                ? 'bg-blue-600/20 text-blue-400 border-blue-500/40 shadow-sm'
                : 'text-gray-400 hover:text-white hover:bg-gray-800/80 border-gray-800'
            }`}
            title="Toggle Gemini Assistant"
          >
            <Bot className="w-3.5 h-3.5 text-blue-400" />
          </button>

          {/* Contextual Smart Action Button */}
          {(activeView === 'editor' || activeView === 'workflow') && (
            <button
              onClick={handleRunSynthesis}
              disabled={isTransforming || files.length === 0}
              className="flex items-center gap-1.5 px-3.5 py-1.5 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-500 hover:to-indigo-500 disabled:opacity-50 text-white rounded-lg text-xs font-semibold shadow-md shadow-indigo-950/40 transition active:scale-[0.98]"
              title="Transform activity diagrams and capabilities into formal supervisory state machine (Thesis Chapter 5)"
            >
              {isTransforming ? (
                <>
                  <Loader2 className="w-3.5 h-3.5 animate-spin" />
                  <span>Synthesizing...</span>
                </>
              ) : (
                <>
                  <Zap className="w-3.5 h-3.5 text-amber-300 fill-amber-300" />
                  <span>Synthesize Model</span>
                </>
              )}
            </button>
          )}

          {activeView === 'statemachine' && (
            <button
              onClick={() => setActiveView('codegen')}
              className="flex items-center gap-1.5 px-3.5 py-1.5 bg-indigo-600 hover:bg-indigo-500 text-white rounded-lg text-xs font-semibold shadow-md shadow-indigo-950/40 transition active:scale-[0.98]"
            >
              <Sparkles className="w-3.5 h-3.5" />
              <span>Generate Code</span>
              <ArrowRight className="w-3 h-3 ml-0.5" />
            </button>
          )}

          {activeView === 'codegen' && (
            <button
              onClick={() => setActiveView('simulator')}
              className="flex items-center gap-1.5 px-3.5 py-1.5 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg text-xs font-semibold shadow-md shadow-emerald-950/40 transition active:scale-[0.98]"
            >
              <Play className="w-3.5 h-3.5" />
              <span>Launch Simulator</span>
              <ArrowRight className="w-3 h-3 ml-0.5" />
            </button>
          )}

          {activeView === 'simulator' && (
            <button
              onClick={() => setActiveView('codegen')}
              className="flex items-center gap-1.5 px-3.5 py-1.5 bg-gray-800 hover:bg-gray-700 text-gray-200 border border-gray-700 rounded-lg text-xs font-semibold transition"
            >
              <FileCode2 className="w-3.5 h-3.5 text-indigo-400" />
              <span>Return to Code</span>
            </button>
          )}
        </div>
      </header>

      {/* Knowledge Catalog Modal */}
      <KnowledgeCatalogModal
        isOpen={isKnowledgeOpen}
        onClose={() => setIsKnowledgeOpen(false)}
        onImportSuccess={() => {
          setIsKnowledgeOpen(false);
          // Auto-trigger refresh by reloading page or resetting store files
          window.location.reload();
        }}
      />
    </>
  );
};
