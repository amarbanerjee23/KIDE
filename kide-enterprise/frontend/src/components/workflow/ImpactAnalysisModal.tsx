import React, { useState, useEffect } from 'react';
import { 
  AlertTriangle, ShieldAlert, CheckCircle2, RefreshCw, 
  FileText, Zap, ChevronDown, ChevronUp, Check, Loader2
} from 'lucide-react';
import Modal from '../common/Modal';
import { useEditorStore } from '../../stores/editorStore';
import { 
  ImpactAnalysisResponse, 
  ReconfigurationProposal, 
  ImpactRiskLevel 
} from '../../types/traceability';
import { 
  analyzeProjectImpact, 
  analyzeBufferImpact, 
  reconfigureProjectCapability, 
  reconfigureBufferCapability 
} from '../../api/traceability';
import { applyAiPatch } from '../../api/ai';

interface ImpactAnalysisModalProps {
  isOpen: boolean;
  onClose: () => void;
  initialSymbol?: string;
  initialFile?: string;
  initialAction?: 'modify' | 'delete' | 'rename';
}

export const ImpactAnalysisModal: React.FC<ImpactAnalysisModalProps> = ({
  isOpen,
  onClose,
  initialSymbol = '',
  initialFile = '',
  initialAction = 'delete',
}) => {
  const { projectId, files, updateFileContent, addFile, setActiveFileId } = useEditorStore();

  const [symbol, setSymbol] = useState(initialSymbol);
  const [targetFile, setTargetFile] = useState(initialFile);
  const [action, setAction] = useState<'modify' | 'delete' | 'rename'>(initialAction);
  const [newName, setNewName] = useState('');
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [impactData, setImpactData] = useState<ImpactAnalysisResponse | null>(null);

  // Reconfiguration State
  const [replacementCap, setReplacementCap] = useState('');
  const [reconfigLoading, setReconfigLoading] = useState(false);
  const [reconfigProposal, setReconfigProposal] = useState<ReconfigurationProposal | null>(null);
  const [expandedDiffs, setExpandedDiffs] = useState<Record<string, boolean>>({});
  const [appliedPatches, setAppliedPatches] = useState<Record<string, boolean>>({});

  useEffect(() => {
    if (isOpen) {
      setSymbol(initialSymbol);
      setTargetFile(initialFile);
      setAction(initialAction);
      setImpactData(null);
      setReconfigProposal(null);
      setError(null);
      setAppliedPatches({});
      if (initialSymbol) {
        handleRunImpactAnalysis(initialSymbol, initialFile, initialAction);
      }
    }
  }, [isOpen, initialSymbol, initialFile, initialAction]);

  const getFilesMap = () => {
    const map: Record<string, string> = {};
    files.forEach(f => {
      map[f.name] = f.content || '';
    });
    return map;
  };

  const handleRunImpactAnalysis = async (
    targetSym = symbol, 
    tgtFile = targetFile, 
    act = action
  ) => {
    if (!targetSym && !tgtFile) return;

    setLoading(true);
    setError(null);
    try {
      let res: ImpactAnalysisResponse;
      if (projectId) {
        res = await analyzeProjectImpact(projectId, {
          target_symbol: targetSym || undefined,
          target_file: tgtFile || undefined,
          action: act,
          new_name: act === 'rename' ? newName : undefined,
        });
      } else {
        res = await analyzeBufferImpact(getFilesMap(), {
          target_symbol: targetSym || undefined,
          target_file: tgtFile || undefined,
          action: act,
          new_name: act === 'rename' ? newName : undefined,
        });
      }
      setImpactData(res);
      // Pre-fill replacement capability hint if target is capability
      if (res.target_type === 'capability' || res.target_symbol.toLowerCase().includes('cool')) {
        setReplacementCap('SmartChillerV2');
      }
    } catch (err: any) {
      setError(err.message || 'Failed to analyze impact');
    } finally {
      setLoading(false);
    }
  };

  const handleRunReconfiguration = async () => {
    if (!symbol && !impactData?.target_symbol) return;
    const depCap = symbol || impactData?.target_symbol || '';
    const repCap = replacementCap.trim();
    if (!repCap) return;

    setReconfigLoading(true);
    setError(null);
    try {
      let proposal: ReconfigurationProposal;
      if (projectId) {
        proposal = await reconfigureProjectCapability(projectId, {
          deprecated_capability: depCap,
          replacement_capability: repCap,
        });
      } else {
        proposal = await reconfigureBufferCapability(getFilesMap(), {
          deprecated_capability: depCap,
          replacement_capability: repCap,
        });
      }
      setReconfigProposal(proposal);
      // Expand diffs by default
      const diffMap: Record<string, boolean> = {};
      proposal.patches.forEach((_, idx) => {
        diffMap[`patch_${idx}`] = true;
      });
      setExpandedDiffs(diffMap);
    } catch (err: any) {
      setError(err.message || 'Failed to compute reconfiguration plan');
    } finally {
      setReconfigLoading(false);
    }
  };

  const handleApplyReconfigurationPatch = async (patchIndex: number) => {
    if (!reconfigProposal) return;
    const patch = reconfigProposal.patches[patchIndex];
    if (!patch) return;

    try {
      if (projectId) {
        await applyAiPatch(projectId, undefined, [patch]);
      }
      // Update local editorStore
      const existing = files.find(f => f.name.toLowerCase() === patch.filename.toLowerCase());
      if (existing) {
        updateFileContent(existing.id, patch.new_content);
        setActiveFileId(existing.id);
      } else {
        const newId = `file_${Date.now()}`;
        addFile({
          id: newId,
          name: patch.filename,
          content: patch.new_content,
          language: 'activity',
        });
        setActiveFileId(newId);
      }

      setAppliedPatches(prev => ({ ...prev, [`patch_${patchIndex}`]: true }));
    } catch (err: any) {
      alert(`Failed to apply reconfiguration patch: ${err.message}`);
    }
  };

  const getRiskBadge = (risk: ImpactRiskLevel) => {
    switch (risk) {
      case 'CRITICAL':
        return 'bg-red-950/70 text-red-300 border-red-800 font-bold';
      case 'HIGH':
        return 'bg-orange-950/70 text-orange-300 border-orange-800 font-bold';
      case 'MEDIUM':
        return 'bg-amber-950/70 text-amber-300 border-amber-800 font-medium';
      case 'LOW':
      default:
        return 'bg-emerald-950/70 text-emerald-300 border-emerald-800 font-medium';
    }
  };

  const toggleDiff = (key: string) => {
    setExpandedDiffs(prev => ({ ...prev, [key]: !prev[key] }));
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="Change Impact Analysis & Semantic Reconfiguration"
      className="max-w-4xl w-[90vw]"
    >
      <div className="space-y-4 text-xs select-none">
        {/* TOP CONTROLS */}
        <div className="p-3 bg-gray-900/80 rounded-lg border border-gray-800 space-y-3">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
            <div>
              <label className="block text-[11px] font-semibold text-gray-300 mb-1">
                Target Symbol
              </label>
              <input
                type="text"
                value={symbol}
                onChange={e => setSymbol(e.target.value)}
                placeholder="e.g. ChillerCooling"
                className="w-full px-2.5 py-1.5 bg-gray-950 border border-gray-700 rounded text-gray-200 text-xs focus:border-cyan-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="block text-[11px] font-semibold text-gray-300 mb-1">
                Proposed Action
              </label>
              <select
                value={action}
                onChange={e => setAction(e.target.value as any)}
                className="w-full px-2.5 py-1.5 bg-gray-950 border border-gray-700 rounded text-gray-200 text-xs focus:border-cyan-500 focus:outline-none"
              >
                <option value="delete">Delete / Deprecate Symbol</option>
                <option value="modify">Modify Signature / Contract</option>
                <option value="rename">Rename Symbol</option>
              </select>
            </div>

            <div className="flex items-end">
              <button
                onClick={() => handleRunImpactAnalysis()}
                disabled={loading || (!symbol && !targetFile)}
                className="w-full flex items-center justify-center gap-1.5 py-1.5 px-3 rounded bg-cyan-600 hover:bg-cyan-500 text-white font-semibold transition disabled:opacity-50"
              >
                {loading ? (
                  <>
                    <Loader2 size={13} className="animate-spin" />
                    <span>Analyzing Blast Radius...</span>
                  </>
                ) : (
                  <>
                    <Zap size={13} />
                    <span>Run Blast Radius Analysis</span>
                  </>
                )}
              </button>
            </div>
          </div>

          {action === 'rename' && (
            <div>
              <label className="block text-[11px] font-semibold text-gray-300 mb-1">
                New Symbol Name
              </label>
              <input
                type="text"
                value={newName}
                onChange={e => setNewName(e.target.value)}
                placeholder="e.g. EnhancedCoolingUnit"
                className="w-full px-2.5 py-1.5 bg-gray-950 border border-gray-700 rounded text-gray-200 text-xs focus:border-cyan-500 focus:outline-none"
              />
            </div>
          )}
        </div>

        {error && (
          <div className="p-2.5 bg-red-950/50 border border-red-800/80 rounded text-red-300 text-xs">
            {error}
          </div>
        )}

        {/* IMPACT ANALYSIS RESULTS */}
        {impactData && (
          <div className="space-y-4">
            {/* SUMMARY CARD */}
            <div className="p-3.5 bg-gray-900/60 rounded-lg border border-gray-800 flex flex-col md:flex-row md:items-center justify-between gap-3">
              <div>
                <div className="flex items-center gap-2">
                  <span className="text-sm font-bold text-gray-100">
                    Blast Radius for '{impactData.target_symbol}'
                  </span>
                  <span className="px-2 py-0.5 rounded text-[10px] uppercase font-mono bg-gray-800 text-gray-300 border border-gray-700">
                    {impactData.target_type}
                  </span>
                </div>
                <p className="text-[11px] text-gray-400 mt-0.5">
                  Action: <span className="font-semibold text-gray-200 capitalize">{impactData.action}</span> | Downstream Impact: <span className="font-mono text-cyan-400 font-bold">{impactData.impacted_symbols_count} symbols</span>
                </p>
              </div>

              <div className="flex items-center gap-2">
                <span className="text-[11px] text-gray-400">Assessed Risk Level:</span>
                <span className={`px-2.5 py-1 rounded-md text-xs border ${getRiskBadge(impactData.risk_level)}`}>
                  {impactData.risk_level}
                </span>
              </div>
            </div>

            {/* AFFECTED STATS CHIPS */}
            <div className="grid grid-cols-2 md:grid-cols-4 gap-2">
              <div className="p-2.5 bg-gray-900/40 rounded border border-gray-800 text-center">
                <span className="block text-[10px] text-gray-400 font-medium uppercase">Activities</span>
                <span className="text-sm font-mono font-bold text-purple-400">
                  {impactData.affected_activities.length}
                </span>
              </div>
              <div className="p-2.5 bg-gray-900/40 rounded border border-gray-800 text-center">
                <span className="block text-[10px] text-gray-400 font-medium uppercase">Operating States</span>
                <span className="text-sm font-mono font-bold text-emerald-400">
                  {impactData.affected_states.length}
                </span>
              </div>
              <div className="p-2.5 bg-gray-900/40 rounded border border-gray-800 text-center">
                <span className="block text-[10px] text-gray-400 font-medium uppercase">Broken Transitions</span>
                <span className="text-sm font-mono font-bold text-rose-400">
                  {impactData.broken_transitions.length}
                </span>
              </div>
              <div className="p-2.5 bg-gray-900/40 rounded border border-gray-800 text-center">
                <span className="block text-[10px] text-gray-400 font-medium uppercase">Code Targets</span>
                <span className="text-sm font-mono font-bold text-cyan-400">
                  {impactData.affected_code_generators.length}
                </span>
              </div>
            </div>

            {/* BREAKING HAZARDS & MITIGATIONS */}
            {(impactData.breaking_hazards.length > 0 || impactData.recommended_mitigations.length > 0) && (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                {impactData.breaking_hazards.length > 0 && (
                  <div className="p-3 bg-rose-950/20 border border-rose-900/50 rounded-lg space-y-2">
                    <div className="flex items-center gap-1.5 text-rose-400 font-semibold text-xs">
                      <AlertTriangle size={14} />
                      <span>Breaking Hazards Detected ({impactData.breaking_hazards.length})</span>
                    </div>
                    <ul className="space-y-1 pl-1">
                      {impactData.breaking_hazards.map((hz, idx) => (
                        <li key={idx} className="text-[11px] text-rose-300/90 flex items-start gap-1.5">
                          <span className="text-rose-500 font-mono">•</span>
                          <span>{hz}</span>
                        </li>
                      ))}
                    </ul>
                  </div>
                )}

                {impactData.recommended_mitigations.length > 0 && (
                  <div className="p-3 bg-emerald-950/20 border border-emerald-900/50 rounded-lg space-y-2">
                    <div className="flex items-center gap-1.5 text-emerald-400 font-semibold text-xs">
                      <ShieldAlert size={14} />
                      <span>Recommended Mitigations</span>
                    </div>
                    <ul className="space-y-1 pl-1">
                      {impactData.recommended_mitigations.map((mt, idx) => (
                        <li key={idx} className="text-[11px] text-emerald-300/90 flex items-start gap-1.5">
                          <CheckCircle2 size={12} className="text-emerald-400 shrink-0 mt-0.5" />
                          <span>{mt}</span>
                        </li>
                      ))}
                    </ul>
                  </div>
                )}
              </div>
            )}

            {/* IMPACTED ITEMS TABLE */}
            <div>
              <h4 className="text-xs font-bold text-gray-300 mb-2 flex items-center justify-between">
                <span>Detailed Downstream Traversal ({impactData.impacted_items.length} items)</span>
              </h4>
              <div className="max-h-48 overflow-y-auto border border-gray-800 rounded-lg bg-gray-950/80">
                <table className="w-full text-left border-collapse text-[11px]">
                  <thead>
                    <tr className="border-b border-gray-800 bg-gray-900/70 text-gray-400">
                      <th className="p-2 font-medium">Stage</th>
                      <th className="p-2 font-medium">Symbol</th>
                      <th className="p-2 font-medium">Type</th>
                      <th className="p-2 font-medium">File</th>
                      <th className="p-2 font-medium">Risk</th>
                      <th className="p-2 font-medium">Impact Reason</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-800/60">
                    {impactData.impacted_items.map((item, idx) => (
                      <tr key={idx} className="hover:bg-gray-900/50">
                        <td className="p-2 font-mono text-cyan-400">Stage {item.stage}</td>
                        <td className="p-2 font-semibold text-gray-200">{item.symbol}</td>
                        <td className="p-2 font-mono text-gray-400">{item.symbol_type}</td>
                        <td className="p-2 text-gray-400">{item.filename}</td>
                        <td className="p-2">
                          <span className={`px-1.5 py-0.5 rounded text-[10px] border ${getRiskBadge(item.risk)}`}>
                            {item.risk}
                          </span>
                        </td>
                        <td className="p-2 text-gray-300">{item.impact_reason}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>

            {/* AUTOMATED SEMANTIC RECONFIGURATION PANEL */}
            <div className="p-3.5 bg-purple-950/20 border border-purple-900/50 rounded-lg space-y-3">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <RefreshCw size={15} className="text-purple-400" />
                  <span className="font-bold text-gray-200 text-xs">
                    Automated Semantic Reconfiguration Engine
                  </span>
                </div>
                <span className="text-[10px] text-purple-300 font-mono bg-purple-900/40 px-2 py-0.5 rounded border border-purple-700/50">
                  Zero-Deadlock In-Memory Solver
                </span>
              </div>

              <div className="flex items-center gap-2">
                <div className="flex-1">
                  <label className="block text-[11px] text-gray-400 mb-1">
                    Substitute Deprecated '{symbol || impactData.target_symbol}' with Compatible Replacement:
                  </label>
                  <input
                    type="text"
                    value={replacementCap}
                    onChange={e => setReplacementCap(e.target.value)}
                    placeholder="e.g. SmartChillerV2"
                    className="w-full px-2.5 py-1.5 bg-gray-950 border border-purple-800/60 rounded text-gray-200 text-xs focus:border-purple-500 focus:outline-none"
                  />
                </div>
                <div className="self-end">
                  <button
                    onClick={handleRunReconfiguration}
                    disabled={reconfigLoading || !replacementCap.trim()}
                    className="flex items-center gap-1.5 py-1.5 px-3 rounded bg-purple-600 hover:bg-purple-500 text-white font-semibold transition disabled:opacity-50 text-xs"
                  >
                    {reconfigLoading ? (
                      <>
                        <Loader2 size={13} className="animate-spin" />
                        <span>Synthesizing Plan...</span>
                      </>
                    ) : (
                      <>
                        <RefreshCw size={13} />
                        <span>Generate Reconfiguration Plan</span>
                      </>
                    )}
                  </button>
                </div>
              </div>

              {/* RECONFIGURATION PROPOSAL RESULTS */}
              {reconfigProposal && (
                <div className="mt-3 p-3 bg-gray-950/80 rounded border border-purple-900/40 space-y-3">
                  <div className="flex items-center justify-between">
                    <div>
                      <span className="text-[11px] font-bold text-gray-200">Status: </span>
                      <span className={`font-mono uppercase font-bold text-xs ${
                        reconfigProposal.status === 'success' ? 'text-emerald-400' : 'text-amber-400'
                      }`}>
                        {reconfigProposal.status}
                      </span>
                      <p className="text-[11px] text-gray-300 mt-0.5">{reconfigProposal.explanation}</p>
                    </div>

                    {reconfigProposal.safety_verification?.safe && (
                      <div className="flex items-center gap-1 text-[11px] text-emerald-400 bg-emerald-950/40 px-2 py-1 rounded border border-emerald-800/50">
                        <CheckCircle2 size={13} />
                        <span>Safety Verified: 0 Deadlocks</span>
                      </div>
                    )}
                  </div>

                  {/* PATCHES */}
                  {reconfigProposal.patches.length > 0 && (
                    <div className="space-y-2">
                      <span className="text-[11px] font-semibold text-gray-400">
                        Synthesized Unified Diffs ({reconfigProposal.patches.length} file{reconfigProposal.patches.length > 1 ? 's' : ''}):
                      </span>

                      {reconfigProposal.patches.map((patch, pIdx) => {
                        const patchKey = `patch_${pIdx}`;
                        const isExpanded = expandedDiffs[patchKey];
                        const isApplied = appliedPatches[patchKey];

                        return (
                          <div key={pIdx} className="border border-gray-800 rounded bg-gray-900/60 overflow-hidden">
                            <div className="flex items-center justify-between p-2 bg-gray-900">
                              <div className="flex items-center gap-2">
                                <FileText size={13} className="text-cyan-400" />
                                <span className="font-mono text-gray-200 text-xs">{patch.filename}</span>
                              </div>

                              <div className="flex items-center gap-2">
                                <button
                                  onClick={() => toggleDiff(patchKey)}
                                  className="text-[11px] text-gray-400 hover:text-gray-200 flex items-center gap-0.5"
                                >
                                  {isExpanded ? <ChevronUp size={13} /> : <ChevronDown size={13} />}
                                  <span>{isExpanded ? 'Hide Diff' : 'View Diff'}</span>
                                </button>

                                <button
                                  onClick={() => handleApplyReconfigurationPatch(pIdx)}
                                  disabled={isApplied}
                                  className={`flex items-center gap-1 py-1 px-2 rounded text-[11px] font-medium transition ${
                                    isApplied 
                                      ? 'bg-emerald-900/40 text-emerald-300 border border-emerald-800/50 cursor-default' 
                                      : 'bg-cyan-600 hover:bg-cyan-500 text-white'
                                  }`}
                                >
                                  {isApplied ? (
                                    <>
                                      <Check size={12} />
                                      <span>Applied</span>
                                    </>
                                  ) : (
                                    <>
                                      <Check size={12} />
                                      <span>Apply Patch</span>
                                    </>
                                  )}
                                </button>
                              </div>
                            </div>

                            {isExpanded && (
                              <pre className="p-2.5 font-mono text-[10px] bg-black/80 text-gray-300 overflow-x-auto whitespace-pre leading-relaxed border-t border-gray-800">
                                {patch.diff ? (
                                  patch.diff.split('\n').map((line, lIdx) => {
                                    let lineClass = 'text-gray-400';
                                    if (line.startsWith('+') && !line.startsWith('+++')) lineClass = 'text-emerald-400 bg-emerald-950/20';
                                    else if (line.startsWith('-') && !line.startsWith('---')) lineClass = 'text-rose-400 bg-rose-950/20';
                                    else if (line.startsWith('@@')) lineClass = 'text-cyan-400';
                                    return (
                                      <div key={lIdx} className={lineClass}>
                                        {line}
                                      </div>
                                    );
                                  })
                                ) : (
                                  patch.new_content
                                )}
                              </pre>
                            )}
                          </div>
                        );
                      })}
                    </div>
                  )}
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </Modal>
  );
};
