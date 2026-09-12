import React, { useState, useEffect } from 'react';
import { useEditorStore, useActiveFile } from '../../stores/editorStore';
import { CodeContext } from './CodeContext';
import { StateMachineContext } from './StateMachineContext';
import { WorkflowContext } from './WorkflowContext';
import { SimulatorContext } from './SimulatorContext';
import { GenerateContext } from './GenerateContext';
import { PanelLeftClose, PanelLeftOpen, ChevronRight } from 'lucide-react';

interface ArtifactContextPanelProps {
  className?: string;
}

export const ArtifactContextPanel: React.FC<ArtifactContextPanelProps> = ({ className = '' }) => {
  const activeFile = useActiveFile();
  const { activeView, selectedNodeId } = useEditorStore();

  // Collapsed state persisted in localStorage
  const [isCollapsed, setIsCollapsed] = useState<boolean>(() => {
    try {
      const saved = localStorage.getItem('kide_context_panel_collapsed');
      if (saved !== null) return saved === 'true';
    } catch {}
    return false;
  });

  // Width resizing state persisted in localStorage
  const [panelWidth, setPanelWidth] = useState<number>(() => {
    try {
      const saved = localStorage.getItem('kide_context_panel_width');
      if (saved) {
        const val = parseInt(saved, 10);
        if (!isNaN(val) && val >= 220 && val <= 360) return val;
      }
    } catch {}
    return 280;
  });

  const [isResizing, setIsResizing] = useState(false);

  useEffect(() => {
    try {
      localStorage.setItem('kide_context_panel_collapsed', String(isCollapsed));
    } catch {}
  }, [isCollapsed]);

  const toggleCollapse = () => {
    setIsCollapsed(prev => !prev);
  };

  // Drag to resize right border
  const handleResizeStart = (e: React.MouseEvent) => {
    e.preventDefault();
    setIsResizing(true);

    const handleMouseMove = (moveEvent: MouseEvent) => {
      // Calculate new width relative to panel's left edge
      const container = document.getElementById('artifact-context-container');
      if (container) {
        const rect = container.getBoundingClientRect();
        const newWidth = Math.max(220, Math.min(360, moveEvent.clientX - rect.left));
        setPanelWidth(newWidth);
        try {
          localStorage.setItem('kide_context_panel_width', String(newWidth));
        } catch {}
      }
    };

    const handleMouseUp = () => {
      setIsResizing(false);
      document.removeEventListener('mousemove', handleMouseMove);
      document.removeEventListener('mouseup', handleMouseUp);
    };

    document.addEventListener('mousemove', handleMouseMove);
    document.addEventListener('mouseup', handleMouseUp);
  };

  // Collapsed View: A thin 28px vertical bar allowing 1-click expand
  if (isCollapsed) {
    return (
      <div 
        className="w-7 h-full bg-[#0d1117] border-r border-gray-800 flex flex-col items-center py-3 select-none z-10 shrink-0"
        title="Expand Artifact Context Panel"
      >
        <button
          onClick={toggleCollapse}
          className="p-1 rounded text-gray-400 hover:text-white hover:bg-gray-800 transition mb-4"
          title="Expand Context Panel"
          aria-label="Expand Context Panel"
        >
          <PanelLeftOpen size={14} />
        </button>
        <div 
          onClick={toggleCollapse}
          className="cursor-pointer text-[10px] uppercase font-bold tracking-wider text-gray-500 hover:text-gray-300 transition [writing-mode:vertical-lr] rotate-180 flex items-center gap-1.5"
        >
          <span>ARTIFACT CONTEXT</span>
        </div>
      </div>
    );
  }

  // Determine which workspace-specific context to render
  const renderContextBody = () => {
    switch (activeView) {
      case 'statemachine':
        return <StateMachineContext />;
      case 'workflow':
        return <WorkflowContext />;
      case 'simulator':
        return <SimulatorContext />;
      case 'codegen':
        return <GenerateContext />;
      case 'editor':
      case 'split':
      default:
        return <CodeContext />;
    }
  };

  return (
    <div
      id="artifact-context-container"
      style={{ width: `${panelWidth}px` }}
      className={`relative h-full min-h-0 bg-[#0d1117] border-r border-gray-800 flex flex-col shrink-0 select-none z-10 ${
        isResizing ? 'transition-none select-none' : 'transition-[width] duration-150 ease-in-out'
      } ${className}`}
    >
      {/* 1. Context Panel Header */}
      <div className="h-9 px-3 border-b border-gray-800/80 bg-[#161b22] flex items-center justify-between">
        <div className="flex items-center gap-1.5 overflow-hidden flex-1 min-w-0">
          <span className="text-[11px] font-bold text-gray-200 uppercase tracking-wider shrink-0">
            CONTEXT
          </span>
          {activeFile && (
            <div className="flex items-center gap-1 overflow-hidden text-[11px] text-gray-400 min-w-0">
              <span className="text-gray-600">/</span>
              <span className="truncate text-gray-300 font-medium" title={activeFile.name}>
                {activeFile.name}
              </span>
              {selectedNodeId && (
                <>
                  <ChevronRight size={11} className="text-gray-600 shrink-0" />
                  <span className="truncate text-blue-300 font-medium" title={selectedNodeId}>
                    {selectedNodeId}
                  </span>
                </>
              )}
            </div>
          )}
        </div>

        {/* Collapse Button */}
        <button
          onClick={toggleCollapse}
          className="p-1 rounded text-gray-400 hover:text-white hover:bg-gray-800 transition shrink-0 ml-1"
          title="Collapse Context Panel"
          aria-label="Collapse Context Panel"
        >
          <PanelLeftClose size={13} />
        </button>
      </div>

      {/* 2. Context Panel Content */}
      <div className="flex-1 min-h-0 flex flex-col overflow-hidden bg-[#0d1117]">
        {renderContextBody()}
      </div>

      {/* 3. Drag to Resize Right Divider */}
      <div
        onMouseDown={handleResizeStart}
        className="absolute right-0 top-0 bottom-0 w-1 cursor-col-resize hover:bg-blue-500/80 active:bg-blue-500 transition-colors z-20"
        title="Drag to resize Context Panel width"
      />
    </div>
  );
};
