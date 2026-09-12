import React, { useState, useEffect } from 'react';
import Editor from '@monaco-editor/react';
import { useEditorStore } from '../../stores/editorStore';
import { 
  exportPython, exportRos2, exportJava, exportPlc, exportCpp, exportDsl, exportJson, downloadZipBundle 
} from '../../api/export';
import { generatorsApi, GeneratorInfo, GeneratorTemplateInfo } from '../../api/generators';
import { 
  Code, Download, Copy, Check, FileCode, Cpu, Layers, 
  Play, Loader2, Wrench, Plus, Eye, BookOpen, 
  FileCheck
} from 'lucide-react';

interface Props {
  onClose?: () => void;
  isEmbedded?: boolean;
}

type TargetTab = 'python' | 'ros2' | 'java' | 'plc' | 'cpp' | 'custom' | 'mnc' | 'json';
type ViewMode = 'output' | 'template';

export const CodeGenerationStudio: React.FC<Props> = () => {
  const { transformResult, projectName, projectId } = useEditorStore();
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
            setTemplateMeta(null);
            setTemplateContent('// Template source unavailable');
          }
        }
      } catch (err: any) {
        console.error('Error fetching generated code/template:', err);
        setOutputContent(`// Error generating code: ${err.message || err}`);
      } finally {
        setIsLoading(false);
      }
    };

    loadData();
  }, [activeTab, model, projectId, selectedGeneratorId]);

  const handleCopy = () => {
    const textToCopy = viewMode === 'template' ? templateContent : outputContent;
    navigator.clipboard.writeText(textToCopy);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleDownloadFile = () => {
    const targetMap: Record<string, { ext: string; mime: string }> = {
      python: { ext: '_controller.py', mime: 'text/x-python' },
      ros2: { ext: '_ros2_node.py', mime: 'text/x-python' },
      java: { ext: 'SupervisorController.java', mime: 'text/x-java-source' },
      plc: { ext: '_plc.st', mime: 'text/plain' },
      cpp: { ext: '_controller.hpp', mime: 'text/x-c++hdr' },
      mnc: { ext: '.mncspec', mime: 'text/plain' },
      json: { ext: '.json', mime: 'application/json' },
      custom: { ext: '_custom.txt', mime: 'text/plain' }
    };
    const target = targetMap[activeTab] || { ext: '.txt', mime: 'text/plain' };
    const filename = `${modelName}${target.ext}`;
    const textToSave = viewMode === 'template' ? templateContent : outputContent;
    
    const blob = new Blob([textToSave], { type: target.mime });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = viewMode === 'template' ? `${activeTab}_generator_template.py` : filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  };

  const handleDownloadZip = async () => {
    if (!model) return;
    setIsDownloading(true);
    try {
      await downloadZipBundle(model);
    } catch (err) {
      console.error('Failed to download ZIP bundle:', err);
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
      setGenStatus(`Created generator: ${res.name}`);
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
        setGenStatus(`Generated ${entries.length} file(s): ${entries.map(e => e[0]).join(', ')}`);
        setViewMode('output');
      }
    } catch (err: any) {
      setGenStatus(`Execution error: ${err.message || err}`);
    } finally {
      setIsExecutingGen(false);
    }
  };

  const getLanguage = () => {
    if (viewMode === 'template') return templateMeta?.language || 'python';
    if (activeTab === 'python' || activeTab === 'ros2') return 'python';
    if (activeTab === 'java') return 'java';
    if (activeTab === 'plc') return 'text';
    if (activeTab === 'cpp') return 'cpp';
    if (activeTab === 'json') return 'json';
    return 'plaintext';
  };

  const targetList = [
    { id: 'python', label: 'Python Controller', sub: 'Async supervisory event loop', icon: FileCode },
    { id: 'ros2', label: 'ROS2 (rclpy) Node', sub: 'Lifecycle node & topics', icon: Cpu },
    { id: 'java', label: 'Java Controller', sub: 'Eclipse Xtext specification', icon: Code },
    { id: 'plc', label: 'PLC (IEC 61131-3)', sub: 'Structured text FUNCTION_BLOCK', icon: Layers },
    { id: 'cpp', label: 'Embedded C++', sub: 'FreeRTOS / Arduino class', icon: Cpu },
    { id: 'mnc', label: 'MNC-ML Formal Spec', sub: 'Supervisory thesis syntax', icon: FileCheck },
    { id: 'custom', label: 'Custom (.generator.py)', sub: 'Custom project script', icon: Wrench },
  ];

  return (
    <div className="w-full h-full flex bg-[#0b0f19] text-gray-200 overflow-hidden">
      
      {/* LEFT SIDEBAR: Generation Targets (Sleek Linear Style) */}
      <aside className="w-64 bg-[#0d121d] border-r border-gray-800 flex flex-col justify-between shrink-0 select-none">
        
        {/* Target Header */}
        <div className="p-3 border-b border-gray-800/80">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold uppercase tracking-wider text-gray-400">Target Controllers</span>
            <span className="text-[10px] font-mono bg-blue-900/40 text-blue-300 px-2 py-0.5 rounded border border-blue-700/30">
              {modelName}
            </span>
          </div>
          <p className="text-[11px] text-gray-500 mt-0.5">Select a target compiler or custom generator script</p>
        </div>

        {/* Target Options List */}
        <div className="p-2 space-y-1 overflow-y-auto flex-1 scrollbar-thin scrollbar-thumb-gray-800">
          {targetList.map((target) => {
            const Icon = target.icon;
            const isSelected = activeTab === target.id;
            return (
              <button
                key={target.id}
                onClick={() => setActiveTab(target.id as TargetTab)}
                className={`w-full text-left p-2.5 rounded-lg flex items-start gap-2.5 transition-all group ${
                  isSelected
                    ? 'bg-blue-600/20 text-blue-300 border border-blue-500/40 shadow-sm'
                    : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50 border border-transparent'
                }`}
              >
                <div className={`p-1.5 rounded-md mt-0.5 ${
                  isSelected ? 'bg-blue-600 text-white' : 'bg-gray-800 text-gray-400 group-hover:text-gray-200'
                }`}>
                  <Icon className="w-3.5 h-3.5" />
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-semibold truncate">{target.label}</span>
                    {isSelected && <span className="w-1.5 h-1.5 rounded-full bg-blue-400"></span>}
                  </div>
                  <p className="text-[10px] text-gray-500 truncate mt-0.5">{target.sub}</p>
                </div>
              </button>
            );
          })}
        </div>

        {/* Sidebar Footer Actions */}
        <div className="p-3 border-t border-gray-800/80 bg-[#0a0e17] space-y-2">
          <button
            onClick={handleDownloadZip}
            disabled={isDownloading || !model}
            className="w-full py-2 px-3 bg-gray-800 hover:bg-gray-700 text-gray-200 rounded-lg text-xs font-medium flex items-center justify-center gap-2 border border-gray-700 transition"
          >
            {isDownloading ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <Download className="w-3.5 h-3.5 text-blue-400" />}
            <span>Export Suite (.ZIP)</span>
          </button>
        </div>
      </aside>

      {/* RIGHT MAIN WORKSPACE: Monaco Editor & Compact Header */}
      <main className="flex-1 flex flex-col min-w-0 bg-[#0b0f19]">
        
        {/* Compact Workspace Header Bar */}
        <div className="h-11 bg-[#111622] border-b border-gray-800 px-4 flex items-center justify-between text-xs select-none gap-3">
          
          {/* Left: View Mode Segmented Pill */}
          <div className="flex items-center gap-2">
            <div className="flex bg-[#1a2333] p-0.5 rounded-lg border border-gray-700/60">
              <button
                onClick={() => setViewMode('output')}
                className={`flex items-center gap-1.5 px-3 py-1 rounded-md text-xs font-semibold transition ${
                  viewMode === 'output' ? 'bg-blue-600 text-white shadow-sm' : 'text-gray-400 hover:text-white'
                }`}
              >
                <Eye className="w-3.5 h-3.5" />
                <span>Generated Output</span>
              </button>
              <button
                onClick={() => setViewMode('template')}
                className={`flex items-center gap-1.5 px-3 py-1 rounded-md text-xs font-semibold transition ${
                  viewMode === 'template' ? 'bg-amber-600 text-white shadow-sm' : 'text-gray-400 hover:text-white'
                }`}
                title="View the underlying generator template implementation"
              >
                <BookOpen className="w-3.5 h-3.5" />
                <span>Generator Template</span>
              </button>
            </div>

            {/* Template Thesis Reference Pill */}
            {viewMode === 'template' && templateMeta?.thesis_module && (
              <span className="hidden md:inline-flex text-[11px] text-amber-300 font-mono bg-amber-950/40 px-2.5 py-0.5 rounded-full border border-amber-800/50">
                Thesis Ref: {templateMeta.thesis_module}
              </span>
            )}
          </div>

          {/* Right: Actions */}
          <div className="flex items-center gap-2">
            {activeTab === 'custom' && (
              <button
                onClick={handleRunCustomGenerator}
                disabled={isExecutingGen}
                className="flex items-center gap-1.5 px-3 py-1 bg-emerald-600 hover:bg-emerald-500 text-white rounded-md text-xs font-semibold transition shadow-sm"
              >
                {isExecutingGen ? <Loader2 className="w-3 h-3 animate-spin" /> : <Play className="w-3 h-3" />}
                <span>Run Generator</span>
              </button>
            )}

            <button
              onClick={handleCopy}
              className="flex items-center gap-1.5 px-2.5 py-1 bg-gray-800 hover:bg-gray-700 text-gray-300 hover:text-white rounded-md text-xs border border-gray-700 transition"
              title="Copy code to clipboard"
            >
              {copied ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
              <span>{copied ? 'Copied' : 'Copy'}</span>
            </button>

            <button
              onClick={handleDownloadFile}
              className="flex items-center gap-1.5 px-2.5 py-1 bg-gray-800 hover:bg-gray-700 text-gray-300 hover:text-white rounded-md text-xs border border-gray-700 transition"
              title="Download this file"
            >
              <Download className="w-3.5 h-3.5" />
              <span>Save File</span>
            </button>
          </div>
        </div>

        {/* Custom Generator Quick Creation Banner (only when on custom tab) */}
        {activeTab === 'custom' && (
          <div className="px-4 py-2 bg-indigo-950/30 border-b border-indigo-900/40 flex flex-wrap items-center justify-between text-xs gap-3">
            <div className="flex items-center gap-2">
              <span className="text-gray-400 font-medium">Select Script:</span>
              <select
                value={selectedGeneratorId}
                onChange={(e) => setSelectedGeneratorId(e.target.value)}
                className="bg-gray-900 border border-gray-700 rounded px-2 py-1 text-xs text-white focus:outline-none focus:border-blue-500"
              >
                {generators.map((g) => (
                  <option key={g.id} value={g.id}>
                    {g.name} {g.builtin ? '(Builtin)' : '(Project Script)'}
                  </option>
                ))}
              </select>
            </div>

            <div className="flex items-center gap-2 flex-1 max-w-sm">
              <span className="text-gray-400 font-medium">New:</span>
              <input
                type="text"
                placeholder="e.g. Safety_Audit.generator.py"
                value={customGenName}
                onChange={(e) => setCustomGenName(e.target.value)}
                className="flex-1 bg-gray-900 border border-gray-700 rounded px-2 py-1 text-xs text-white focus:outline-none focus:border-blue-500"
              />
              <button
                onClick={handleCreateCustomGenerator}
                disabled={isCreatingGen || !customGenName.trim()}
                className="px-2.5 py-1 bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50 text-white rounded text-xs font-semibold flex items-center gap-1 transition"
              >
                {isCreatingGen ? <Loader2 className="w-3 h-3 animate-spin" /> : <Plus className="w-3 h-3" />}
                <span>Add</span>
              </button>
            </div>
            {genStatus && (
              <span className="text-[11px] text-indigo-300 font-mono truncate">{genStatus}</span>
            )}
          </div>
        )}

        {/* Monaco Editor Container */}
        <div className="flex-1 relative overflow-hidden">
          {isLoading ? (
            <div className="absolute inset-0 flex flex-col items-center justify-center bg-[#0b0f19] text-gray-400 gap-2 z-10">
              <Loader2 className="w-6 h-6 animate-spin text-blue-500" />
              <span className="text-xs">Generating code...</span>
            </div>
          ) : null}

          <Editor
            height="100%"
            language={getLanguage()}
            value={viewMode === 'template' ? templateContent : outputContent}
            theme="vs-dark"
            options={{
              readOnly: viewMode !== 'template' && activeTab !== 'custom',
              minimap: { enabled: false },
              fontSize: 13,
              fontFamily: "'JetBrains Mono', 'Fira Code', Menlo, monospace",
              scrollBeyondLastLine: false,
              automaticLayout: true,
              tabSize: 4,
              padding: { top: 12, bottom: 12 },
              lineNumbers: 'on',
              folding: true,
              wordWrap: 'on'
            }}
          />
        </div>
      </main>
    </div>
  );
};
