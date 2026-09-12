import React, { useState } from 'react';
import { useEditorStore } from '../../stores/editorStore';
import { CollapsibleSection } from './CollapsibleSection';
import { 
  FileCode2, Sparkles, Download, CheckCircle2, 
  ArrowRight
} from 'lucide-react';

export const GenerateInspector: React.FC = () => {
  const { setActiveView } = useEditorStore();
  const [selectedTarget, setSelectedTarget] = useState<string>('python');

  const targets = [
    { id: 'python', name: 'Python Controller', ext: '.py', runtime: 'Python 3.11+ (asyncio)', files: ['controller.py', 'requirements.txt', 'README.md'] },
    { id: 'ros2', name: 'ROS2 Supervisory Node', ext: '.py', runtime: 'ROS2 Humble / Iron', files: ['ros2_supervisor.py', 'package.xml', 'launch.py'] },
    { id: 'plc_st', name: 'PLC (IEC 61131-3)', ext: '.st', runtime: 'Structured Text IEC 61131-3', files: ['supervisor_plc.st'] },
    { id: 'java', name: 'Java Controller', ext: '.java', runtime: 'Java 17 / Maven', files: ['SupervisorController.java', 'pom.xml'] },
    { id: 'cpp', name: 'Embedded C++', ext: '.hpp', runtime: 'C++17 (Header-Only)', files: ['supervisor_controller.hpp', 'CMakeLists.txt'] },
  ];

  const current = targets.find(t => t.id === selectedTarget) || targets[0];

  return (
    <div className="flex-1 flex flex-col min-h-0 overflow-y-auto select-none">
      {/* Target Selector Bar */}
      <div className="p-3 border-b border-gray-800 bg-[#111622]/40">
        <div className="text-xs font-bold text-gray-200 uppercase tracking-wider mb-1 flex items-center gap-1.5">
          <Sparkles size={14} className="text-blue-400" />
          <span>Synthesis Target</span>
        </div>
        <div className="grid grid-cols-2 gap-1 mt-2">
          {targets.map(t => (
            <button
              key={t.id}
              onClick={() => setSelectedTarget(t.id)}
              className={`p-1.5 rounded text-left text-xs font-medium transition border ${
                selectedTarget === t.id
                  ? 'bg-blue-600/20 text-blue-300 border-blue-500/50'
                  : 'bg-gray-900 text-gray-400 border-gray-800 hover:text-gray-200'
              }`}
            >
              <div className="truncate font-semibold">{t.name}</div>
              <div className="text-[10px] text-gray-500 font-mono">{t.ext}</div>
            </button>
          ))}
        </div>
      </div>

      {/* Target Status & Runtime */}
      <div className="p-3 border-b border-gray-800/60 bg-gray-900/20 text-xs">
        <div className="flex items-center gap-1.5 text-emerald-400 font-semibold mb-1">
          <CheckCircle2 size={13} />
          <span>Formally Verified Synthesis Ready</span>
        </div>
        <div className="font-mono text-[11px] text-gray-400">
          Runtime: {current.runtime}
        </div>
      </div>

      {/* Model Lineage */}
      <CollapsibleSection
        title="Model Lineage"
        icon={<ArrowRight size={13} className="text-indigo-400" />}
        hideIfZero={false}
      >
        <div className="p-2 rounded bg-gray-900 border border-gray-800 space-y-2 mt-1 text-xs font-mono">
          <div className="flex items-center gap-2 text-gray-300">
            <span className="w-1.5 h-1.5 rounded-full bg-orange-400" />
            <span>Process Workflow (.activity)</span>
          </div>
          <div className="text-gray-600 pl-2">&darr; automated synthesis</div>
          <div className="flex items-center gap-2 text-gray-300">
            <span className="w-1.5 h-1.5 rounded-full bg-rose-400" />
            <span>Supervisory Automata (MNC-ML)</span>
          </div>
          <div className="text-gray-600 pl-2">&darr; target code generation</div>
          <div className="flex items-center gap-2 text-blue-300 font-bold">
            <span className="w-1.5 h-1.5 rounded-full bg-blue-400" />
            <span>{current.name}</span>
          </div>
        </div>
      </CollapsibleSection>

      {/* Generated Files */}
      <CollapsibleSection
        title="Artifact Files"
        count={current.files.length}
        icon={<FileCode2 size={13} className="text-cyan-400" />}
        hideIfZero={false}
      >
        <div className="space-y-1 mt-1 font-mono text-xs">
          {current.files.map((file, idx) => (
            <div key={idx} className="p-1.5 rounded bg-gray-900 border border-gray-800 flex items-center justify-between text-gray-300">
              <span>{file}</span>
              <span className="text-[10px] text-gray-500">Verified</span>
            </div>
          ))}
        </div>
      </CollapsibleSection>

      {/* Actions */}
      <div className="p-3 mt-auto border-t border-gray-800 space-y-1.5">
        <div className="text-[10px] uppercase font-bold text-gray-500 tracking-wider mb-1">
          Actions
        </div>
        <button
          onClick={() => setActiveView('codegen')}
          className="w-full py-1.5 px-2.5 bg-blue-600 hover:bg-blue-500 text-white rounded text-xs font-semibold flex items-center justify-center gap-1.5 transition shadow"
        >
          <Download size={13} />
          <span>Save & Download Files</span>
        </button>
        <button
          onClick={() => setActiveView('editor')}
          className="w-full py-1 px-2.5 bg-gray-800 hover:bg-gray-700 text-gray-300 rounded text-xs transition"
        >
          Open in Code Editor
        </button>
      </div>
    </div>
  );
};
