import React, { useState, useRef, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { MonacoDslEditor } from '../components/editor/MonacoDslEditor';
import { UnifiedWorkspaceHeader } from '../components/workflow/UnifiedWorkspaceHeader';
import { FileExplorer } from '../components/editor/FileExplorer';
import { EditorTabs } from '../components/editor/EditorTabs';
import GeminiAssistantPanel from '../components/editor/GeminiAssistantPanel';
import { ActivityFlowEditor } from '../components/flow/ActivityFlowEditor';
import { MncFlowViewer } from '../components/flow/MncFlowViewer';
import { LiveRunnerConsole } from '../components/simulation/LiveRunnerConsole';
import { CodeGenerationStudio } from '../components/workflow/CodeGenerationStudio';
import { TransformOutput } from '../components/output/TransformOutput';
import { Loader2, GitMerge, Sparkles, ArrowRight } from 'lucide-react';
import { useEditorStore } from '../stores/editorStore';
import { projectsApi } from '../api/projects';
import { transformWorkspace } from '../api/transform';

const ProjectWorkspacePage = () => {
  const { id } = useParams<{ id: string }>();
  const { 
    setProject, setFiles, files, activeView, setActiveView, 
    transformResult, setTransformResult, setIsTransforming, isTransforming 
  } = useEditorStore();
  const [isLoading, setIsLoading] = useState(true);

  const [leftWidth, setLeftWidth] = useState(20); // 20% for File Explorer
  const [middleWidth, setMiddleWidth] = useState(45); // 45% for Editor in Split
  const [topHeight, setTopHeight] = useState(55); // percentage for Flow in Split
  const [showAssistant, setShowAssistant] = useState(false);
  
  const containerRef = useRef<HTMLDivElement>(null);
  const rightContainerRef = useRef<HTMLDivElement>(null);

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
      if (newWidth > 10 && newWidth < 40) setLeftWidth(newWidth);
    }
  };
  const handleLeftDragEnd = () => {
    document.removeEventListener('mousemove', handleLeftDrag);
    document.removeEventListener('mouseup', handleLeftDragEnd);
  };

  // Handle Dragging Editor Resizer (Split mode)
  const handleMiddleDragStart = (e: React.MouseEvent) => {
    e.preventDefault();
    document.addEventListener('mousemove', handleMiddleDrag);
    document.addEventListener('mouseup', handleMiddleDragEnd);
  };
  const handleMiddleDrag = (e: MouseEvent) => {
    if (containerRef.current) {
      const rect = containerRef.current.getBoundingClientRect();
      const totalWidth = ((e.clientX - rect.left) / rect.width) * 100;
      const newMiddle = totalWidth - leftWidth;
      if (newMiddle > 20 && newMiddle < 60) setMiddleWidth(newMiddle);
    }
  };
  const handleMiddleDragEnd = () => {
    document.removeEventListener('mousemove', handleMiddleDrag);
    document.removeEventListener('mouseup', handleMiddleDragEnd);
  };

  // Handle Dragging Vertical Split in Right Panel
  const handleVDragStart = (e: React.MouseEvent) => {
    e.preventDefault();
    document.addEventListener('mousemove', handleVDrag);
    document.addEventListener('mouseup', handleVDragEnd);
  };
  const handleVDrag = (e: MouseEvent) => {
    if (rightContainerRef.current) {
      const rect = rightContainerRef.current.getBoundingClientRect();
      const newHeight = ((e.clientY - rect.top) / rect.height) * 100;
      if (newHeight > 20 && newHeight < 80) setTopHeight(newHeight);
    }
  };
  const handleVDragEnd = () => {
    document.removeEventListener('mousemove', handleVDrag);
    document.removeEventListener('mouseup', handleVDragEnd);
  };

  const handleRunWorkspaceSynthesis = async () => {
    if (files.length === 0) return;
    setIsTransforming(true);
    try {
      const res = await transformWorkspace(files);
      setTransformResult(res);
      setActiveView('statemachine');
    } catch (err) {
      alert(`Synthesis failed: ${err}`);
    } finally {
      setIsTransforming(false);
    }
  };

  if (isLoading) {
    return (
      <div className="flex-1 flex flex-col items-center justify-center bg-[#0d1117] text-gray-400 gap-3">
        <Loader2 className="w-8 h-8 animate-spin text-blue-500" />
        <span className="text-sm">Loading Project Workspace...</span>
      </div>
    );
  }

  const activeModel = transformResult?.model;

  return (
    <div className="flex flex-col h-full bg-background overflow-hidden relative">
      <UnifiedWorkspaceHeader 
        showAssistant={showAssistant} 
        setShowAssistant={setShowAssistant} 
      />
      
      {/* Dynamic View Body according to activeView */}
      <div ref={containerRef} className="flex flex-1 overflow-hidden relative">
        
        {/* VIEW 1: CLEAN CODE EDITOR MODE */}
        {activeView === 'editor' && (
          <div className="flex flex-1 h-full overflow-hidden">
            {/* File Explorer Panel */}
            <div style={{ width: `${leftWidth}%` }} className="h-full min-w-[180px]">
              <FileExplorer />
            </div>

            {/* Resizer */}
            <div 
              className="w-1 bg-accent hover:bg-highlight cursor-col-resize z-10 transition-colors"
              onMouseDown={handleLeftDragStart}
            />

            {/* Monaco Editor Panel */}
            <div className="flex-1 h-full flex flex-col min-w-[300px]">
              <EditorTabs />
              <div className="flex-1 overflow-hidden">
                <MonacoDslEditor />
              </div>
            </div>
          </div>
        )}

        {/* VIEW 2: FULL-CANVAS SUPERVISORY STATE MACHINE */}
        {activeView === 'statemachine' && (
          <div className="flex-1 h-full relative">
            {activeModel ? (
              <MncFlowViewer model={activeModel} mode="statemachine" />
            ) : (
              <div className="flex flex-col items-center justify-center h-full text-center p-8 bg-[#0b0f19]">
                <div className="p-4 rounded-full bg-blue-950/60 border border-blue-800/80 text-blue-400 mb-4">
                  <GitMerge className="w-10 h-10" />
                </div>
                <h3 className="text-lg font-bold text-white mb-2">No Synthesized Model Yet</h3>
                <p className="text-xs text-gray-400 max-w-md mb-6 leading-relaxed">
                  Run the thesis synthesis engine to automatically generate the full supervisory state machine, 
                  operating states, lifecycle commands, events, and validation blocks from your activity diagram.
                </p>
                <button
                  onClick={handleRunWorkspaceSynthesis}
                  disabled={isTransforming}
                  className="px-4 py-2 bg-blue-600 hover:bg-blue-500 disabled:opacity-50 text-white rounded-lg text-xs font-semibold flex items-center gap-2 shadow-lg shadow-blue-600/30 transition"
                >
                  {isTransforming ? <Loader2 className="w-4 h-4 animate-spin" /> : <Sparkles className="w-4 h-4" />}
                  <span>Synthesize Model From Workspace</span>
                  <ArrowRight className="w-3.5 h-3.5" />
                </button>
              </div>
            )}
          </div>
        )}

        {/* VIEW 3: FULL-CANVAS ACTIVITY WORKFLOW */}
        {activeView === 'workflow' && (
          <div className="flex-1 h-full relative">
            <ActivityFlowEditor />
          </div>
        )}

        {/* VIEW 4: FULL-CANVAS LIVE SIMULATOR */}
        {activeView === 'simulator' && (
          <div className="flex-1 h-full p-4 overflow-hidden bg-[#0b0f19]">
            {Number(id) ? (
              <LiveRunnerConsole projectId={Number(id)} />
            ) : (
              <div className="flex items-center justify-center h-full text-gray-400 text-xs">
                No active project selected.
              </div>
            )}
          </div>
        )}

        {/* VIEW 5: CODE GENERATION STUDIO & TEMPLATES */}
        {activeView === 'codegen' && (
          <div className="flex-1 h-full overflow-hidden">
            <CodeGenerationStudio isEmbedded={true} />
          </div>
        )}

        {/* VIEW 6: CLASSIC SIDE-BY-SIDE SPLIT VIEW */}
        {activeView === 'split' && (
          <>
            {/* Panel 1: File Explorer */}
            <div style={{ width: `${leftWidth}%` }} className="h-full min-w-[160px]">
              <FileExplorer />
            </div>

            {/* Resizer 1 */}
            <div 
              className="w-1 bg-accent hover:bg-highlight cursor-col-resize z-10 transition-colors"
              onMouseDown={handleLeftDragStart}
            />

            {/* Panel 2: Editor */}
            <div style={{ width: `${middleWidth}%` }} className="h-full flex flex-col min-w-[200px]">
              <EditorTabs />
              <div className="flex-1 overflow-hidden">
                <MonacoDslEditor />
              </div>
            </div>

            {/* Resizer 2 */}
            <div 
              className="w-1 bg-accent hover:bg-highlight cursor-col-resize z-10 transition-colors"
              onMouseDown={handleMiddleDragStart}
            />

            {/* Panel 3: Flow + Output */}
            <div ref={rightContainerRef} style={{ width: `${100 - leftWidth - middleWidth}%` }} className="h-full flex flex-col min-w-[200px]">
              {/* Top Right - Flow Editor */}
              <div style={{ height: `${topHeight}%` }} className="w-full relative min-h-[100px]">
                <ActivityFlowEditor />
              </div>

              {/* Resizer V */}
              <div 
                className="h-1 bg-accent hover:bg-highlight cursor-row-resize z-10 transition-colors"
                onMouseDown={handleVDragStart}
              />

              {/* Bottom Right - Output */}
              <div style={{ height: `${100 - topHeight}%` }} className="w-full bg-surface min-h-[100px]">
                <TransformOutput />
              </div>
            </div>
          </>
        )}

        {/* Floating Gemini Assistant Panel */}
        {showAssistant && (
          <GeminiAssistantPanel onClose={() => setShowAssistant(false)} />
        )}
      </div>
    </div>
  );
};

export default ProjectWorkspacePage;
