import React, { useMemo } from 'react';
import { useEditorStore, useActiveFile } from '../../stores/editorStore';
import { extractOutlineSymbols } from '../../utils/outlineExtractor';
import { navigateToDefinition } from '../../lib/monaco/xtextLanguageService';
import { CollapsibleSection } from './CollapsibleSection';
import { OutlineItem } from '../../types/navigator';
import { 
  Database, Cpu, Settings, Activity, Layers, 
  Tag, Code2, AlertTriangle, AlertCircle, 
  ArrowRight, Sparkles, ExternalLink, ArrowUpRight, Clock, GitMerge
} from 'lucide-react';

export const CodeInspector: React.FC = () => {
  const activeFile = useActiveFile();
  const { 
    files, 
    selectedNodeId, 
    setSelectedNodeId, 
    validationErrors, 
    dirtyFileIds, 
    setActiveFileId, 
    setActiveView 
  } = useEditorStore();

  const isDirty = activeFile ? dirtyFileIds.includes(activeFile.id) : false;
  const fileIssues = activeFile ? (validationErrors[activeFile.id] || []) : [];
  const errorCount = fileIssues.filter(e => e.severity === 'error').length;

  // Extract symbols from file content
  const symbols = useMemo<OutlineItem[]>(() => {
    if (!activeFile) return [];
    return extractOutlineSymbols(activeFile.content, activeFile.name);
  }, [activeFile?.content, activeFile?.name]);

  // Find if a specific symbol is selected
  const selectedSymbol = useMemo(() => {
    if (!selectedNodeId || symbols.length === 0) return null;
    
    const findInList = (list: OutlineItem[]): OutlineItem | null => {
      for (const item of list) {
        if (item.name === selectedNodeId || item.id === selectedNodeId) return item;
        if (item.children) {
          const found = findInList(item.children);
          if (found) return found;
        }
      }
      return null;
    };
    return findInList(symbols);
  }, [selectedNodeId, symbols]);

  // Extract relations (dependencies, outputs, sister files)
  const relations = useMemo(() => {
    if (!activeFile) return null;

    const lowerName = activeFile.name.toLowerCase();
    const content = activeFile.content;

    const requires: Array<{ name: string; type: 'capability' | 'operation'; targetFileId?: string }> = [];
    const produces: Array<{ name: string; type: string }> = [];
    const relatedFiles: Array<{ name: string; fileId: string }> = [];

    if (lowerName.endsWith('.activity') || lowerName.endsWith('.json')) {
      const capMatches = content.matchAll(/requireCapability\s*:\s*"?([A-Za-z0-9_]+)"?/g);
      for (const m of capMatches) {
        const capName = m[1];
        const matchFile = files.find(f => 
          f.id !== activeFile.id && (
            f.name.toLowerCase().includes(capName.toLowerCase()) || 
            (f.content && f.content.includes(capName))
          )
        );
        requires.push({ name: capName, type: 'capability', targetFileId: matchFile?.id });
      }

      const opMatches = content.matchAll(/requireOperation\s*\(\s*([A-Za-z0-9_]+)\s*\)/g);
      for (const m of opMatches) {
        const opName = m[1];
        const matchFile = files.find(f => 
          f.id !== activeFile.id && (
            f.name.toLowerCase().includes(opName.toLowerCase()) || 
            (f.content && f.content.includes(opName))
          )
        );
        requires.push({ name: opName, type: 'operation', targetFileId: matchFile?.id });
      }

      produces.push({
        name: `${activeFile.name.replace(/\.[^/.]+$/, '')} State Machine Automata`,
        type: 'Supervisory Automata'
      });
    }

    const basePrefix = activeFile.name.split('.')[0].replace(/(_Cap|_Ops|_Data|System|Control|Cell)$/i, '');
    if (basePrefix.length > 2) {
      for (const f of files) {
        if (f.id !== activeFile.id && f.name.toLowerCase().includes(basePrefix.toLowerCase())) {
          relatedFiles.push({ name: f.name, fileId: f.id });
        }
      }
    }

    return { requires, produces, relatedFiles };
  }, [activeFile, files]);

  // Symbol jump navigation
  const handleJumpToSymbol = (item: OutlineItem) => {
    if (!activeFile) return;
    setSelectedNodeId(item.name);
    if (item.line) {
      navigateToDefinition(activeFile.id, item.line, item.column || 1);
    }
  };

  // Open related file
  const handleOpenRelatedFile = (fileId: string) => {
    setActiveFileId(fileId);
    setActiveView('editor');
  };

  if (!activeFile) {
    return (
      <div className="p-4 text-center text-gray-500 text-xs mt-6">
        No active file open in the code workspace.
      </div>
    );
  }

  // Helper for symbol icon
  const getSymbolIcon = (kind: string) => {
    switch (kind) {
      case 'DataModel': return <Database size={13} className="text-rose-400 shrink-0" />;
      case 'Capability': return <Cpu size={13} className="text-purple-400 shrink-0" />;
      case 'Operation': return <Settings size={13} className="text-amber-400 shrink-0" />;
      case 'Activity': return <Activity size={13} className="text-emerald-400 shrink-0" />;
      case 'State': return <Layers size={13} className="text-blue-400 shrink-0" />;
      case 'Command': return <Code2 size={13} className="text-cyan-400 shrink-0" />;
      default: return <Tag size={12} className="text-gray-400 shrink-0" />;
    }
  };

  // -----------------------------------------------------------------
  // STATE C: SPECIFIC SYMBOL SELECTED (e.g. Activity "Monitor")
  // -----------------------------------------------------------------
  if (selectedSymbol) {
    // Extract symbol-specific context from file content if activity
    const isActivity = selectedSymbol.kind === 'Activity';
    let activityRequires: string[] = [];
    let nextActivities: string[] = [];
    let timing: string | null = null;
    let conditions: string[] = [];

    if (isActivity) {
      // Find activity block in content
      const actRegex = new RegExp(`Activity\\s+${selectedSymbol.name}\\s*\\{([\\s\\S]*?)\\}(?=\\s*(?:Activity|\\}))`, 'i');
      const actMatch = activeFile.content.match(actRegex);
      if (actMatch) {
        const actBlock = actMatch[1];
        const capMatch = actBlock.match(/requireCapability\s*:\s*"?([A-Za-z0-9_]+)"?/);
        if (capMatch) activityRequires.push(capMatch[1]);
        const opMatch = actBlock.match(/requireOperation\s*\(\s*([A-Za-z0-9_]+)\s*\)/);
        if (opMatch) activityRequires.push(opMatch[1]);
        const nextMatch = actBlock.match(/nextActivity\s*:\s*([A-Za-z0-9_]+)/);
        if (nextMatch) nextActivities.push(nextMatch[1]);
        const timeMatch = actBlock.match(/time\s*:\s*([0-9.]+)\s*([a-zA-Z]+)/);
        if (timeMatch) timing = `${timeMatch[1]} ${timeMatch[2]}`;

        // Conditions
        const condMatches = actBlock.matchAll(/([a-zA-Z0-9_]+)\s*(>|<|=)\s*([0-9.]+)/g);
        for (const c of condMatches) {
          conditions.push(`${c[1]} ${c[2]} ${c[3]}`);
        }
      }
    }

    return (
      <div className="flex-1 flex flex-col min-h-0 overflow-y-auto select-none">
        {/* Symbol Quick Nav / Return to Overview */}
        <div className="p-3 border-b border-gray-800 bg-[#111622]/60 flex items-center justify-between">
          <div className="flex items-center gap-1.5 text-xs text-gray-300 font-semibold truncate">
            {getSymbolIcon(selectedSymbol.kind)}
            <span>{selectedSymbol.name}</span>
          </div>
          <button
            onClick={() => setSelectedNodeId(null)}
            className="text-[11px] text-blue-400 hover:text-blue-300 hover:underline shrink-0"
          >
            File Overview
          </button>
        </div>

        {/* Requires */}
        <CollapsibleSection
          title="Requires"
          count={activityRequires.length}
          icon={<ArrowRight size={13} className="text-emerald-400" />}
          hideIfZero={true}
        >
          <div className="space-y-1 mt-1">
            {activityRequires.map((req, idx) => (
              <div key={idx} className="p-1.5 rounded bg-gray-900 border border-gray-800 font-mono text-xs text-indigo-300">
                {req}
              </div>
            ))}
          </div>
        </CollapsibleSection>

        {/* Next Activities */}
        <CollapsibleSection
          title="Next Activities"
          count={nextActivities.length}
          icon={<ArrowRight size={13} className="text-blue-400" />}
          hideIfZero={true}
        >
          <div className="space-y-1 mt-1">
            {nextActivities.map((nxt, idx) => (
              <button
                key={idx}
                onClick={() => setSelectedNodeId(nxt)}
                className="w-full p-1.5 rounded bg-gray-900 border border-gray-800 text-left font-medium text-xs text-gray-200 hover:border-blue-500 transition"
              >
                {nxt}
              </button>
            ))}
          </div>
        </CollapsibleSection>

        {/* Conditions */}
        <CollapsibleSection
          title="Branch Conditions"
          count={conditions.length}
          icon={<GitMerge size={13} className="text-amber-400" />}
          hideIfZero={true}
        >
          <div className="space-y-1 mt-1">
            {conditions.map((cond, idx) => (
              <div key={idx} className="p-1.5 rounded bg-amber-950/20 border border-amber-800/40 font-mono text-xs text-amber-300">
                {cond}
              </div>
            ))}
          </div>
        </CollapsibleSection>

        {/* Timing */}
        {timing && (
          <CollapsibleSection
            title="Execution Timing"
            icon={<Clock size={13} className="text-cyan-400" />}
            hideIfZero={false}
          >
            <div className="mt-1 p-1.5 rounded bg-gray-900 border border-gray-800 font-mono text-xs text-cyan-300">
              {timing}
            </div>
          </CollapsibleSection>
        )}

        {/* Relevant Actions */}
        <div className="p-3 mt-auto border-t border-gray-800 space-y-1.5">
          <button
            onClick={() => handleJumpToSymbol(selectedSymbol)}
            className="w-full py-1.5 px-2.5 bg-blue-600 hover:bg-blue-500 text-white rounded text-xs font-semibold flex items-center justify-center gap-1.5 transition shadow"
          >
            <ExternalLink size={13} />
            <span>Jump to Code Definition (L{selectedSymbol.line || 1})</span>
          </button>
          <button
            onClick={() => setSelectedNodeId(null)}
            className="w-full py-1 px-2.5 bg-gray-800 hover:bg-gray-700 text-gray-300 rounded text-xs transition"
          >
            Back to {activeFile.name}
          </button>
        </div>
      </div>
    );
  }

  // -----------------------------------------------------------------
  // STATE A/B: ARTIFACT SELECTED / NO SPECIFIC SYMBOL SELECTED
  // -----------------------------------------------------------------
  return (
    <div className="flex-1 flex flex-col min-h-0 overflow-y-auto select-none">
      {/* 1. STRUCTURE SECTION (Declared AST Symbols) */}
      <CollapsibleSection
        title="Structure"
        count={symbols.length}
        icon={<Layers size={13} className="text-indigo-400" />}
        hideIfZero={false}
      >
        <div className="space-y-1 mt-1">
          {symbols.length === 0 ? (
            <div className="text-gray-500 text-[11px] py-1">No declared symbols in file.</div>
          ) : (
            symbols.map(sym => (
              <div
                key={sym.id}
                onClick={() => handleJumpToSymbol(sym)}
                className="group p-1.5 rounded hover:bg-gray-800/80 border border-transparent hover:border-gray-700/80 flex items-center justify-between cursor-pointer transition text-xs"
              >
                <div className="flex items-center gap-2 min-w-0">
                  {getSymbolIcon(sym.kind)}
                  <span className="font-medium text-gray-200 group-hover:text-blue-300 truncate">
                    {sym.name}
                  </span>
                </div>
                {sym.line && (
                  <span className="font-mono text-[10px] text-gray-500 group-hover:text-gray-400">
                    L{sym.line}
                  </span>
                )}
              </div>
            ))
          )}
        </div>
      </CollapsibleSection>

      {/* 2. DEPENDENCIES / CONNECTIONS */}
      {relations && relations.requires.length > 0 && (
        <CollapsibleSection
          title="Dependencies"
          count={relations.requires.length}
          icon={<ArrowRight size={13} className="text-emerald-400" />}
          hideIfZero={true}
        >
          <div className="space-y-1.5 mt-1">
            {relations.requires.map((req, idx) => (
              <div
                key={idx}
                onClick={() => req.targetFileId && handleOpenRelatedFile(req.targetFileId)}
                className={`p-1.5 rounded bg-gray-900 border border-gray-800 flex items-center justify-between text-xs ${
                  req.targetFileId ? 'hover:border-blue-500 cursor-pointer group' : ''
                }`}
              >
                <div className="flex items-center gap-1.5 min-w-0">
                  {req.type === 'capability' ? (
                    <Cpu size={12} className="text-purple-400 shrink-0" />
                  ) : (
                    <Settings size={12} className="text-amber-400 shrink-0" />
                  )}
                  <span className="font-medium text-gray-300 group-hover:text-blue-300 truncate">
                    {req.name}
                  </span>
                </div>
                {req.targetFileId && (
                  <ArrowUpRight size={12} className="text-gray-500 group-hover:text-blue-400 shrink-0" />
                )}
              </div>
            ))}
          </div>
        </CollapsibleSection>
      )}

      {/* 3. PRODUCED OUTPUTS */}
      {relations && relations.produces.length > 0 && (
        <CollapsibleSection
          title="Produces"
          count={relations.produces.length}
          icon={<Sparkles size={13} className="text-purple-400" />}
          hideIfZero={true}
        >
          <div className="space-y-1.5 mt-1">
            {relations.produces.map((prod, idx) => (
              <div key={idx} className="p-1.5 rounded bg-purple-950/20 border border-purple-800/40 text-xs">
                <div className="font-semibold text-purple-200">{prod.name}</div>
                <div className="text-[10px] text-purple-400/80">{prod.type}</div>
              </div>
            ))}
          </div>
        </CollapsibleSection>
      )}

      {/* 4. SISTER SYSTEM ARTIFACTS */}
      {relations && relations.relatedFiles.length > 0 && (
        <CollapsibleSection
          title="Related System Files"
          count={relations.relatedFiles.length}
          icon={<Database size={13} className="text-sky-400" />}
          hideIfZero={true}
          defaultExpanded={false}
        >
          <div className="space-y-1 mt-1">
            {relations.relatedFiles.map(rf => (
              <button
                key={rf.fileId}
                onClick={() => handleOpenRelatedFile(rf.fileId)}
                className="w-full p-1.5 rounded bg-gray-900/60 border border-gray-800 hover:border-blue-500 text-left flex items-center justify-between text-xs text-gray-300 hover:text-blue-300 transition group"
              >
                <span className="font-mono truncate">{rf.name}</span>
                <ArrowUpRight size={12} className="text-gray-500 group-hover:text-blue-400 shrink-0" />
              </button>
            ))}
          </div>
        </CollapsibleSection>
      )}

      {/* 5. ISSUES (Auto-surfaced when present; errors visually outrank warnings) */}
      {fileIssues.length > 0 && (
        <CollapsibleSection
          title={errorCount > 0 ? 'Errors' : 'Warnings'}
          count={fileIssues.length}
          icon={errorCount > 0 ? <AlertCircle size={13} className="text-rose-400" /> : <AlertTriangle size={13} className="text-amber-400" />}
          badgeClass={errorCount > 0 ? 'bg-rose-950 text-rose-300 border border-rose-800' : 'bg-amber-950 text-amber-300 border border-amber-800'}
          hideIfZero={true}
        >
          <div className="space-y-1.5 mt-1">
            {fileIssues.map((issue, idx) => (
              <div
                key={idx}
                onClick={() => issue.line && navigateToDefinition(activeFile.id, issue.line, issue.column || 1)}
                className={`p-2 rounded border cursor-pointer transition text-xs ${
                  issue.severity === 'error'
                    ? 'bg-rose-950/20 border-rose-800/40 hover:border-rose-600 text-rose-300'
                    : 'bg-amber-950/20 border-amber-800/40 hover:border-amber-600 text-amber-300'
                }`}
              >
                <div className="flex items-center justify-between font-mono text-[10px] mb-0.5">
                  <span className="font-bold uppercase tracking-wider">{issue.severity}</span>
                  {issue.line && <span>Line {issue.line}</span>}
                </div>
                <div className="text-gray-200 leading-snug">{issue.message}</div>
              </div>
            ))}
          </div>
        </CollapsibleSection>
      )}

      {/* 6. DETAILS / METADATA */}
      <CollapsibleSection
        title="Details"
        icon={<Tag size={13} className="text-gray-400" />}
        hideIfZero={false}
        defaultExpanded={false}
      >
        <div className="p-2 rounded bg-gray-900 border border-gray-800 font-mono text-[11px] space-y-1 text-gray-300 mt-1">
          <div className="flex justify-between">
            <span className="text-gray-500">File:</span>
            <span className="text-gray-200 font-semibold">{activeFile.name}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-500">Language:</span>
            <span className="text-blue-400">{activeFile.language}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-500">Lines:</span>
            <span>{activeFile.content.split('\n').length}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-500">Size:</span>
            <span>{activeFile.content.length} bytes</span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-500">Status:</span>
            <span className={isDirty ? 'text-amber-400' : 'text-emerald-400'}>
              {isDirty ? 'Unsaved Edits' : 'Saved'}
            </span>
          </div>
        </div>
      </CollapsibleSection>

      {/* 7. RELEVANT ACTIONS */}
      <div className="p-3 mt-auto border-t border-gray-800 space-y-1.5">
        <div className="text-[10px] uppercase font-bold text-gray-500 tracking-wider mb-1">
          Actions
        </div>
        {activeFile.name.endsWith('.activity') && (
          <button
            onClick={() => setActiveView('workflow')}
            className="w-full py-1.5 px-2.5 bg-indigo-600 hover:bg-indigo-500 text-white rounded text-xs font-semibold flex items-center justify-center gap-1.5 transition shadow"
          >
            <Activity size={13} />
            <span>Show in Workflow Diagram</span>
          </button>
        )}
        <button
          onClick={() => setActiveView('knowledgegraph')}
          className="w-full py-1.5 px-2.5 bg-gray-800 hover:bg-gray-700 text-gray-200 rounded text-xs font-semibold flex items-center justify-center gap-1.5 transition border border-gray-700"
        >
          <Layers size={13} />
          <span>Inspect in Project Graph</span>
        </button>
      </div>
    </div>
  );
};
