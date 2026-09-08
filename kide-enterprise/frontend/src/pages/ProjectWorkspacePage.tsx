import React, { useState, useRef } from 'react';
import { useParams } from 'react-router-dom';
import MonacoDslEditor from '../components/editor/MonacoDslEditor';
import EditorToolbar from '../components/editor/EditorToolbar';
import FileExplorer from '../components/editor/FileExplorer';
import EditorTabs from '../components/editor/EditorTabs';
import GeminiAssistantPanel from '../components/editor/GeminiAssistantPanel';
import ActivityFlowEditor from '../components/flow/ActivityFlowEditor';
import TransformOutput from '../components/output/TransformOutput';
import { useEditorStore } from '../stores/editorStore';
import { Bot } from 'lucide-react';

const ProjectWorkspacePage = () => {
  const { id } = useParams();
  const [leftWidth, setLeftWidth] = useState(20); // 20% for File Explorer
  const [middleWidth, setMiddleWidth] = useState(40); // 40% for Editor
  const [topHeight, setTopHeight] = useState(60); // percentage for Flow
  const [showAssistant, setShowAssistant] = useState(false);
  
  const containerRef = useRef<HTMLDivElement>(null);
  const rightContainerRef = useRef<HTMLDivElement>(null);

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

  // Handle Dragging Editor Resizer
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

  return (
    <div className="flex flex-col h-full bg-background overflow-hidden relative">
      <EditorToolbar />
      
      <div ref={containerRef} className="flex flex-1 overflow-hidden relative">
        
        {/* Panel 1: File Explorer */}
        <div style={{ width: `${leftWidth}%` }} className="h-full min-w-[150px]">
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
            
            {/* Toggle Assistant Button */}
            {!showAssistant && (
              <button 
                onClick={() => setShowAssistant(true)}
                className="absolute top-4 right-4 bg-[#161b22] border border-gray-700 hover:border-blue-500 text-blue-400 p-2 rounded-full shadow-lg transition-all z-50 flex items-center justify-center group"
                title="Open Gemini Assistant"
              >
                <Bot className="w-5 h-5" />
                <span className="w-0 overflow-hidden group-hover:w-24 group-hover:ml-2 whitespace-nowrap transition-all duration-300 text-xs font-semibold">
                  Ask Gemini
                </span>
              </button>
            )}
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

        {/* Panel 4: Gemini Assistant (Overlay or Flex) */}
        {showAssistant && (
          <GeminiAssistantPanel onClose={() => setShowAssistant(false)} />
        )}
      </div>
    </div>
  );
};

export default ProjectWorkspacePage;
