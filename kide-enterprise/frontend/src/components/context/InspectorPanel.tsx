import React, { useState, useEffect } from 'react';
import { useEditorStore, useActiveFile } from '../../stores/editorStore';
import { CodeInspector } from './CodeInspector';
import { GraphInspector } from './GraphInspector';
import { WorkflowInspector } from './WorkflowInspector';
import { StateMachineInspector } from './StateMachineInspector';
import { SimulatorInspector } from './SimulatorInspector';
import { GenerateInspector } from './GenerateInspector';
import { PanelLeftClose, PanelLeftOpen, CheckCircle2, AlertTriangle, AlertCircle } from 'lucide-react';

interface InspectorPanelProps {
  className?: string;
}

export const InspectorPanel: React.FC<InspectorPanelProps> = ({ className = '' }) => {
  const activeFile = useActiveFile();
  const { 
    activeView, 
    selectedNodeId, 
    validationErrors, 
    dirtyFileIds, 
    graphScope, 
    selectedGraphNode 
  } = useEditorStore();

  // Collapsed state persisted in localStorage
  const [isCollapsed, setIsCollapsed] = useState<boolean>(() => {
    try {
      const saved = localStorage.getItem('kide_inspector_collapsed') ?? localStorage.getItem('kide_context_panel_collapsed');
      if (saved !== null) return saved === 'true';
    } catch {}
    return false;
  });

  // Width resizing state persisted in localStorage
  const [panelWidth, setPanelWidth] = useState<number>(() => {
    try {
      const saved = localStorage.getItem('kide_inspector_width') ?? localStorage.getItem('kide_context_panel_width');
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
      localStorage.setItem('kide_inspector_collapsed', String(isCollapsed));
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
      const container = document.getElementById('inspector-panel-container');
      if (container) {
        const rect = container.getBoundingClientRect();
        const newWidth = Math.max(220, Math.min(360, moveEvent.clientX - rect.left));
        setPanelWidth(newWidth);
        try {
          localStorage.setItem('kide_inspector_width', String(newWidth));
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

  // Helper to compute semantic type
  const getSemanticType = (): string => {
    if (activeView === 'knowledgegraph') {
      if (selectedGraphNode) {
        const t = selectedGraphNode.type || 'Entity';
        return t.charAt(0).toUpperCase() + t.slice(1);
      }
      return graphScope === 'global' ? 'Global Domain Catalog' : 'Project Knowledge Graph';
    }

    if (activeView === 'statemachine') {
      return selectedNodeId ? 'Operating State' : 'Supervisory Automata';
    }

    if (activeView === 'workflow') {
      return selectedNodeId ? 'Workflow Activity' : 'Activity Diagram';
    }

    if (activeView === 'simulator') {
      return 'Live Controller Simulator';
    }

    if (activeView === 'codegen') {
      return 'Code Synthesis Studio';
    }

    // Code / Default
    if (selectedNodeId) return 'Declared Symbol';
    if (!activeFile) return 'Engineering Workspace';

    const lower = activeFile.name.toLowerCase();
    if (lower.endsWith('.activity')) return 'Activity Diagram';
    if (lower.endsWith('.dml')) return 'Data Model Package';
    if (lower.endsWith('.cap')) return 'Semantic Capability';
    if (lower.endsWith('.op')) return 'Device Operation';
    if (lower.endsWith('.mnc') || lower.endsWith('.mncml')) return 'MNC-ML Interface';
    if (lower.endsWith('.json')) return 'Activity Diagram (JSON)';
    if (lower.endsWith('.py')) return 'Python Controller';
    return 'Source Artifact';
  };

  // Helper to compute semantic name
  const getSemanticName = (): string => {
    if (activeView === 'knowledgegraph') {
      return selectedGraphNode ? selectedGraphNode.name : (graphScope === 'global' ? 'Global Repository' : 'Project Knowledge Graph');
    }
    if (activeView === 'statemachine') {
      return selectedNodeId || 'State Machine';
    }
    if (activeView === 'workflow') {
      return selectedNodeId || activeFile?.name || 'Workflow Diagram';
    }
    if (activeView === 'simulator') {
      return 'Controller Simulator';
    }
    if (activeView === 'codegen') {
      return 'Industrial Code Generator';
    }
    return selectedNodeId || activeFile?.name || 'Project File';
  };

  // Helper to compute breadcrumb
  const getBreadcrumb = (): string | null => {
    if (activeView === 'knowledgegraph') {
      return selectedGraphNode ? `${graphScope === 'global' ? 'Global Repository' : 'Project Graph'} › ${selectedGraphNode.name}` : null;
    }
    if (activeView === 'statemachine' && selectedNodeId) {
      return `State Machine › ${selectedNodeId}`;
    }
    if (activeView === 'workflow' && selectedNodeId) {
      return `${activeFile?.name || 'Workflow'} › ${selectedNodeId}`;
    }
    if (selectedNodeId && activeFile) {
      return `${activeFile.name} › ${selectedNodeId}`;
    }
    return null;
  };

  // Helper to compute status badge
  const renderStatusBadge = () => {
    if (activeView === 'knowledgegraph') {
      return (
        <span className="text-[10px] font-medium text-emerald-400 flex items-center gap-1">
          <CheckCircle2 size={11} />
          <span>Live Graph</span>
        </span>
      );
    }

    if (activeView === 'simulator') {
      return (
        <span className="text-[10px] font-medium text-emerald-400 flex items-center gap-1">
          <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse" />
          <span>Live</span>
        </span>
      );
    }

    if (activeFile) {
      const issues = validationErrors[activeFile.id] || [];
      const errorCount = issues.filter(e => e.severity === 'error').length;
      const warnCount = issues.filter(e => e.severity !== 'error').length;
      const isDirty = dirtyFileIds.includes(activeFile.id);

      if (errorCount > 0) {
        return (
          <span className="text-[10px] font-medium text-rose-400 flex items-center gap-1 bg-rose-950/50 px-1.5 py-0.5 rounded border border-rose-800/60">
            <AlertCircle size={11} />
            <span>{errorCount} {errorCount === 1 ? 'Error' : 'Errors'}</span>
          </span>
        );
      }
      if (warnCount > 0) {
        return (
          <span className="text-[10px] font-medium text-amber-400 flex items-center gap-1 bg-amber-950/50 px-1.5 py-0.5 rounded border border-amber-800/60">
            <AlertTriangle size={11} />
            <span>{warnCount} {warnCount === 1 ? 'Warning' : 'Warnings'}</span>
          </span>
        );
      }
      if (isDirty) {
        return (
          <span className="text-[10px] font-medium text-amber-300 flex items-center gap-1">
            <span className="w-1.5 h-1.5 rounded-full bg-amber-400" />
            <span>Unsaved</span>
          </span>
        );
      }
      return (
        <span className="text-[10px] font-medium text-emerald-400 flex items-center gap-1">
          <CheckCircle2 size={11} />
          <span>Valid</span>
        </span>
      );
    }

    return (
      <span className="text-[10px] font-medium text-emerald-400 flex items-center gap-1">
        <CheckCircle2 size={11} />
        <span>Ready</span>
      </span>
    );
  };

  // Collapsed View: 28px thin bar with 1-click restore
  if (isCollapsed) {
    return (
      <div 
        className="w-7 h-full bg-[#0d1117] border-r border-gray-800 flex flex-col items-center py-3 select-none z-10 shrink-0"
        title="Expand Inspector Panel"
      >
        <button
          onClick={toggleCollapse}
          className="p-1 rounded text-gray-400 hover:text-white hover:bg-gray-800 transition mb-4"
          title="Expand Inspector"
          aria-label="Expand Inspector"
        >
          <PanelLeftOpen size={14} />
        </button>
        <div 
          onClick={toggleCollapse}
          className="cursor-pointer text-[10px] uppercase font-bold tracking-wider text-gray-500 hover:text-gray-300 transition [writing-mode:vertical-lr] rotate-180 flex items-center gap-1.5"
        >
          <span>INSPECTOR</span>
        </div>
      </div>
    );
  }

  // Dispatch to workspace inspector
  const renderInspectorBody = () => {
    switch (activeView) {
      case 'knowledgegraph':
        return <GraphInspector />;
      case 'statemachine':
        return <StateMachineInspector />;
      case 'workflow':
        return <WorkflowInspector />;
      case 'simulator':
        return <SimulatorInspector />;
      case 'codegen':
        return <GenerateInspector />;
      case 'editor':
      case 'split':
      default:
        return <CodeInspector />;
    }
  };

  const breadcrumb = getBreadcrumb();
  const semanticName = getSemanticName();
  const semanticType = getSemanticType();

  return (
    <div
      id="inspector-panel-container"
      style={{ width: `${panelWidth}px` }}
      className={`relative h-full min-h-0 bg-[#0d1117] border-r border-gray-800 flex flex-col shrink-0 select-none z-10 ${
        isResizing ? 'transition-none select-none' : 'transition-[width] duration-150 ease-in-out'
      } ${className}`}
    >
      {/* 1. Master Inspector Semantic Header */}
      <div className="border-b border-gray-800/80 bg-[#141a24] px-3 py-2 shrink-0">
        <div className="flex items-center justify-between mb-1">
          <span className="text-[10px] font-black tracking-widest uppercase text-blue-400">
            INSPECTOR
          </span>
          <div className="flex items-center gap-2">
            {renderStatusBadge()}
            <button
              onClick={toggleCollapse}
              className="p-1 rounded text-gray-500 hover:text-gray-300 hover:bg-gray-800 transition"
              title="Collapse Inspector"
              aria-label="Collapse Inspector"
            >
              <PanelLeftClose size={13} />
            </button>
          </div>
        </div>

        {breadcrumb && (
          <div className="text-[10px] text-gray-500 truncate mb-0.5 font-mono">
            {breadcrumb}
          </div>
        )}

        <div className="flex items-baseline justify-between gap-1 min-w-0">
          <div className="text-xs font-bold text-gray-100 truncate" title={semanticName}>
            {semanticName}
          </div>
          <div className="text-[10px] text-gray-400 shrink-0 font-medium">
            {semanticType}
          </div>
        </div>
      </div>

      {/* 2. Workspace-Adaptive Dynamic Inspector Body */}
      <div className="flex-1 min-h-0 flex flex-col overflow-hidden">
        {renderInspectorBody()}
      </div>

      {/* 3. Drag Resize Handle */}
      <div
        onMouseDown={handleResizeStart}
        className="absolute top-0 right-0 w-1.5 h-full cursor-col-resize hover:bg-blue-500/50 transition-colors z-20"
        title="Drag to resize Inspector panel"
      />
    </div>
  );
};

// Backwards compatibility export
export const ArtifactContextPanel = InspectorPanel;
