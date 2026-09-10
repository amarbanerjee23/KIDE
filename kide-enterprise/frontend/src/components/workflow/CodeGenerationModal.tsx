import React, { useState, useEffect } from 'react';
import Editor from '@monaco-editor/react';
import { useEditorStore } from '../../stores/editorStore';
import { 
  exportPython, exportRos2, exportJava, exportPlc, exportCpp, exportDsl, exportJson, downloadZipBundle 
} from '../../api/export';
import { generatorsApi, GeneratorInfo } from '../../api/generators';
import { LiveRunnerConsole } from '../simulation/LiveRunnerConsole';
import { 
  Code, Download, Copy, Check, FileCode, Cpu, Layers, 
  Sparkles, X, Play, Loader2, Wrench, Plus
} from 'lucide-react';

interface CodeGenerationModalProps {
  isOpen: boolean;
  onClose: () => void;
}

type TabType = 'python' | 'ros2' | 'java' | 'plc' | 'cpp' | 'custom' | 'simulation' | 'mnc' | 'json';

export const CodeGenerationModal: React.FC<CodeGenerationModalProps> = ({ isOpen, onClose }) => {
  const { transformResult, projectName, projectId } = useEditorStore();
  const [activeTab, setActiveTab] = useState<TabType>('python');
  const [codeContent, setCodeContent] = useState<string>('');
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [copied, setCopied] = useState<boolean>(false);
  const [isDownloading, setIsDownloading] = useState<boolean>(false);

  // Custom generators state
  const [generators, setGenerators] = useState<GeneratorInfo[]>([]);
  const [selectedGeneratorId, setSelectedGeneratorId] = useState<string>('python');
  const [customGenName, setCustomGenName] = useState<string>('');
  const [isCreatingGen, setIsCreatingGen] = useState<boolean>(false);
  const [genStatus, setGenStatus] = useState<string | null>(null);

  const model = transformResult?.model;
  const modelName = model?.name || projectName || 'Supervisor';

  // Load custom generators list when modal opens
  useEffect(() => {
    if (!isOpen || !projectId) return;
    const loadGens = async () => {
      try {
        const list = await generatorsApi.listGenerators(projectId);
        setGenerators(list);
      } catch (err) {
        console.error('Failed to load project generators:', err);
      }
    };
    loadGens();
  }, [isOpen, projectId]);

  // Load code content for current tab
  useEffect(() => {
    if (!isOpen || !model) return;
    if (activeTab === 'simulation') return;

    const loadCode = async () => {
      setIsLoading(true);
      try {
        let content = '';
        if (activeTab === 'python') content = await exportPython(model);
        else if (activeTab === 'ros2') content = await exportRos2(model);
        else if (activeTab === 'java') content = await exportJava(model);
        else if (activeTab === 'plc') content = await exportPlc(model);
        else if (activeTab === 'cpp') content = await exportCpp(model);
        else if (activeTab === 'mnc') content = await exportDsl(model);
        else if (activeTab === 'json') content = await exportJson(model);
        else if (activeTab === 'custom') {
          if (projectId) {
            const res = await generatorsApi.generateCode(projectId, { generator_id: selectedGeneratorId });
            const files = Object.entries(res.files);
            content = files.length > 0 ? files[0][1] : '// No output generated';
          }
        }
        setCodeContent(content);
      } catch (err) {
        console.error('Code generation error:', err);
        setCodeContent(`// Error generating code: ${err}`);
      } finally {
        setIsLoading(false);
      }
    };

    loadCode();
  }, [activeTab, isOpen, model, selectedGeneratorId, projectId]);

  const handleCopy = () => {
    navigator.clipboard.writeText(codeContent);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleDownloadZip = async () => {
    if (!model) return;
    setIsDownloading(true);
    try {
      await downloadZipBundle(model, `${modelName}_controller_suite.zip`);
    } catch (err) {
      console.error('Download error:', err);
    } finally {
      setIsDownloading(false);
    }
  };

  const handleCreateCustomGenerator = async () => {
    if (!projectId || !customGenName.trim()) return;
    setIsCreatingGen(true);
    setGenStatus(null);
    try {
      const res = await generatorsApi.createGenerator(projectId, { name: customGenName });
      setGenStatus(`Created ${res.name}!`);
      setCustomGenName('');
      const list = await generatorsApi.listGenerators(projectId);
      setGenerators(list);
    } catch (err: any) {
      setGenStatus(`Error: ${err.message || err}`);
    } finally {
      setIsCreatingGen(false);
    }
  };

  if (!isOpen) return null;

  const getLanguage = () => {
    if (activeTab === 'python') return 'python';
    if (activeTab === 'ros2') return 'python';
    if (activeTab === 'java') return 'java';
    if (activeTab === 'plc') return 'text';
    if (activeTab === 'cpp') return 'cpp';
    if (activeTab === 'json') return 'json';
    return 'plaintext';
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 animate-in fade-in duration-200">
      <div className="bg-slate-900 border border-slate-700 rounded-xl shadow-2xl w-full max-w-6xl h-[90vh] flex flex-col overflow-hidden text-slate-100">
        
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-800 bg-slate-900/80">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-indigo-500/20 text-indigo-400 rounded-lg border border-indigo-500/30">
              <Sparkles className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-lg font-semibold flex items-center gap-2">
                Code Generation & Live Execution Studio
                <span className="text-xs bg-indigo-500/20 text-indigo-300 px-2 py-0.5 rounded border border-indigo-500/30 font-mono">
                  {modelName}
                </span>
              </h2>
              <p className="text-xs text-slate-400">
                Production-grade multi-target synthesizers and live in-browser controller execution
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={handleDownloadZip}
              disabled={isDownloading || !model}
              className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-500 disabled:opacity-50 text-white rounded-lg text-xs font-semibold flex items-center gap-1.5 shadow-lg shadow-emerald-500/20 transition"
            >
              {isDownloading ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <Download className="w-3.5 h-3.5" />}
              Export All Targets (ZIP)
            </button>
            <button 
              onClick={onClose}
              className="p-1.5 text-slate-400 hover:text-white hover:bg-slate-800 rounded-lg transition"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Tab Navigation */}
        <div className="flex items-center justify-between px-6 bg-slate-950/60 border-b border-slate-800 text-xs overflow-x-auto">
          <div className="flex items-center gap-1 py-2">
            <button
              onClick={() => setActiveTab('python')}
              className={`px-3 py-1.5 rounded-md flex items-center gap-1.5 transition font-mono ${
                activeTab === 'python' ? 'bg-indigo-600 text-white font-semibold' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <FileCode className="w-3.5 h-3.5" /> Python Controller
            </button>
            <button
              onClick={() => setActiveTab('ros2')}
              className={`px-3 py-1.5 rounded-md flex items-center gap-1.5 transition font-mono ${
                activeTab === 'ros2' ? 'bg-indigo-600 text-white font-semibold' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <Cpu className="w-3.5 h-3.5" /> ROS2 (rclpy)
            </button>
            <button
              onClick={() => setActiveTab('java')}
              className={`px-3 py-1.5 rounded-md flex items-center gap-1.5 transition font-mono ${
                activeTab === 'java' ? 'bg-indigo-600 text-white font-semibold' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <Code className="w-3.5 h-3.5" /> Java (Eclipse)
            </button>
            <button
              onClick={() => setActiveTab('plc')}
              className={`px-3 py-1.5 rounded-md flex items-center gap-1.5 transition font-mono ${
                activeTab === 'plc' ? 'bg-indigo-600 text-white font-semibold' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <Layers className="w-3.5 h-3.5" /> PLC (IEC 61131-3)
            </button>
            <button
              onClick={() => setActiveTab('cpp')}
              className={`px-3 py-1.5 rounded-md flex items-center gap-1.5 transition font-mono ${
                activeTab === 'cpp' ? 'bg-indigo-600 text-white font-semibold' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <Cpu className="w-3.5 h-3.5" /> Embedded C++
            </button>
            <button
              onClick={() => setActiveTab('custom')}
              className={`px-3 py-1.5 rounded-md flex items-center gap-1.5 transition font-mono ${
                activeTab === 'custom' ? 'bg-indigo-600 text-white font-semibold' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <Wrench className="w-3.5 h-3.5" /> Custom Generator
            </button>
            <button
              onClick={() => setActiveTab('simulation')}
              className={`px-3 py-1.5 rounded-md flex items-center gap-1.5 transition font-mono ${
                activeTab === 'simulation' ? 'bg-emerald-600 text-white font-semibold' : 'text-emerald-400 hover:text-emerald-300 font-medium'
              }`}
            >
              <Play className="w-3.5 h-3.5" /> Live Controller Runner
            </button>
            <button
              onClick={() => setActiveTab('mnc')}
              className={`px-3 py-1.5 rounded-md flex items-center gap-1.5 transition font-mono ${
                activeTab === 'mnc' ? 'bg-indigo-600 text-white font-semibold' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              MNC-ML
            </button>
            <button
              onClick={() => setActiveTab('json')}
              className={`px-3 py-1.5 rounded-md flex items-center gap-1.5 transition font-mono ${
                activeTab === 'json' ? 'bg-indigo-600 text-white font-semibold' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              JSON IR
            </button>
          </div>

          {activeTab !== 'simulation' && (
            <button
              onClick={handleCopy}
              className="px-2.5 py-1 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded text-xs flex items-center gap-1 transition"
            >
              {copied ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
              {copied ? 'Copied' : 'Copy'}
            </button>
          )}
        </div>

        {/* Content Body */}
        <div className="relative flex-1 flex flex-col min-h-0 bg-slate-950">
          {isLoading && (
            <div className="absolute inset-0 z-20 flex items-center justify-center bg-slate-950/70 backdrop-blur-xs">
              <Loader2 className="w-6 h-6 animate-spin text-indigo-400" />
              <span className="ml-2 text-xs text-slate-300">Generating code...</span>
            </div>
          )}
          {activeTab === 'simulation' ? (
            <div className="flex-1 p-4 overflow-hidden">
              {projectId ? (
                <LiveRunnerConsole projectId={projectId} />
              ) : (
                <div className="flex items-center justify-center h-full text-slate-500 text-xs">
                  Please select or open a project to start live simulation.
                </div>
              )}
            </div>
          ) : activeTab === 'custom' ? (
            <div className="flex-1 flex flex-col overflow-hidden">
              {/* Custom Generator Toolbar */}
              <div className="p-3 bg-slate-900 border-b border-slate-800 flex flex-wrap items-center justify-between gap-3 text-xs">
                <div className="flex items-center gap-2">
                  <span className="text-slate-400 font-semibold">Select Generator:</span>
                  <select
                    value={selectedGeneratorId}
                    onChange={(e) => setSelectedGeneratorId(e.target.value)}
                    className="bg-slate-800 border border-slate-700 rounded px-2.5 py-1 text-slate-200 font-mono text-xs focus:outline-none focus:border-indigo-500"
                  >
                    {generators.map(g => (
                      <option key={g.id} value={g.id}>
                        {g.name} {g.builtin ? '(Builtin)' : '(Project Script)'}
                      </option>
                    ))}
                  </select>
                </div>

                {/* Create Custom Generator Box */}
                <div className="flex items-center gap-2">
                  <input
                    type="text"
                    placeholder="NewGeneratorName"
                    value={customGenName}
                    onChange={(e) => setCustomGenName(e.target.value)}
                    className="bg-slate-800 border border-slate-700 rounded px-2.5 py-1 text-xs text-slate-200 font-mono focus:outline-none focus:border-indigo-500 w-44"
                  />
                  <button
                    onClick={handleCreateCustomGenerator}
                    disabled={isCreatingGen || !customGenName.trim()}
                    className="px-3 py-1 bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50 text-white rounded text-xs font-semibold flex items-center gap-1 transition"
                  >
                    <Plus className="w-3.5 h-3.5" />
                    + Add to Project
                  </button>
                </div>
              </div>

              {genStatus && (
                <div className="px-4 py-1.5 bg-indigo-950/40 text-indigo-300 text-xs border-b border-indigo-800/40 font-mono">
                  {genStatus}
                </div>
              )}

              {/* Editor displaying output */}
              <div className="flex-1 min-h-0">
                <Editor
                  height="100%"
                  language={getLanguage()}
                  value={codeContent}
                  theme="vs-dark"
                  options={{
                    readOnly: true,
                    minimap: { enabled: false },
                    fontSize: 12,
                    lineNumbers: 'on',
                    scrollBeyondLastLine: false,
                    wordWrap: 'on'
                  }}
                />
              </div>
            </div>
          ) : (
            <div className="flex-1 min-h-0">
              <Editor
                height="100%"
                language={getLanguage()}
                value={codeContent}
                theme="vs-dark"
                options={{
                  readOnly: true,
                  minimap: { enabled: false },
                  fontSize: 12,
                  lineNumbers: 'on',
                  scrollBeyondLastLine: false,
                  wordWrap: 'on'
                }}
              />
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="px-6 py-2 bg-slate-950 border-t border-slate-800 flex items-center justify-between text-[11px] text-slate-500 font-mono">
          <span>Target Architecture: {activeTab.toUpperCase()}</span>
          <span>KIDE Synthesis Engine &bull; Thesis Compliant (GenerateMnCDesignFromActivityDiagram)</span>
        </div>
      </div>
    </div>
  );
};
