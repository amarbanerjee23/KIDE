import React, { useMemo } from 'react';
import { useEditorStore, useActiveFile } from '../../stores/editorStore';
import { 
  Network, ArrowUpRight, Cpu, Settings, Activity, 
  Sparkles, FileCode2, ExternalLink 
} from 'lucide-react';

export const ArtifactRelations: React.FC = () => {
  const activeFile = useActiveFile();
  const { files, setActiveFileId, setActiveView } = useEditorStore();

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
      // Find required capabilities
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

      // Find required operations
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

      // Produces supervisory automata
      produces.push({
        name: `${activeFile.name.replace(/\.[^/.]+$/, '')} State Machine Automata`,
        type: 'Supervisory Automata'
      });

      // Generates standard 5 targets
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
      // Look for interface or operations
      const ifMatch = content.match(/interface\s+([A-Za-z0-9_]+)/);
      if (ifMatch) {
        requires.push({
          name: ifMatch[1],
          type: 'operation'
        });
      }
      // Used by activity workflows
      const activityFiles = files.filter(f => f.name.endsWith('.activity') && f.content.includes(activeFile.name.replace(/\.[^/.]+$/, '')));
      for (const af of activityFiles) {
        produces.push({
          name: af.name,
          type: 'Consuming Workflow',
          targetFileId: af.id
        });
      }
    }

    // 3. Find other files sharing the base prefix (e.g. CoolingSystem...)
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

  return (
    <div className="flex-1 overflow-y-auto py-2 px-3 space-y-4 text-xs select-none">
      {/* REQUIRES SECTION */}
      <div>
        <div className="text-[10px] font-bold text-gray-400 uppercase tracking-wider mb-1.5 flex items-center justify-between">
          <span>REQUIRES / USES</span>
          <span className="font-mono text-gray-500">{relations.requires.length}</span>
        </div>
        {relations.requires.length === 0 ? (
          <p className="text-[11px] text-gray-500 italic pl-1">No explicit dependency declarations.</p>
        ) : (
          <div className="space-y-1">
            {relations.requires.map((req, idx) => (
              <div
                key={idx}
                onClick={() => req.targetFileId && handleOpenRelatedFile(req.targetFileId)}
                className={`flex items-center justify-between p-1.5 rounded transition ${
                  req.targetFileId 
                    ? 'hover:bg-gray-800/80 cursor-pointer text-gray-200' 
                    : 'text-gray-400 bg-gray-900/40'
                }`}
              >
                <div className="flex items-center gap-2 overflow-hidden">
                  {req.type === 'capability' ? (
                    <Cpu size={13} className="text-purple-400 shrink-0" />
                  ) : (
                    <Settings size={13} className="text-amber-400 shrink-0" />
                  )}
                  <span className="truncate font-medium">{req.name}</span>
                </div>
                {req.targetFileId && (
                  <ArrowUpRight size={12} className="text-gray-500 shrink-0" />
                )}
              </div>
            ))}
          </div>
        )}
      </div>

      {/* PRODUCES SECTION */}
      {relations.produces.length > 0 && (
        <div className="border-t border-gray-800/60 pt-3">
          <div className="text-[10px] font-bold text-gray-400 uppercase tracking-wider mb-1.5 flex items-center justify-between">
            <span>PRODUCES</span>
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

      {/* GENERATES SECTION */}
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
    </div>
  );
};
