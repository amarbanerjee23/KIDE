import React, { useState, useRef, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useEditorStore } from '../stores/editorStore';
import { projectsApi } from '../api/projects';
import { transformWorkspace } from '../api/transform';
import { ProjectHeader } from '../components/workflow/ProjectHeader';
import { WorkflowStepper } from '../components/workflow/WorkflowStepper';
import { WorkspaceNavigation } from '../components/workflow/WorkspaceNavigation';
import { MonacoDslEditor } from '../components/editor/MonacoDslEditor';
import { FileExplorer } from '../components/editor/FileExplorer';
import { EditorTabs } from '../components/editor/EditorTabs';
import { GeminiAssistantPanel } from '../components/editor/GeminiAssistantPanel';
import { ActivityFlowEditor } from '../components/flow/ActivityFlowEditor';
import { MncFlowViewer } from '../components/flow/MncFlowViewer';
import { LiveRunnerConsole } from '../components/simulation/LiveRunnerConsole';
import { CodeGenerationStudio } from '../components/workflow/CodeGenerationStudio';
import { KnowledgeGraphViewer } from '../components/knowledge/KnowledgeGraphViewer';
import { 
  Loader2, GitMerge, Activity, Sparkles, ArrowRight, 
  FileCode2 
} from 'lucide-react';

const ProjectWorkspacePage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const { 
    setProject, setFiles, files, activeView, setActiveView, 
    transformResult, setTransformResult, setIsTransforming, isTransforming,
    splitMode, splitLayout, aiAssistantOpen, setAiAssistantOpen,
    setActiveStage
  } = useEditorStore();

  const [isLoading, setIsLoading] = useState(true);
  const [leftExplorerWidth, setLeftExplorerWidth] = useState(20); // 20%
  const [splitRatio, setSplitRatio] = useState(50); // 50-50 in split mode
  
  const containerRef = useRef<HTMLDivElement>(null);
  const splitRef = useRef<HTMLDivElement>(null);

  const getLanguage = (filename: string) => {
    if (filename.endsWith('.json')) return 'json';
    if (filename.endsWith('.mnc')) return 'mncml';
    if (filename.endsWith('.activity')) return 'activitydsl';
    if (filename.endsWith('.cap') || filename.endsWith('.capability')) return 'capabilitydsl';
    if (filename.endsWith('.op') || filename.endsWith('.operation')) return 'operationdsl';
    if (filename.endsWith('.dml')) return 'dmldsl';
    return 'text';
  };

  // Load project and project files on mount / id change
  useEffect(() => {
    if (!id) return;
    const numId = Number(id);
    let isCancelled = false;
    setIsLoading(true);

    const loadProjectData = async () => {
      try {
        const [proj, pFiles] = await Promise.all([
          projectsApi.getProject(numId),
          projectsApi.listFiles(numId)
        ]);
        if (!isCancelled) {
          setProject(numId, proj.name);
          const mappedFiles = pFiles.map(f => ({
            id: String(f.id),
            name: f.filename || f.name || 'untitled',
            content: f.content,
            language: getLanguage(f.filename || f.name || '')
          }));
          setFiles(mappedFiles);
        }
      } catch (err) {
        console.error('Failed to load project workspace:', err);
      } finally {
        if (!isCancelled) setIsLoading(false);
      }
    };

    loadProjectData();
    return () => { isCancelled = true; };
  }, [id, setProject, setFiles]);

  // Handle Dragging File Explorer Resizer
  const handleLeftDragStart = (e: React.MouseEvent) => {
    e.preventDefault();
    document.addEventListener('mousemove', handleLeftDrag);
    document.addEventListener('mouseup', handleLeftDragEnd);
  };
  const handleLeftDrag = (e: MouseEvent) => {
    if (containerRef.current) {
      const rect = containerRef.current.getBoundingClientRect();
      const newWidth = ((e.clientX - rect.left) / rect.width) * 100;
      if (newWidth > 12 && newWidth < 38) setLeftExplorerWidth(newWidth);
    }
  };
  const handleLeftDragEnd = () => {
    document.removeEventListener('mousemove', handleLeftDrag);
    document.removeEventListener('mouseup', handleLeftDragEnd);
  };

  // Handle Dragging Split Pane Resizer
  const handleSplitDragStart = (e: React.MouseEvent) => {
    e.preventDefault();
    document.addEventListener('mousemove', handleSplitDrag);
    document.addEventListener('mouseup', handleSplitDragEnd);
  };
  const handleSplitDrag = (e: MouseEvent) => {
    if (splitRef.current) {
      const rect = splitRef.current.getBoundingClientRect();
      const newRatio = ((e.clientX - rect.left) / rect.width) * 100;
      if (newRatio > 25 && newRatio < 75) setSplitRatio(newRatio);
    }
  };
  const handleSplitDragEnd = () => {
    document.removeEventListener('mousemove', handleSplitDrag);
    document.removeEventListener('mouseup', handleSplitDragEnd);
  };

  const handleRunWorkspaceSynthesis = async () => {
    if (files.length === 0) return;
    setIsTransforming(true);
    try {
      const res = await transformWorkspace(files);
      setTransformResult(res);
      setActiveStage(4);
      setActiveView('statemachine');
    } catch (err: any) {
      alert(`Synthesis failed: ${err.message || err}`);
    } finally {
      setIsTransforming(false);
    }
  };

  if (isLoading) {
    return (
      <div className="flex-1 flex flex-col items-center justify-center bg-[#0b0f19] text-gray-400 gap-3">
        <Loader2 className="w-8 h-8 animate-spin text-blue-500" />
        <span className="text-xs font-medium tracking-wide">Loading Project Workspace...</span>
      </div>
    );
  }

  const activeModel = transformResult?.model;
  const hasActivityFiles = files.some(f => (f.name || '').endsWith('.activity'));

  // Renders a workspace component by view id
  const renderWorkspacePane = (view: string) => {
    switch (view) {
      case 'editor':
        return (
          <div ref={containerRef} className="flex flex-1 h-full min-h-0 w-full overflow-hidden">
            {/* File Explorer Panel */}
            <div style={{ width: `${leftExplorerWidth}%` }} className="h-full min-w-[180px] min-h-0">
              <FileExplorer />
            </div>

            {/* Resizer Divider */}
            <div 
              className="w-1 bg-gray-800 hover:bg-blue-500 cursor-col-resize z-10 transition-colors"
              onMouseDown={handleLeftDragStart}
              title="Drag to resize file explorer"
            />

            {/* Monaco Editor Panel */}
            <div className="flex-1 h-full min-h-0 flex flex-col min-w-[300px] overflow-hidden">
              <EditorTabs />
              <div className="flex-1 min-h-0 overflow-hidden">
                <MonacoDslEditor />
              </div>
            </div>
          </div>
        );

      case 'statemachine':
        return (
          <div className="flex-1 h-full min-h-0 w-full relative overflow-hidden">
            {activeModel ? (
              <MncFlowViewer model={activeModel} mode="statemachine" />
            ) : (
              <div className="flex flex-col items-center justify-center h-full text-center p-8 bg-[#0b0f19]">
                <div className="p-4 rounded-2xl bg-blue-950/50 border border-blue-800/60 text-blue-400 mb-4 shadow-xl">
                  <GitMerge className="w-10 h-10" />
                </div>
                <h3 className="text-base font-bold text-gray-100 mb-2">No State Machine Generated Yet</h3>
                <p className="text-xs text-gray-400 max-w-md mb-6 leading-relaxed">
                  Run the automated synthesis compiler to derive formal supervisory states, transitions, 
                  and command/event blocks from your activity diagram.
                </p>
                <button
                  onClick={handleRunWorkspaceSynthesis}
                  disabled={isTransforming || files.length === 0}
                  className="px-4 py-2 bg-blue-600 hover:bg-blue-500 disabled:opacity-50 text-white rounded-lg text-xs font-semibold flex items-center gap-2 shadow-lg shadow-blue-600/30 transition"
                >
                  {isTransforming ? <Loader2 className="w-4 h-4 animate-spin" /> : <Sparkles className="w-4 h-4" />}
                  <span>Synthesize Model From Workspace</span>
                  <ArrowRight className="w-3.5 h-3.5" />
                </button>
              </div>
            )}
          </div>
        );

      case 'workflow':
        return (
          <div className="flex-1 h-full min-h-0 w-full relative overflow-hidden">
            {hasActivityFiles ? (
              <ActivityFlowEditor />
            ) : (
              <div className="flex flex-col items-center justify-center h-full text-center p-8 bg-[#0b0f19]">
                <div className="p-4 rounded-2xl bg-indigo-950/50 border border-indigo-800/60 text-indigo-400 mb-4 shadow-xl">
                  <Activity className="w-10 h-10" />
                </div>
                <h3 className="text-base font-bold text-gray-100 mb-2">No Activity Diagram Yet</h3>
                <p className="text-xs text-gray-400 max-w-md mb-6 leading-relaxed">
                  Activity diagrams orchestrate supervisory process logic and define sequential actions, 
                  loops, and conditional branches. Create an `.activity` file to design your process flow.
                </p>
                <button
                  onClick={() => setActiveView('editor')}
                  className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-lg text-xs font-semibold flex items-center gap-2 shadow-lg shadow-indigo-600/30 transition"
                >
                  <FileCode2 className="w-4 h-4" />
                  <span>Open Code Editor & Create .activity</span>
                </button>
              </div>
            )}
          </div>
        );

      case 'simulator':
        return (
          <div className="flex-1 h-full min-h-0 w-full p-4 overflow-hidden bg-[#0b0f19]">
            {Number(id) ? (
              <LiveRunnerConsole projectId={Number(id)} />
            ) : (
              <div className="flex items-center justify-center h-full text-gray-500 text-xs">
                No active project selected for simulation.
              </div>
            )}
          </div>
        );

      case 'codegen':
        return (
          <div className="flex-1 h-full min-h-0 w-full overflow-hidden">
            <CodeGenerationStudio isEmbedded={true} />
          </div>
        );

      case 'knowledgegraph':
        return (
          <div className="flex-1 h-full min-h-0 w-full overflow-hidden">
            <KnowledgeGraphViewer />
          </div>
        );

      default:
        return null;
    }
  };

  // Render Split layout combinations
  const renderSplitContent = () => {
    let leftView = 'editor';
    let rightView = 'workflow';

    if (splitLayout === 'code-statemachine') {
      leftView = 'editor';
      rightView = 'statemachine';
    } else if (splitLayout === 'workflow-simulator') {
      leftView = 'workflow';
      rightView = 'simulator';
    }

    return (
      <div ref={splitRef} className="flex flex-1 min-h-0 w-full h-full overflow-hidden">
        {/* Left Pane */}
        <div style={{ width: `${splitRatio}%` }} className="h-full min-h-0 overflow-hidden">
          {renderWorkspacePane(leftView)}
        </div>

        {/* Resizer */}
        <div 
          className="w-1 bg-gray-800 hover:bg-blue-500 cursor-col-resize z-20 transition-colors"
          onMouseDown={handleSplitDragStart}
          title="Drag to adjust split pane ratio"
        />

        {/* Right Pane */}
        <div style={{ width: `${100 - splitRatio}%` }} className="h-full min-h-0 overflow-hidden">
          {renderWorkspacePane(rightView)}
        </div>
      </div>
    );
  };

  return (
    <div className="flex flex-col flex-1 h-full w-full min-h-0 overflow-hidden bg-[#0b0f19] relative">
      {/* Region B: Compact Project Header */}
      <ProjectHeader />

      {/* Region C: Four-Stage Workflow Stepper */}
      <WorkflowStepper />

      {/* Region D: Workspace Navigation Tabs */}
      <WorkspaceNavigation />

      {/* Region E: Main Workspace Area & Persistent AI Assistant */}
      <div className="flex flex-1 min-h-0 w-full overflow-hidden relative">
        {/* Main Work Area */}
        <div className="flex-1 flex min-h-0 w-full h-full overflow-hidden">
          {splitMode ? renderSplitContent() : renderWorkspacePane(activeView)}
        </div>

        {/* Persistent Contextual AI Assistant Drawer */}
        {aiAssistantOpen && (
          <GeminiAssistantPanel onClose={() => setAiAssistantOpen(false)} />
        )}
      </div>
    </div>
  );
};

export default ProjectWorkspacePage;
