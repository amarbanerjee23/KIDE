import React, { useRef } from 'react';
import { useEditorStore, WorkspaceView, SplitLayoutOption } from '../../stores/editorStore';
import { 
  FileCode2, GitMerge, Activity, Play, Sparkles, Network,
  Columns, ChevronDown 
} from 'lucide-react';

export const WorkspaceNavigation: React.FC = () => {
  const { 
    activeView, setActiveView, 
    splitMode, setSplitMode,
    splitLayout, setSplitLayout 
  } = useEditorStore();

  const [isSplitMenuOpen, setIsSplitMenuOpen] = React.useState(false);
  const tabListRef = useRef<HTMLDivElement>(null);

  const tabs: { id: WorkspaceView; label: string; icon: React.FC<{ className?: string }> }[] = [
    { id: 'editor', label: 'Code', icon: FileCode2 },
    { id: 'statemachine', label: 'State Machine', icon: GitMerge },
    { id: 'workflow', label: 'Workflow', icon: Activity },
    { id: 'knowledgegraph', label: 'Knowledge Graph', icon: Network },
    { id: 'simulator', label: 'Simulator', icon: Play },
    { id: 'codegen', label: 'Generate', icon: Sparkles },
  ];

  // Handle keyboard navigation between tabs (ArrowLeft, ArrowRight)
  const handleKeyDown = (e: React.KeyboardEvent, currentIndex: number) => {
    if (e.key === 'ArrowRight') {
      e.preventDefault();
      const nextIndex = (currentIndex + 1) % tabs.length;
      setActiveView(tabs[nextIndex].id);
    } else if (e.key === 'ArrowLeft') {
      e.preventDefault();
      const prevIndex = (currentIndex - 1 + tabs.length) % tabs.length;
      setActiveView(tabs[prevIndex].id);
    }
  };

  const splitOptions: { id: SplitLayoutOption; label: string }[] = [
    { id: 'code-workflow', label: 'Code | Workflow' },
    { id: 'code-statemachine', label: 'Code | State Machine' },
    { id: 'code-knowledgegraph', label: 'Code | Knowledge Graph' },
    { id: 'workflow-knowledgegraph', label: 'Workflow | Knowledge Graph' },
    { id: 'workflow-simulator', label: 'Workflow | Simulator' },
  ];

  return (
    <nav 
      aria-label="Workspace Navigation Tabs"
      className="h-10 min-h-[40px] bg-[#161b22] border-b border-gray-800 px-4 flex items-center justify-between select-none shrink-0"
    >
      {/* LEFT: Application Tabs */}
      <div 
        ref={tabListRef}
        role="tablist" 
        aria-label="Workspace Tools"
        className="flex items-center gap-1 h-full"
      >
        {tabs.map((tab, idx) => {
          const Icon = tab.icon;
          // In split mode, the base view is still indicated
          const isSelected = activeView === tab.id;

          return (
            <button
              key={tab.id}
              role="tab"
              id={`workspace-tab-${tab.id}`}
              aria-selected={isSelected}
              aria-controls={`workspace-panel-${tab.id}`}
              tabIndex={isSelected ? 0 : -1}
              onClick={() => setActiveView(tab.id)}
              onKeyDown={(e) => handleKeyDown(e, idx)}
              className={`h-full flex items-center gap-2 px-3 text-xs font-medium transition-all relative border-b-2 ${
                isSelected
                  ? 'text-blue-300 border-blue-500 bg-blue-600/10 font-semibold'
                  : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/40 border-transparent'
              }`}
            >
              <Icon className={`w-3.5 h-3.5 ${isSelected ? 'text-blue-400' : 'text-gray-500'}`} />
              <span>{tab.label}</span>
            </button>
          );
        })}
      </div>

      {/* RIGHT: Split View Layout Toggle & Options */}
      <div className="flex items-center gap-1">
        <div className="relative">
          <div className="flex items-center bg-[#0d1117] rounded-lg border border-gray-800 p-0.5">
            <button
              onClick={() => setSplitMode(prev => !prev)}
              aria-pressed={splitMode}
              className={`flex items-center gap-1.5 px-2.5 py-1 rounded-md text-xs font-medium transition ${
                splitMode 
                  ? 'bg-blue-600/20 text-blue-300 font-semibold' 
                  : 'text-gray-400 hover:text-gray-200'
              }`}
              title="Toggle Split Workspace Layout"
            >
              <Columns className="w-3.5 h-3.5" />
              <span className="hidden sm:inline">Split View</span>
            </button>

            {splitMode && (
              <button
                onClick={() => setIsSplitMenuOpen(prev => !prev)}
                className="p-1 text-gray-400 hover:text-white rounded transition"
                title="Configure Split View Panes"
              >
                <ChevronDown className="w-3 h-3" />
              </button>
            )}
          </div>

          {splitMode && isSplitMenuOpen && (
            <div className="absolute right-0 mt-1 w-44 bg-[#1c2128] border border-gray-700/80 rounded-lg shadow-xl py-1 z-50 animate-in fade-in-50 zoom-in-95">
              <div className="px-3 py-1 text-[10px] font-semibold text-gray-400 uppercase tracking-wider">
                Split Combination
              </div>
              {splitOptions.map(opt => (
                <button
                  key={opt.id}
                  onClick={() => {
                    setSplitLayout(opt.id);
                    setIsSplitMenuOpen(false);
                  }}
                  className={`w-full text-left px-3 py-1.5 text-xs flex items-center justify-between transition ${
                    splitLayout === opt.id 
                      ? 'text-blue-300 bg-blue-600/15 font-semibold' 
                      : 'text-gray-300 hover:bg-gray-800'
                  }`}
                >
                  <span>{opt.label}</span>
                  {splitLayout === opt.id && <span className="w-1.5 h-1.5 rounded-full bg-blue-400"></span>}
                </button>
              ))}
            </div>
          )}
        </div>
      </div>
    </nav>
  );
};

