import React, { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { useEditorStore, useActiveFile } from '../../stores/editorStore';
import { projectsApi } from '../../api/projects';
import { transformWorkspace, transformData } from '../../api/transform';
import { parseText } from '../../api/parse';
import { validateSemantic } from '../../api/validate';
import { downloadZipBundle } from '../../api/export';
import { 
  getWorkflowStagesStatus, getPrimaryProjectAction, PrimaryAction 
} from '../../utils/workflowState';
import { KnowledgeCatalogModal } from '../knowledge/KnowledgeCatalogModal';
import { 
  FolderKanban, ChevronRight, CheckCircle2, AlertTriangle, 
  Loader2, Sparkles, Zap, ChevronDown, BookOpen, Network, 
  Columns, Bot, Download, Layers, Code2, RefreshCw, 
  Check, Play
} from 'lucide-react';

export const ProjectHeader: React.FC = () => {
  const activeFile = useActiveFile();
  const { 
    files, projectId, projectName, dirtyFileIds, activeView, setActiveView,
    isTransforming, setIsTransforming, isParsing, setIsParsing,
    isSaving, setIsSaving, transformResult, setTransformResult,
    isSynthesisStale, validationErrors, setValidationErrors,
    markFileSaved, setParsedModel, setActiveStage,
    splitMode, setSplitMode, aiAssistantOpen, setAiAssistantOpen
  } = useEditorStore();

  const [isKnowledgeCatalogOpen, setIsKnowledgeCatalogOpen] = useState(false);
  const [isKnowledgeMenuOpen, setIsKnowledgeMenuOpen] = useState(false);
  const [isActionsMenuOpen, setIsActionsMenuOpen] = useState(false);
  const [validationSuccessPill, setValidationSuccessPill] = useState<string | null>(null);

  const knowledgeMenuRef = useRef<HTMLDivElement>(null);
  const actionsMenuRef = useRef<HTMLDivElement>(null);

  // Close menus on outside click
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (knowledgeMenuRef.current && !knowledgeMenuRef.current.contains(e.target as Node)) {
        setIsKnowledgeMenuOpen(false);
      }
      if (actionsMenuRef.current && !actionsMenuRef.current.contains(e.target as Node)) {
        setIsActionsMenuOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Compute stages status & primary CTA
  const stages = getWorkflowStagesStatus(
    files,
    transformResult,
    validationErrors,
    dirtyFileIds,
    isSynthesisStale
  );

  const primaryAction: PrimaryAction = getPrimaryProjectAction(
    activeView,
    stages,
    isSynthesisStale,
    isTransforming,
    isSaving,
    isParsing,
    dirtyFileIds,
    Boolean(activeFile)
  );

  const isCurrentFileDirty = activeFile ? dirtyFileIds.includes(activeFile.id) : false;

  // Save handler
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

  // Keyboard shortcut Ctrl+S
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

  // Semantic Validate
  const handleValidate = async () => {
    setValidationSuccessPill(null);
    const errors = await validateSemantic(files);
    const grouped: Record<string, any[]> = {};
    errors.forEach(e => {
      const id = e.fileId || (activeFile ? activeFile.id : '');
      if (!grouped[id]) grouped[id] = [];
      grouped[id].push(e);
    });
    
    // Clear previous or set new
    files.forEach(f => {
      setValidationErrors(f.id, grouped[f.id] || []);
    });

    if (errors.length === 0) {
      setValidationSuccessPill('✓ Valid');
      setTimeout(() => setValidationSuccessPill(null), 3500);
    } else {
      setValidationSuccessPill(`${errors.length} issue${errors.length > 1 ? 's' : ''}`);
    }
  };

  // Automated Synthesis
  const handleSynthesize = async () => {
    if (files.length === 0) return;
    setIsTransforming(true);
    try {
      const res = await transformWorkspace(files, activeFile?.id);
      setTransformResult(res);
      setActiveStage(4);
      setActiveView('statemachine');
    } catch (err: any) {
      alert(`Synthesis failed: ${err.message || err}`);
    } finally {
      setIsTransforming(false);
    }
  };

  // Parse Current DSL
  const handleParse = async () => {
    if (!activeFile) return;
    setIsParsing(true);
    try {
      const result = await parseText(activeFile.language, activeFile.content);
      setParsedModel(activeFile.id, result.ast);
      setValidationErrors(activeFile.id, result.errors);
    } catch (err: any) {
      console.error('Parse failed:', err);
    } finally {
      setIsParsing(false);
    }
  };

  // Transform single file
  const handleTransformSingle = async () => {
    if (!activeFile) return;
    setIsTransforming(true);
    try {
      const parsed = await parseText(activeFile.language, activeFile.content);
      setParsedModel(activeFile.id, parsed.ast);
      const res = await transformData(parsed.ast);
      setTransformResult(res);
      setActiveView('statemachine');
    } catch (err: any) {
      alert(`Transform failed: ${err.message || err}`);
    } finally {
      setIsTransforming(false);
    }
  };

  // Export ZIP
  const handleExportZip = async () => {
    if (!transformResult?.model) {
      alert('Please synthesize a supervisory model first.');
      return;
    }
    try {
      await downloadZipBundle(transformResult.model);
    } catch (err: any) {
      alert(`Export failed: ${err.message || err}`);
    }
  };

  // Primary action click dispatcher
  const handleExecutePrimaryAction = () => {
    switch (primaryAction.type) {
      case 'save':
        handleSave();
        break;
      case 'parse':
        handleParse();
        break;
      case 'validate':
      case 'view_errors':
        handleValidate();
        break;
      case 'synthesize':
      case 'resynthesize':
        handleSynthesize();
        break;
      case 'generate':
        setActiveView('codegen');
        break;
      case 'simulate':
        setActiveView('simulator');
        break;
      default:
        handleSynthesize();
    }
  };

  // DSL language badge
  const getLanguageLabel = (lang?: string) => {
    switch (lang) {
      case 'dmldsl': return 'DML';
      case 'capabilitydsl': return 'CAP';
      case 'operationdsl': return 'OP';
      case 'activitydsl': return 'ACT';
      case 'mncml': return 'MNC';
      case 'json': return 'JSON';
      default: return 'DSL';
    }
  };

  return (
    <>
      <header 
        aria-label="Project Header"
        className="h-14 min-h-[56px] bg-[#161b22] border-b border-gray-800 px-4 flex items-center justify-between select-none z-20 text-gray-200 shrink-0"
      >
        {/* LEFT: Structural Breadcrumbs & Save State */}
        <div className="flex items-center gap-3 min-w-0 max-w-[48%]">
          <Link 
            to="/projects"
            className="flex items-center gap-1.5 text-xs text-gray-400 hover:text-white transition shrink-0"
            title="Return to Projects List"
          >
            <FolderKanban className="w-4 h-4 text-blue-400" />
            <span className="hidden sm:inline font-medium">Projects</span>
          </Link>

          <ChevronRight className="w-3.5 h-3.5 text-gray-600 shrink-0" />

          {/* Project Name */}
          <span 
            className="text-xs font-semibold text-gray-200 truncate max-w-[140px] md:max-w-[180px]"
            title={projectName || 'Project'}
          >
            {projectName || 'Project'}
          </span>

          {activeFile && (
            <>
              <ChevronRight className="w-3.5 h-3.5 text-gray-600 shrink-0" />
              <div className="flex items-center gap-1.5 min-w-0 truncate">
                <span className="font-mono text-xs text-gray-300 truncate" title={activeFile.name}>
                  {activeFile.name}
                </span>
                <span className="text-[10px] font-mono px-1.5 py-0.2 rounded bg-gray-800 text-gray-400 border border-gray-700/60 shrink-0">
                  {getLanguageLabel(activeFile.language)}
                </span>
              </div>
            </>
          )}

          {/* Divider */}
          <div className="h-4 w-[1px] bg-gray-800 shrink-0 hidden sm:block" />

          {/* Clear Save Status Semantics */}
          <div className="hidden sm:flex items-center text-xs shrink-0">
            {isSaving ? (
              <span className="text-blue-400 flex items-center gap-1 font-medium">
                <Loader2 className="w-3 h-3 animate-spin" />
                <span>Saving...</span>
              </span>
            ) : isCurrentFileDirty ? (
              <button
                onClick={handleSave}
                className="text-amber-400 hover:text-amber-300 flex items-center gap-1 font-medium group transition"
                title="Unsaved changes. Click to save (Ctrl+S)"
              >
                <span className="w-1.5 h-1.5 rounded-full bg-amber-400 animate-pulse"></span>
                <span>Unsaved</span>
                <span className="text-[10px] text-amber-400/60 hidden md:inline">(Ctrl+S)</span>
              </button>
            ) : (
              <span className="text-emerald-400/80 flex items-center gap-1 font-medium">
                <Check className="w-3 h-3 text-emerald-400" />
                <span>Saved</span>
              </span>
            )}
          </div>
        </div>

        {/* RIGHT: Contextual Command Hierarchy & Utilities */}
        <div className="flex items-center gap-2 shrink-0">
          
          {/* 1. Knowledge Menu (Hub & Graph) */}
          <div className="relative" ref={knowledgeMenuRef}>
            <button
              onClick={() => setIsKnowledgeMenuOpen(prev => !prev)}
              aria-expanded={isKnowledgeMenuOpen}
              className="flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-xs font-medium text-gray-300 hover:text-white hover:bg-gray-800/80 border border-gray-800 transition"
              title="External Knowledge Hub & Connected Knowledge Graph"
            >
              <BookOpen className="w-3.5 h-3.5 text-indigo-400" />
              <span className="hidden md:inline">Knowledge</span>
              <ChevronDown className="w-3 h-3 text-gray-500" />
            </button>

            {isKnowledgeMenuOpen && (
              <div className="absolute right-0 mt-1.5 w-52 bg-[#1c2128] border border-gray-700/80 rounded-xl shadow-xl py-1 z-50 animate-in fade-in-50 zoom-in-95">
                <button
                  onClick={() => {
                    setIsKnowledgeMenuOpen(false);
                    setIsKnowledgeCatalogOpen(true);
                  }}
                  className="w-full text-left px-3 py-2 text-xs text-gray-200 hover:bg-gray-800 flex items-center gap-2.5 transition"
                >
                  <BookOpen className="w-3.5 h-3.5 text-indigo-400 shrink-0" />
                  <div>
                    <div className="font-semibold">Equipment Catalog</div>
                    <div className="text-[10px] text-gray-400">Pre-indexed sensor ontologies</div>
                  </div>
                </button>
                <button
                  onClick={() => {
                    setIsKnowledgeMenuOpen(false);
                    setActiveView('knowledgegraph');
                  }}
                  className="w-full text-left px-3 py-2 text-xs text-gray-200 hover:bg-gray-800 flex items-center gap-2.5 transition"
                >
                  <Network className="w-3.5 h-3.5 text-purple-400 shrink-0" />
                  <div>
                    <div className="font-semibold">Knowledge Graph</div>
                    <div className="text-[10px] text-gray-400">Explore connected thesis ontology</div>
                  </div>
                </button>
              </div>
            )}
          </div>

          {/* 2. Secondary Action: Validate */}
          <button
            onClick={handleValidate}
            className="flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-xs font-medium text-gray-300 hover:text-white hover:bg-gray-800/80 border border-gray-800 transition"
            title="Cross-validate all project files against semantic rules"
          >
            <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
            <span className="hidden sm:inline">Validate</span>
            {validationSuccessPill && (
              <span className="text-[10px] px-1.5 py-0.2 rounded bg-emerald-950 text-emerald-300 border border-emerald-800 font-mono">
                {validationSuccessPill}
              </span>
            )}
          </button>

          {/* 3. Actions ▾ (Advanced execution menu) */}
          <div className="relative" ref={actionsMenuRef}>
            <button
              onClick={() => setIsActionsMenuOpen(prev => !prev)}
              aria-expanded={isActionsMenuOpen}
              className="flex items-center gap-1 px-2.5 py-1.5 rounded-lg text-xs font-medium text-gray-400 hover:text-gray-200 hover:bg-gray-800/80 border border-gray-800 transition"
              title="Execution actions and transformations"
            >
              <span>Actions</span>
              <ChevronDown className="w-3 h-3 text-gray-500" />
            </button>

            {isActionsMenuOpen && (
              <div className="absolute right-0 mt-1.5 w-56 bg-[#1c2128] border border-gray-700/80 rounded-xl shadow-xl py-1 z-50 animate-in fade-in-50 zoom-in-95">
                <button
                  onClick={() => {
                    setIsActionsMenuOpen(false);
                    handleParse();
                  }}
                  disabled={!activeFile || isParsing}
                  className="w-full text-left px-3 py-2 text-xs text-gray-200 hover:bg-gray-800 flex items-center gap-2 transition disabled:opacity-40"
                >
                  <Code2 className="w-3.5 h-3.5 text-blue-400 shrink-0" />
                  <span>Parse Current DSL</span>
                </button>
                <button
                  onClick={() => {
                    setIsActionsMenuOpen(false);
                    handleTransformSingle();
                  }}
                  disabled={!activeFile || isTransforming}
                  className="w-full text-left px-3 py-2 text-xs text-gray-200 hover:bg-gray-800 flex items-center gap-2 transition disabled:opacity-40"
                >
                  <RefreshCw className="w-3.5 h-3.5 text-indigo-400 shrink-0" />
                  <span>Transform File to MNC</span>
                </button>
                <button
                  onClick={() => {
                    setIsActionsMenuOpen(false);
                    handleSynthesize();
                  }}
                  disabled={files.length === 0 || isTransforming}
                  className="w-full text-left px-3 py-2 text-xs text-gray-200 hover:bg-gray-800 flex items-center gap-2 transition disabled:opacity-40"
                >
                  <Layers className="w-3.5 h-3.5 text-purple-400 shrink-0" />
                  <span>Workspace Multi-DSL Transform</span>
                </button>
                <div className="my-1 border-t border-gray-700/60" />
                <button
                  onClick={() => {
                    setIsActionsMenuOpen(false);
                    handleExportZip();
                  }}
                  disabled={!transformResult?.model}
                  className="w-full text-left px-3 py-2 text-xs text-gray-200 hover:bg-gray-800 flex items-center gap-2 transition disabled:opacity-40"
                >
                  <Download className="w-3.5 h-3.5 text-emerald-400 shrink-0" />
                  <span>Export Suite (.ZIP)</span>
                </button>
              </div>
            )}
          </div>

          {/* 4. Single Dominant Primary Next Action (State-Driven) */}
          <button
            onClick={handleExecutePrimaryAction}
            disabled={primaryAction.disabled}
            title={primaryAction.tooltip}
            className={`flex items-center gap-1.5 px-3.5 py-1.5 rounded-lg text-xs font-semibold shadow-md transition active:scale-[0.98] ${
              primaryAction.variant === 'warning'
                ? 'bg-amber-600 hover:bg-amber-500 text-gray-950 shadow-amber-950/40'
                : primaryAction.variant === 'accent'
                ? 'bg-emerald-600 hover:bg-emerald-500 text-white shadow-emerald-950/40'
                : 'bg-blue-600 hover:bg-blue-500 text-white shadow-blue-950/40'
            } disabled:opacity-50 disabled:cursor-not-allowed`}
          >
            {isTransforming ? (
              <Loader2 className="w-3.5 h-3.5 animate-spin" />
            ) : primaryAction.type === 'synthesize' || primaryAction.type === 'resynthesize' ? (
              <Zap className="w-3.5 h-3.5 text-amber-300 fill-amber-300" />
            ) : primaryAction.type === 'generate' ? (
              <Sparkles className="w-3.5 h-3.5" />
            ) : primaryAction.type === 'simulate' ? (
              <Play className="w-3.5 h-3.5" />
            ) : primaryAction.type === 'view_errors' ? (
              <AlertTriangle className="w-3.5 h-3.5" />
            ) : (
              <Zap className="w-3.5 h-3.5" />
            )}
            <span>{primaryAction.label}</span>
          </button>

          {/* 5. Split Layout Mode Toggle */}
          <button
            onClick={() => setSplitMode(prev => !prev)}
            aria-pressed={splitMode}
            className={`p-2 rounded-lg text-xs transition border ${
              splitMode
                ? 'bg-blue-600/20 text-blue-400 border-blue-500/40 shadow-sm'
                : 'text-gray-400 hover:text-white hover:bg-gray-800/80 border-gray-800'
            }`}
            title={splitMode ? "Exit Split View" : "Toggle Split Layout (Editor + Diagram/Runner)"}
          >
            <Columns className="w-3.5 h-3.5" />
          </button>

          {/* 6. AI Assistant Drawer Toggle */}
          <button
            onClick={() => setAiAssistantOpen(prev => !prev)}
            aria-expanded={aiAssistantOpen}
            className={`flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-xs font-medium transition border ${
              aiAssistantOpen
                ? 'bg-blue-600/20 text-blue-400 border-blue-500/40 shadow-sm'
                : 'text-gray-300 hover:text-white hover:bg-gray-800/80 border-gray-800'
            }`}
            title="Toggle Contextual Gemini AI Assistant (Alt+A)"
          >
            <Bot className="w-3.5 h-3.5 text-blue-400" />
            <span className="hidden xl:inline">AI Assistant</span>
          </button>
        </div>
      </header>

      {/* Equipment Catalog Modal */}
      <KnowledgeCatalogModal
        isOpen={isKnowledgeCatalogOpen}
        onClose={() => setIsKnowledgeCatalogOpen(false)}
        onImportSuccess={() => {
          setIsKnowledgeCatalogOpen(false);
          window.location.reload();
        }}
      />
    </>
  );
};
