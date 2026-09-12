import React, { useState, useEffect } from 'react';
import Editor from '@monaco-editor/react';
import { useEditorStore } from '../../stores/editorStore';
import { 
  exportPython, exportRos2, exportJava, exportPlc, exportCpp, exportDsl, exportJson, downloadZipBundle 
} from '../../api/export';
import { generatorsApi, GeneratorInfo, GeneratorTemplateInfo } from '../../api/generators';
import { 
  Code, Download, Copy, Check, FileCode, Cpu, Layers, 
  Sparkles, Play, Loader2, Wrench, Plus, Eye, BookOpen
} from 'lucide-react';

interface Props {
  onClose?: () => void;
  isEmbedded?: boolean;
}

type TargetTab = 'python' | 'ros2' | 'java' | 'plc' | 'cpp' | 'custom' | 'mnc' | 'json';
type ViewMode = 'output' | 'template';

export const CodeGenerationStudio: React.FC<Props> = ({ onClose, isEmbedded = false }) => {
  const { transformResult, projectName, projectId, setActiveView } = useEditorStore();
  const [activeTab, setActiveTab] = useState<TargetTab>('python');
  const [viewMode, setViewMode] = useState<ViewMode>('output');
  
  const [outputContent, setOutputContent] = useState<string>('');
  const [templateContent, setTemplateContent] = useState<string>('');
  const [templateMeta, setTemplateMeta] = useState<GeneratorTemplateInfo | null>(null);
  
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [copied, setCopied] = useState<boolean>(false);
  const [isDownloading, setIsDownloading] = useState<boolean>(false);

  // Custom generator states
  const [generators, setGenerators] = useState<GeneratorInfo[]>([]);
  const [selectedGeneratorId, setSelectedGeneratorId] = useState<string>('python');
  const [customGenName, setCustomGenName] = useState<string>('');
  const [customScriptCode, setCustomScriptCode] = useState<string>('');
  const [isCreatingGen, setIsCreatingGen] = useState<boolean>(false);
  const [isExecutingGen, setIsExecutingGen] = useState<boolean>(false);
  const [genStatus, setGenStatus] = useState<string | null>(null);

  const model = transformResult?.model;
  const modelName = model?.name || projectName || 'Supervisor';

  // Load custom generators list
  useEffect(() => {
    if (!projectId) return;
    const loadGens = async () => {
      try {
        const list = await generatorsApi.listGenerators(projectId);
        setGenerators(list);
      } catch (err) {
        console.error('Failed to load generators:', err);
      }
    };
    loadGens();
  }, [projectId]);

  // Load code and template source for active tab
  useEffect(() => {
    if (!model && activeTab !== 'custom') return;

    const loadData = async () => {
      setIsLoading(true);
      try {
        // 1. Generate Target Code
        let output = '';
        if (model) {
          if (activeTab === 'python') output = await exportPython(model);
          else if (activeTab === 'ros2') output = await exportRos2(model);
          else if (activeTab === 'java') output = await exportJava(model);
          else if (activeTab === 'plc') output = await exportPlc(model);
          else if (activeTab === 'cpp') output = await exportCpp(model);
          else if (activeTab === 'mnc') output = await exportDsl(model);
          else if (activeTab === 'json') output = await exportJson(model);
          else if (activeTab === 'custom' && projectId) {
            const res = await generatorsApi.generateCode(projectId, { generator_id: selectedGeneratorId });
            const files = Object.entries(res.files);
            output = files.length > 0 ? files[0][1] : '// No custom generator output generated';
          }
        }
        setOutputContent(output);

        // 2. Fetch Generator Template
        if (projectId) {
          const targetGen = activeTab === 'plc' ? 'plc_st' : activeTab;
          try {
            const tpl = await generatorsApi.getTemplateSource(projectId, targetGen);
            setTemplateMeta(tpl);
            setTemplateContent(tpl.template_source || '');
            if (activeTab === 'custom' && !customScriptCode) {
              setCustomScriptCode(tpl.template_source || '');
            }
          } catch (e) {
            // Fallback for custom
            setTemplateMeta(null);
            setTemplateContent('// Template source unavailable');
          }
        }
      } catch (err: any) {
        console.error('Generation error:', err);
        setOutputContent(`// Error generating target: ${err?.message || err}`);
      } finally {
        setIsLoading(false);
      }
    };

    loadData();
  }, [activeTab, model, selectedGeneratorId, projectId]);

  const handleCopy = () => {
    const textToCopy = viewMode === 'output' ? outputContent : templateContent;
    navigator.clipboard.writeText(textToCopy);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleDownloadFile = () => {
    const content = viewMode === 'output' ? outputContent : templateContent;
    const ext = viewMode === 'output' 
      ? (activeTab === 'python' ? '.py' : activeTab === 'ros2' ? '_node.py' : activeTab === 'java' ? '.java' : activeTab === 'plc' ? '.st' : activeTab === 'cpp' ? '.hpp' : '.txt')
      : '.template.py';
    const filename = `${modelName}_${activeTab}${ext}`;
    const blob = new Blob([content], { type: 'text/plain;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
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
      const res = await generatorsApi.createGenerator(projectId, { 
        name: customGenName, 
        code: customScriptCode || undefined 
      });
      setGenStatus(`Created custom generator: ${res.name}`);
      setCustomGenName('');
      const list = await generatorsApi.listGenerators(projectId);
      setGenerators(list);
    } catch (err: any) {
      setGenStatus(`Error: ${err.message || err}`);
    } finally {
      setIsCreatingGen(false);
    }
  };

  const handleRunCustomGenerator = async () => {
    if (!projectId) return;
    setIsExecutingGen(true);
    setGenStatus(null);
    try {
      const res = await generatorsApi.generateCode(projectId, { 
        generator_id: selectedGeneratorId, 
        save_to_project: true 
      });
      const entries = Object.entries(res.files);
      if (entries.length > 0) {
        setOutputContent(entries[0][1]);
        setGenStatus(`Executed successfully! Emitted ${entries.length} file(s): ${entries.map(e => e[0]).join(', ')}`);
        setViewMode('output');
      }
    } catch (err: any) {
      setGenStatus(`Execution error: ${err.message || err}`);
    } finally {
      setIsExecutingGen(false);
    }
  };

  const getLanguage = () => {
    if (viewMode === 'template') {
      return templateMeta?.language || 'python';
    }
    if (activeTab === 'python' || activeTab === 'ros2') return 'python';
    if (activeTab === 'java') return 'java';
    if (activeTab === 'plc') return 'text';
    if (activeTab === 'cpp') return 'cpp';
    if (activeTab === 'json') return 'json';
    return 'plaintext';
  };

  return (
    <div className={`flex flex-col h-full bg-[#0b0f19] text-slate-100 ${isEmbedded ? '' : 'border border-slate-700 rounded-xl overflow-hidden shadow-2xl'}`}>
      
      {/* Top Header */}
      <div className="flex flex-wrap items-center justify-between px-4 py-3 bg-[#111827] border-b border-gray-800 gap-3">
        <div className="flex items-center gap-3">
          <div className="p-2 bg-indigo-600/20 text-indigo-400 rounded-lg border border-indigo-500/30">
            <Sparkles className="w-5 h-5" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h2 className="text-base font-bold text-white">Code Generation Studio & Templates</h2>
              <span className="text-[11px] bg-indigo-500/20 text-indigo-300 px-2 py-0.5 rounded font-mono font-semibold border border-indigo-500/30">
                {modelName}
              </span>
            </div>
            <p className="text-xs text-slate-400">
              Inspect generator templates & synthesize production-ready controllers (Python, ROS2, Java, PLC ST, C++)
            </p>
          </div>
        </div>

        {/* Global Actions */}
        <div className="flex items-center gap-2">
          <button
            onClick={() => setActiveView('simulator')}
            className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg text-xs font-semibold flex items-center gap-1.5 transition shadow"
            title="Launch interactive digital twin controller runner"
          >
            <Play className="w-3.5 h-3.5" />
            <span>Launch Live Runner</span>
          </button>
          
          <button
            onClick={handleDownloadZip}
            disabled={isDownloading || !model}
            className="px-3 py-1.5 bg-blue-600 hover:bg-blue-500 disabled:opacity-50 text-white rounded-lg text-xs font-semibold flex items-center gap-1.5 transition shadow"
          >
            {isDownloading ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <Download className="w-3.5 h-3.5" />}
            <span>Export Suite (.ZIP)</span>
          </button>

          {onClose && (
            <button 
              onClick={onClose}
              className="p-1.5 text-slate-400 hover:text-white hover:bg-slate-800 rounded-lg transition ml-2"
            >
              ✕
            </button>
          )}
        </div>
      </div>

      {/* Target Selector & View Mode Switcher */}
      <div className="flex flex-wrap items-center justify-between px-4 py-2 bg-[#0f172a] border-b border-gray-800 text-xs gap-2">
        {/* Target Tabs */}
        <div className="flex items-center gap-1 overflow-x-auto">
          {[
            { id: 'python', label: 'Python Controller', icon: FileCode },
            { id: 'ros2', label: 'ROS2 (rclpy)', icon: Cpu },
            { id: 'java', label: 'Java (Eclipse)', icon: Code },
            { id: 'plc', label: 'PLC (IEC 61131-3)', icon: Layers },
            { id: 'cpp', label: 'Embedded C++', icon: Cpu },
            { id: 'custom', label: 'Custom (.generator.py)', icon: Wrench },
            { id: 'mnc', label: 'MNC Spec', icon: FileCode },
            { id: 'json', label: 'JSON IR', icon: FileCode },
          ].map((tab) => {
            const Icon = tab.icon;
            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id as TargetTab)}
                className={`px-3 py-1.5 rounded-md flex items-center gap-1.5 transition font-mono whitespace-nowrap ${
                  activeTab === tab.id
                    ? 'bg-indigo-600 text-white font-semibold shadow'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
                }`}
              >
                <Icon className="w-3.5 h-3.5" />
                <span>{tab.label}</span>
              </button>
            );
          })}
        </div>

        {/* View Mode Switcher: Generated Output vs Generator Template */}
        <div className="flex items-center gap-2">
          <div className="flex bg-slate-900 p-0.5 rounded-lg border border-slate-700">
            <button
              onClick={() => setViewMode('output')}
              className={`flex items-center gap-1 px-2.5 py-1 rounded text-xs font-semibold transition ${
                viewMode === 'output' ? 'bg-indigo-600 text-white shadow' : 'text-slate-400 hover:text-white'
              }`}
            >
              <Eye className="w-3.5 h-3.5" />
              <span>Generated Code</span>
            </button>
            <button
              onClick={() => setViewMode('template')}
              className={`flex items-center gap-1 px-2.5 py-1 rounded text-xs font-semibold transition ${
                viewMode === 'template' ? 'bg-amber-600 text-white shadow' : 'text-slate-400 hover:text-white'
              }`}
              title="Inspect the Generator Template & Transformation Rules"
            >
              <BookOpen className="w-3.5 h-3.5" />
              <span>Generator Template</span>
            </button>
          </div>

          <button
            onClick={handleCopy}
            className="px-2.5 py-1 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded text-xs flex items-center gap-1 transition"
          >
            {copied ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
            <span>{copied ? 'Copied' : 'Copy'}</span>
          </button>
          
          <button
            onClick={handleDownloadFile}
            className="px-2.5 py-1 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded text-xs flex items-center gap-1 transition"
          >
            <Download className="w-3.5 h-3.5" />
            <span>Save File</span>
          </button>
        </div>
      </div>

      {/* Template Metadata Banner (When viewing template) */}
      {viewMode === 'template' && templateMeta && (
        <div className="px-4 py-2 bg-amber-950/30 border-b border-amber-800/40 text-xs flex flex-wrap items-center justify-between gap-2">
          <div className="flex items-center gap-2">
            <span className="font-bold text-amber-400">Template Target:</span>
            <span className="text-slate-200 font-semibold">{templateMeta.name}</span>
            <span className="text-slate-400 text-[11px]">&bull; {templateMeta.description}</span>
          </div>
          <div className="flex items-center gap-2 text-[11px]">
            <span className="text-amber-300 font-mono bg-amber-900/40 px-2 py-0.5 rounded border border-amber-800/50">
              Ref: {templateMeta.thesis_module}
            </span>
          </div>
        </div>
      )}

      {/* Custom Generator Controls (When on Custom tab) */}
      {activeTab === 'custom' && (
        <div className="p-3 bg-slate-900 border-b border-slate-800 flex flex-wrap items-center justify-between gap-3 text-xs">
          <div className="flex items-center gap-2">
            <span className="text-slate-400 font-semibold">Active Generator:</span>
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
            <button
              onClick={handleRunCustomGenerator}
              disabled={isExecutingGen}
              className="px-3 py-1 bg-emerald-600 hover:bg-emerald-500 text-white rounded text-xs font-semibold flex items-center gap-1 transition shadow"
            >
              {isExecutingGen ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <Play className="w-3.5 h-3.5" />}
              Execute Generator
            </button>
          </div>

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
              Save As Project Script
            </button>
          </div>
        </div>
      )}

      {genStatus && (
        <div className="px-4 py-1.5 bg-indigo-950/60 text-indigo-300 text-xs border-b border-indigo-800/40 font-mono flex items-center justify-between">
          <span>{genStatus}</span>
          <button onClick={() => setGenStatus(null)} className="text-slate-400 hover:text-white">✕</button>
        </div>
      )}

      {/* Main Monaco Editor for Code / Template */}
      <div className="flex-1 relative overflow-hidden">
        {isLoading && (
          <div className="absolute inset-0 z-20 flex items-center justify-center bg-slate-950/70 backdrop-blur-xs">
            <Loader2 className="w-6 h-6 animate-spin text-indigo-400" />
            <span className="ml-2 text-xs text-slate-300">Compiling target architecture...</span>
          </div>
        )}

        <Editor
          height="100%"
          language={getLanguage()}
          value={viewMode === 'output' ? outputContent : templateContent}
          theme="vs-dark"
          options={{
            readOnly: viewMode === 'output',
            minimap: { enabled: false },
            fontSize: 12,
            lineNumbers: 'on',
            scrollBeyondLastLine: false,
            wordWrap: 'on'
          }}
        />
      </div>

      {/* Footer Info */}
      <div className="px-4 py-2 bg-[#0d131f] border-t border-gray-800 flex items-center justify-between text-[11px] text-slate-400 font-mono">
        <span>Active Target: {activeTab.toUpperCase()} ({viewMode === 'output' ? 'Synthesized Source' : 'Xtext / AbstractGenerator Template'})</span>
        <span>Thesis Module: GenerateMnCDesignFromActivityDiagram &bull; IFileSystemAccess2</span>
      </div>
    </div>
  );
};
