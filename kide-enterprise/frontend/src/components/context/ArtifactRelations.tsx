import React, { useMemo, useState, useEffect } from 'react';
import { useEditorStore, useActiveFile } from '../../stores/editorStore';
import { 
  Network, ArrowUpRight, Cpu, Settings, Activity, 
  Sparkles, FileCode2, ExternalLink, Zap, RefreshCw
} from 'lucide-react';
import { ImpactAnalysisModal } from '../workflow/ImpactAnalysisModal';
import { getProjectTraceabilityMatrix, getBufferTraceabilityMatrix } from '../../api/traceability';
import { TraceabilityMatrixResponse, TraceabilityMatrixRow } from '../../types/traceability';

export const ArtifactRelations: React.FC = () => {
  const activeFile = useActiveFile();
  const { projectId, files, setActiveFileId, setActiveView } = useEditorStore();

  const [matrixData, setMatrixData] = useState<TraceabilityMatrixResponse | null>(null);
  const [matrixLoading, setMatrixLoading] = useState<boolean>(false);
  const [isImpactModalOpen, setIsImpactModalOpen] = useState<boolean>(false);
  const [impactModalSymbol, setImpactModalSymbol] = useState<string>('');
  const [impactModalAction, setImpactModalAction] = useState<'modify' | 'delete' | 'rename'>('delete');

  // Load Traceability Matrix on project or files change
  useEffect(() => {
    let isCancelled = false;
    const loadMatrix = async () => {
      setMatrixLoading(true);
      try {
        let res: TraceabilityMatrixResponse;
        if (projectId) {
          res = await getProjectTraceabilityMatrix(projectId);
        } else {
          const filesMap: Record<string, string> = {};
          files.forEach(f => {
            filesMap[f.name] = f.content || '';
          });
          res = await getBufferTraceabilityMatrix(filesMap);
        }
        if (!isCancelled) {
          setMatrixData(res);
        }
      } catch (err) {
        console.warn('Traceability matrix load deferred:', err);
      } finally {
        if (!isCancelled) setMatrixLoading(false);
      }
    };

    loadMatrix();
    return () => {
      isCancelled = true;
    };
  }, [projectId, files]);

  // Find matrix row matching active file
  const activeMatrixRow: TraceabilityMatrixRow | undefined = useMemo(() => {
    if (!activeFile || !matrixData) return undefined;
    const baseName = activeFile.name.replace(/\.[^/.]+$/, '');
    return matrixData.rows.find(r => 
      r.filename.toLowerCase() === activeFile.name.toLowerCase() ||
      r.symbol.toLowerCase() === baseName.toLowerCase()
    );
  }, [activeFile, matrixData]);

  const relations = useMemo(() => {
    if (!activeFile) return null;

    const lowerName = activeFile.name.toLowerCase();
    const content = activeFile.content;

    const requires: Array<{ name: string; type: 'capability' | 'operation'; targetFileId?: string }> = [];
    const produces: Array<{ name: string; type: string; targetFileId?: string }> = [];
    const generates: Array<{ name: string; target: string; ext: string }> = [];
    const relatedFiles: Array<{ name: string; type: string; fileId: string }> = [];

    // 1. If Activity Workflow
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
        requires.push({
          name: capName,
          type: 'capability',
          targetFileId: matchFile?.id
        });
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
        requires.push({
          name: opName,
          type: 'operation',
          targetFileId: matchFile?.id
        });
      }

      produces.push({
        name: `${activeFile.name.replace(/\.[^/.]+$/, '')} State Machine Automata`,
        type: 'Supervisory Automata'
      });

      generates.push(
        { name: 'Python Controller', target: 'python', ext: '.py' },
        { name: 'ROS2 Supervisory Node', target: 'ros2', ext: '.py' },
        { name: 'PLC (IEC 61131-3)', target: 'plc_st', ext: '.st' },
        { name: 'Java Supervisor Controller', target: 'java', ext: '.java' },
        { name: 'Embedded C++ Controller', target: 'cpp', ext: '.hpp' }
      );
    }

    // 2. If Capability
    if (lowerName.endsWith('.cap') || lowerName.endsWith('.capability')) {
      const ifMatch = content.match(/interface\s+([A-Za-z0-9_]+)/);
      if (ifMatch) {
        requires.push({
          name: ifMatch[1],
          type: 'operation'
        });
      }
      const activityFiles = files.filter(f => f.name.endsWith('.activity') && f.content.includes(activeFile.name.replace(/\.[^/.]+$/, '')));
      for (const af of activityFiles) {
        produces.push({
          name: af.name,
          type: 'Consuming Workflow',
          targetFileId: af.id
        });
      }
    }

    // 3. Find other files sharing the base prefix
    const basePrefix = activeFile.name.split('.')[0].replace(/(_Cap|_Ops|_Data|System|Control|Cell)$/i, '');
    if (basePrefix.length > 2) {
      for (const f of files) {
        if (f.id !== activeFile.id && f.name.toLowerCase().includes(basePrefix.toLowerCase())) {
          relatedFiles.push({
            name: f.name,
            type: f.language,
            fileId: f.id
          });
        }
      }
    }

    return { requires, produces, generates, relatedFiles };
  }, [activeFile, files]);

  if (!activeFile || !relations) {
    return (
      <div className="flex flex-col items-center justify-center p-6 text-center text-gray-500 h-full select-none">
        <Network className="w-7 h-7 text-gray-600 mb-2" />
        <p className="text-xs font-medium text-gray-300">No Artifact Selected</p>
        <p className="text-[11px] text-gray-500 mt-1">Select an artifact to inspect its dependency relationships.</p>
      </div>
    );
  }

  const handleOpenRelatedFile = (fileId: string) => {
    setActiveFileId(fileId);
    setActiveView('editor');
  };

  const handleOpenGeneratedTarget = () => {
    setActiveView('codegen');
  };

  const triggerImpactAnalysis = (symbolName: string, action: 'modify' | 'delete' | 'rename' = 'delete') => {
    setImpactModalSymbol(symbolName);
    setImpactModalAction(action);
    setIsImpactModalOpen(true);
  };

  const activeSymbolName = activeMatrixRow?.symbol || activeFile.name.replace(/\.[^/.]+$/, '');

  return (
    <div className="flex-1 overflow-y-auto py-2 px-3 space-y-4 text-xs select-none">
      {/* ACTION BAR: CHANGE IMPACT & RECONFIGURATION */}
      <div className="p-2 bg-gradient-to-r from-cyan-950/40 to-blue-950/40 border border-cyan-800/40 rounded-lg space-y-2">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-1.5 text-cyan-300 font-semibold text-xs">
            <Zap size={14} className="text-cyan-400" />
            <span>Traceability & Impact</span>
          </div>
          {activeMatrixRow && (
            <span className="text-[10px] font-mono text-cyan-400 bg-cyan-900/40 px-1.5 py-0.5 rounded border border-cyan-700/50">
              Stage {activeMatrixRow.stage}
            </span>
          )}
        </div>

        <p className="text-[11px] text-gray-400">
          Analyze downstream blast radius or compute automated semantic reconfiguration for{' '}
          <span className="font-semibold text-gray-200">'{activeSymbolName}'</span>.
        </p>

        <div className="flex items-center gap-2">
          <button
            onClick={() => triggerImpactAnalysis(activeSymbolName, 'delete')}
            className="flex-1 flex items-center justify-center gap-1 py-1.5 px-2 rounded bg-cyan-600 hover:bg-cyan-500 text-white font-medium text-[11px] transition shadow-sm"
          >
            <Zap size={12} />
            <span>Analyze Blast Radius</span>
          </button>

          {(activeFile.name.endsWith('.cap') || activeFile.name.endsWith('.capability')) && (
            <button
              onClick={() => triggerImpactAnalysis(activeSymbolName, 'modify')}
              className="flex items-center justify-center gap-1 py-1.5 px-2 rounded bg-purple-600/80 hover:bg-purple-600 text-white font-medium text-[11px] transition"
              title="Substitute with another capability"
            >
              <RefreshCw size={12} />
              <span>Reconfigure</span>
            </button>
          )}
        </div>
      </div>

      {/* MATRIX STATS IF AVAILABLE */}
      {matrixData && (
        <div className="flex items-center justify-between px-2 py-1 bg-gray-900/50 rounded border border-gray-800/60 text-[11px] text-gray-400">
          <span>Trace Coverage:</span>
          <span className="font-mono text-emerald-400 font-bold">
            {matrixLoading ? 'Updating...' : `${matrixData.coverage_percentage}% (${matrixData.total_links} links)`}
          </span>
        </div>
      )}

      {/* UPSTREAM DEPENDENCIES (REQUIRES) */}
      <div>
        <div className="text-[10px] font-bold text-gray-400 uppercase tracking-wider mb-1.5 flex items-center justify-between">
          <span>UPSTREAM DEPENDENCIES (REQUIRES)</span>
          <span className="font-mono text-gray-500">
            {activeMatrixRow ? activeMatrixRow.upstream_symbols.length : relations.requires.length}
          </span>
        </div>
        {relations.requires.length === 0 && (!activeMatrixRow || activeMatrixRow.upstream_symbols.length === 0) ? (
          <p className="text-[11px] text-gray-500 italic pl-1">No upstream dependencies declared.</p>
        ) : (
          <div className="space-y-1">
            {relations.requires.map((req, idx) => (
              <div
                key={idx}
                className="flex items-center justify-between p-1.5 rounded bg-gray-900/40 hover:bg-gray-800/80 transition group"
              >
                <div 
                  onClick={() => req.targetFileId && handleOpenRelatedFile(req.targetFileId)}
                  className="flex items-center gap-2 overflow-hidden flex-1 cursor-pointer"
                >
                  {req.type === 'capability' ? (
                    <Cpu size={13} className="text-purple-400 shrink-0" />
                  ) : (
                    <Settings size={13} className="text-amber-400 shrink-0" />
                  )}
                  <span className="truncate font-medium text-gray-200">{req.name}</span>
                </div>

                <div className="flex items-center gap-1">
                  <button
                    onClick={() => triggerImpactAnalysis(req.name, 'delete')}
                    title={`Analyze impact of deleting ${req.name}`}
                    className="opacity-0 group-hover:opacity-100 p-1 rounded hover:bg-gray-700 text-gray-400 hover:text-cyan-300 transition"
                  >
                    <Zap size={11} />
                  </button>
                  {req.targetFileId && (
                    <button
                      onClick={() => handleOpenRelatedFile(req.targetFileId!)}
                      className="p-1 text-gray-500 group-hover:text-gray-300"
                    >
                      <ArrowUpRight size={12} />
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* DOWNSTREAM IMPACTS (PRODUCES & SUPERVISORY AUTOMATA) */}
      {relations.produces.length > 0 && (
        <div className="border-t border-gray-800/60 pt-3">
          <div className="text-[10px] font-bold text-gray-400 uppercase tracking-wider mb-1.5 flex items-center justify-between">
            <span>DOWNSTREAM IMPACTS (PRODUCES)</span>
            <span className="font-mono text-gray-500">{relations.produces.length}</span>
          </div>
          <div className="space-y-1">
            {relations.produces.map((prod, idx) => (
              <div
                key={idx}
                onClick={() => {
                  if (prod.targetFileId) handleOpenRelatedFile(prod.targetFileId);
                  else setActiveView('statemachine');
                }}
                className="flex items-center justify-between p-1.5 rounded hover:bg-gray-800/80 cursor-pointer text-gray-200 transition"
              >
                <div className="flex items-center gap-2 overflow-hidden">
                  <Activity size={13} className="text-emerald-400 shrink-0" />
                  <span className="truncate font-medium">{prod.name}</span>
                </div>
                <ArrowUpRight size={12} className="text-gray-500 shrink-0" />
              </div>
            ))}
          </div>
        </div>
      )}

      {/* GENERATES CONTROLLER TARGETS */}
      {relations.generates.length > 0 && (
        <div className="border-t border-gray-800/60 pt-3">
          <div className="text-[10px] font-bold text-gray-400 uppercase tracking-wider mb-1.5 flex items-center justify-between">
            <span>GENERATES CONTROLLER TARGETS</span>
            <span className="font-mono text-gray-500">{relations.generates.length}</span>
          </div>
          <div className="space-y-1">
            {relations.generates.map((gen, idx) => (
              <div
                key={idx}
                onClick={handleOpenGeneratedTarget}
                className="flex items-center justify-between p-1.5 rounded hover:bg-gray-800/80 cursor-pointer text-gray-200 transition"
              >
                <div className="flex items-center gap-2 overflow-hidden">
                  <Sparkles size={13} className="text-cyan-400 shrink-0" />
                  <span className="truncate font-medium">{gen.name}</span>
                </div>
                <span className="text-[10px] text-gray-500 font-mono">{gen.ext}</span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* RELATED ARTIFACTS IN SAME SYSTEM */}
      {relations.relatedFiles.length > 0 && (
        <div className="border-t border-gray-800/60 pt-3">
          <div className="text-[10px] font-bold text-gray-400 uppercase tracking-wider mb-1.5 flex items-center justify-between">
            <span>RELATED ARTIFACTS</span>
            <span className="font-mono text-gray-500">{relations.relatedFiles.length}</span>
          </div>
          <div className="space-y-1">
            {relations.relatedFiles.map((rf, idx) => (
              <div
                key={idx}
                onClick={() => handleOpenRelatedFile(rf.fileId)}
                className="flex items-center justify-between p-1.5 rounded hover:bg-gray-800/80 cursor-pointer text-gray-200 transition"
              >
                <div className="flex items-center gap-2 overflow-hidden">
                  <FileCode2 size={13} className="text-blue-400 shrink-0" />
                  <span className="truncate font-medium">{rf.name}</span>
                </div>
                <ArrowUpRight size={12} className="text-gray-500 shrink-0" />
              </div>
            ))}
          </div>
        </div>
      )}

      {/* KNOWLEDGE GRAPH LINK */}
      <div className="pt-3 border-t border-gray-800/60">
        <button
          onClick={() => setActiveView('knowledgegraph')}
          className="w-full flex items-center justify-center gap-1.5 py-1.5 px-2.5 rounded bg-emerald-950/30 border border-emerald-800/40 text-emerald-300 hover:bg-emerald-950/50 transition font-medium text-[11px]"
        >
          <Network size={13} />
          <span>View in Interactive Knowledge Graph</span>
          <ExternalLink size={11} className="ml-1" />
        </button>
      </div>

      {/* MODAL */}
      <ImpactAnalysisModal
        isOpen={isImpactModalOpen}
        onClose={() => setIsImpactModalOpen(false)}
        initialSymbol={impactModalSymbol}
        initialFile={activeFile.name}
        initialAction={impactModalAction}
      />
    </div>
  );
};
