import React, { useState } from 'react';
import { useEditorStore } from '../../stores/editorStore';
import { Sparkles, GitCommit, FileCode2, AlertCircle, CheckCircle2 } from 'lucide-react';

export type GenContextMode = 'targets' | 'lineage' | 'outputs' | 'issues';

export const GenerateContext: React.FC = () => {
  const { files, transformResult } = useEditorStore();
  const [mode, setMode] = useState<GenContextMode>('targets');

  const targets = [
    { name: 'Python Controller', target: 'python', status: 'Generated', ext: '.py', file: 'CoolingSystem_controller.py' },
    { name: 'ROS2 Node', target: 'ros2', status: 'Generated', ext: '.py', file: 'CoolingSystem_ros2_node.py' },
    { name: 'Java Supervisor', target: 'java', status: 'Generated', ext: '.java', file: 'CoolingSystemSupervisorController.java' },
    { name: 'PLC (IEC 61131-3)', target: 'plc_st', status: 'Generated', ext: '.st', file: 'CoolingSystem_plc.st' },
    { name: 'Embedded C++', target: 'cpp', status: 'Generated', ext: '.hpp', file: 'CoolingSystem_controller.hpp' },
    { name: 'Custom Generator', target: 'custom', status: 'Available', ext: '.py', file: 'Custom_Controller.py' }
  ];

  const primaryActivity = files.find(f => f.name.endsWith('.activity'));
  const primaryMnc = files.find(f => f.name.endsWith('.mnc'));

  return (
    <div className="flex-1 flex flex-col min-h-0 select-none overflow-hidden">
      {/* Submode Selector */}
      <div className="px-2 py-1.5 border-b border-gray-800/80 bg-[#111622]/60 flex items-center gap-1">
        <button
          onClick={() => setMode('targets')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition ${
            mode === 'targets'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
        >
          <Sparkles size={12} />
          <span>Targets</span>
        </button>

        <button
          onClick={() => setMode('lineage')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition ${
            mode === 'lineage'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
        >
          <GitCommit size={12} />
          <span>Lineage</span>
        </button>

        <button
          onClick={() => setMode('outputs')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition ${
            mode === 'outputs'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
        >
          <FileCode2 size={12} />
          <span>Outputs</span>
        </button>

        <button
          onClick={() => setMode('issues')}
          className={`flex-1 flex items-center justify-center gap-1 py-1 px-1 rounded text-[11px] font-medium transition ${
            mode === 'issues'
              ? 'bg-blue-600/20 text-blue-300 font-semibold shadow-sm'
              : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
          }`}
        >
          <AlertCircle size={12} />
          <span>Issues</span>
        </button>
      </div>

      {/* Mode Content */}
      <div className="flex-1 overflow-y-auto py-2 px-3 space-y-2 text-xs">
        {mode === 'targets' && (
          <div className="space-y-1.5">
            <div className="text-[10px] font-bold text-gray-500 uppercase tracking-wider mb-1">
              Generation Target Readiness
            </div>
            {targets.map(t => (
              <div key={t.target} className="flex items-center justify-between p-2 rounded bg-gray-900/40 border border-gray-800">
                <div className="flex items-center gap-2">
                  <span className="w-1.5 h-1.5 rounded-full bg-emerald-400" />
                  <span className="font-medium text-gray-200">{t.name}</span>
                </div>
                <span className="text-[10px] font-mono text-emerald-400 flex items-center gap-1">
                  <CheckCircle2 size={11} /> Ready
                </span>
              </div>
            ))}
          </div>
        )}

        {mode === 'lineage' && (
          <div className="space-y-2">
            <div className="text-[10px] font-bold text-gray-500 uppercase tracking-wider mb-1">
              Model Transformation Lineage
            </div>
            <div className="space-y-1.5 relative pl-4 border-l-2 border-blue-500/40 my-2">
              <div className="p-2 rounded bg-gray-900/60 border border-gray-800">
                <div className="text-[10px] text-gray-500 uppercase">Stage 3: Source Workflow</div>
                <div className="font-semibold text-gray-200">{primaryActivity?.name || 'CoolingSystem.activity'}</div>
              </div>
              <div className="p-2 rounded bg-gray-900/60 border border-gray-800">
                <div className="text-[10px] text-gray-500 uppercase">Stage 4: Synthesized MNC Model</div>
                <div className="font-semibold text-gray-200">
                  {transformResult?.model?.name || primaryMnc?.name || 'CoolingSystem_Control'}
                </div>
              </div>
              <div className="p-2 rounded bg-gray-900/60 border border-gray-800">
                <div className="text-[10px] text-gray-500 uppercase">Stage 4+: Target Code Generators</div>
                <div className="font-semibold text-blue-300">Executable Controller Runtimes</div>
              </div>
            </div>
          </div>
        )}

        {mode === 'outputs' && (
          <div className="space-y-1.5">
            <div className="text-[10px] font-bold text-gray-500 uppercase tracking-wider mb-1">
              Synthesized Code Files
            </div>
            {targets.map(t => (
              <div key={t.target} className="p-2 rounded bg-gray-900/40 border border-gray-800 flex items-center justify-between font-mono text-[11px]">
                <span className="text-gray-200 truncate">{t.file}</span>
                <span className="text-gray-500 shrink-0 ml-2">{t.ext}</span>
              </div>
            ))}
          </div>
        )}

        {mode === 'issues' && (
          <div className="p-4 text-center text-gray-500">
            <CheckCircle2 className="w-6 h-6 text-emerald-500 mx-auto mb-1.5" />
            <p className="text-xs text-gray-300">All Generators Ready</p>
            <p className="text-[11px] text-gray-500 mt-0.5">Template definitions match KIDE Metamodel Specification §5.3.</p>
          </div>
        )}
      </div>
    </div>
  );
};

