import React, { useMemo, useState } from 'react';
import { useEditorStore, useActiveFile } from '../../stores/editorStore';
import { extractOutlineSymbols } from '../../utils/outlineExtractor';
import { navigateToDefinition } from '../../lib/monaco/xtextLanguageService';
import { OutlineItem } from '../../types/navigator';
import { 
  Database, Cpu, Settings, Activity, Layers, 
  ChevronRight, ChevronDown, ListTree, Code2, Tag 
} from 'lucide-react';

export const ProjectOutline: React.FC = () => {
  const activeFile = useActiveFile();
  const { setActiveView } = useEditorStore();
  const [collapsedNodes, setCollapsedNodes] = useState<Record<string, boolean>>({});

  const symbols = useMemo<OutlineItem[]>(() => {
    if (!activeFile) return [];
    return extractOutlineSymbols(activeFile.content, activeFile.name);
  }, [activeFile?.content, activeFile?.name]);

  const toggleCollapse = (id: string) => {
    setCollapsedNodes(prev => ({ ...prev, [id]: !prev[id] }));
  };

  const handleSelectSymbol = (item: OutlineItem) => {
    if (!activeFile) return;
    setActiveView('editor');
    if (item.line) {
      navigateToDefinition(activeFile.id, item.line, item.column || 1);
    }
  };

  const getSymbolIcon = (kind: string) => {
    switch (kind) {
      case 'Package': return <Layers size={13} className="text-indigo-400 shrink-0" />;
      case 'DataModel': return <Database size={13} className="text-rose-400 shrink-0" />;
      case 'Field': return <Tag size={12} className="text-gray-400 shrink-0" />;
      case 'Capability': return <Cpu size={13} className="text-purple-400 shrink-0" />;
      case 'Operation': return <Settings size={13} className="text-amber-400 shrink-0" />;
      case 'Activity': return <Activity size={13} className="text-emerald-400 shrink-0" />;
      case 'State': return <Layers size={13} className="text-blue-400 shrink-0" />;
      case 'Command': return <Code2 size={13} className="text-cyan-400 shrink-0" />;
      case 'Event': return <Tag size={12} className="text-amber-400 shrink-0" />;
      default: return <Code2 size={13} className="text-gray-400 shrink-0" />;
    }
  };

  if (!activeFile) {
    return (
      <div className="flex flex-col items-center justify-center p-6 text-center text-gray-500 h-full select-none">
        <ListTree className="w-8 h-8 text-gray-600 mb-2" />
        <p className="text-xs font-semibold text-gray-300">No Active File</p>
        <p className="text-[11px] text-gray-500 mt-1">Select a file from the explorer to view its outline.</p>
      </div>
    );
  }

  if (symbols.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center p-6 text-center text-gray-500 h-full select-none">
        <ListTree className="w-8 h-8 text-gray-600 mb-2" />
        <p className="text-xs font-semibold text-gray-300">No Outline Available</p>
        <p className="text-[11px] text-gray-500 mt-1">No structural symbols declared in {activeFile.name}.</p>
      </div>
    );
  }

  const renderOutlineNode = (node: OutlineItem, depth: number = 0) => {
    const hasChildren = node.children && node.children.length > 0;
    const isCollapsed = Boolean(collapsedNodes[node.id]);

    return (
      <div key={node.id} className="select-none">
        <div
          onClick={() => handleSelectSymbol(node)}
          style={{ paddingLeft: `${depth * 14 + 10}px` }}
          className="group flex items-center justify-between py-1 pr-2 hover:bg-gray-800/60 rounded cursor-pointer text-xs text-gray-300 hover:text-white transition-colors"
        >
          <div className="flex items-center gap-1.5 overflow-hidden flex-1 min-w-0">
            {hasChildren ? (
              <button
                type="button"
                onClick={(e) => {
                  e.stopPropagation();
                  toggleCollapse(node.id);
                }}
                className="p-0.5 hover:text-white text-gray-500 rounded"
              >
                {isCollapsed ? <ChevronRight size={12} /> : <ChevronDown size={12} />}
              </button>
            ) : (
              <span className="w-4" />
            )}
            {getSymbolIcon(node.kind)}
            <span className="truncate font-medium text-gray-200 group-hover:text-blue-300">
              {node.name}
            </span>
            {node.detail && (
              <span className="text-[10px] text-gray-500 truncate ml-1">
                {node.detail}
              </span>
            )}
          </div>

          <span className="text-[10px] text-gray-500 font-mono shrink-0 ml-1 opacity-0 group-hover:opacity-100 transition-opacity">
            L{node.line}
          </span>
        </div>

        {hasChildren && !isCollapsed && (
          <div>
            {node.children!.map((child) => renderOutlineNode(child, depth + 1))}
          </div>
        )}
      </div>
    );
  };

  return (
    <div className="flex-1 overflow-y-auto py-1">
      <div className="px-3 py-1 text-[11px] text-gray-400 font-medium border-b border-gray-800/80 mb-1 flex items-center justify-between">
        <span className="truncate">{activeFile.name}</span>
        <span className="text-[10px] text-gray-500 uppercase">{activeFile.language}</span>
      </div>
      <div className="space-y-0.5">
        {symbols.map((sym) => renderOutlineNode(sym, 0))}
      </div>
    </div>
  );
};

